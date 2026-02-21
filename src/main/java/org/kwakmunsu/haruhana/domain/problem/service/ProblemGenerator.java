package org.kwakmunsu.haruhana.domain.problem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.dailyproblem.service.DailyProblemManager;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;
import org.kwakmunsu.haruhana.domain.member.service.MemberReader;
import org.kwakmunsu.haruhana.domain.problem.entity.Problem;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;
import org.kwakmunsu.haruhana.domain.problem.repository.ProblemJpaRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.domain.problem.service.dto.ProblemGenerationGroup;
import org.kwakmunsu.haruhana.domain.problem.service.dto.ProblemGenerationKey;
import org.kwakmunsu.haruhana.domain.problem.service.dto.ProblemResponse;
import org.kwakmunsu.haruhana.infrastructure.gemini.ChatService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProblemGenerator {

    private final MemberReader memberReader;
    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final ProblemJpaRepository problemJpaRepository;
    private final DailyProblemManager dailyProblemManager;

    @Transactional
    public void generateProblem(LocalDate targetDate) {
        // 23: 55 실행 되고 익일 날짜 포함해서 그 전날까지 활성화 되어있는 회원 Preference 조회
        List<MemberPreference> memberPreferences = memberReader.getMemberPreferences(targetDate);

        if (memberPreferences.isEmpty()) {
            log.info("[ProblemGenerator] 생성할 문제가 없습니다. 대상 날짜: {}", targetDate);
            return;
        }

        // 카테고리, 난이도 끼리 묶음
        Map<ProblemGenerationKey, List<MemberPreference>> groupedPreferences = groupByTopicAndDifficulty(memberPreferences);

        // 카테고리, 난이도 별로 문제를 생성한다
        List<ProblemGenerationGroup> generationGroups = createGenerationGroups(groupedPreferences);

        for (ProblemGenerationGroup group : generationGroups) {
            try {
                Problem problem = generateAndSaveProblem(group, targetDate);
                dailyProblemManager.assignDailyProblemToMembers(problem, group.members(), targetDate);
            } catch (Exception e) {
                log.error("[ProblemGenerator] 문제 생성 실패 - 카테고리: {}, 난이도: {}", group.key().categoryTopicName(), group.key().difficulty(), e);
                assignBackupProblem(group, targetDate);
            }
        }
    }

    /**
     * 회원의 첫 문제를 생성하고 할당
     * @param member 회원가입 한 첫 회원
     * @param categoryTopic 카테고리 주제
     * @param difficulty 난이도
     *
     * */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateInitialProblem(Member member, CategoryTopic categoryTopic, ProblemDifficulty difficulty) {
        try {
            ProblemResponse problemResponse = getProblemToAi(categoryTopic.getName(), difficulty);

            LocalDate today = LocalDate.now();
            Problem problem = problemJpaRepository.save(Problem.create(
                    problemResponse.title(),
                    problemResponse.description(),
                    problemResponse.aiAnswer(),
                    categoryTopic,
                    difficulty,
                    today,
                    Prompt.V1_PROMPT.name()
            ));
            // 기존 메서드 사용 할려고 그냥 List로 감싸서 보냄
            dailyProblemManager.assignDailyProblemToMembers(problem, List.of(member), today);

            log.info("[ProblemGenerator] 첫 문제 생성 완료 - 카테고리: {}, 난이도: {}, 대상 회원: {}",
                    categoryTopic.getName(),
                    difficulty,
                    member.getId()
            );
        } catch (Exception e) {
            log.error("[ProblemGenerator] 문제 생성 실패 - 카테고리: {}, 난이도: {}", categoryTopic.getName(), difficulty, e);
            problemJpaRepository.findFirstByCategoryTopicIdAndDifficultyAndStatusOrderByProblemAtDesc(
                            categoryTopic.getId(), difficulty, EntityStatus.ACTIVE)
                    .ifPresentOrElse(
                            backup -> {
                                dailyProblemManager.assignDailyProblemToMembers(backup, List.of(member), LocalDate.now());
                                log.info("[ProblemGenerator] 백업 문제 할당 완료 - 회원: {}", member.getId());
                            },
                            () -> log.warn("[ProblemGenerator] 백업 문제 없음 - 카테고리: {}, 난이도: {}",
                                    categoryTopic.getName(), difficulty)
                    );
        }
    }

    /**
     * 카테고리 주제와 난이도별로 회원 학습 정보를 그룹화
     */
    private Map<ProblemGenerationKey, List<MemberPreference>> groupByTopicAndDifficulty(List<MemberPreference> preferences) {
        return preferences.stream()
                .collect(Collectors.groupingBy(preference ->
                        ProblemGenerationKey.of(
                                preference.getCategoryTopic().getId(),
                                preference.getCategoryTopic().getName(),
                                preference.getDifficulty()
                        )
                ));
    }

    /**
     * 그룹화된 데이터를 ProblemGenerationGroup 으로 변환
     */
    private List<ProblemGenerationGroup> createGenerationGroups(
            Map<ProblemGenerationKey, List<MemberPreference>> groupedPreferences) {
        return groupedPreferences.entrySet().stream()
                .map(entry -> {
                    // 첫 번째 Preference 에서 CategoryTopic 가져오기 (모두 동일함)
                    CategoryTopic categoryTopic = entry.getValue().getFirst().getCategoryTopic();

                    return ProblemGenerationGroup.builder()
                            .key(entry.getKey())
                            .categoryTopic(categoryTopic)
                            .members(entry.getValue().stream()
                                    .map(MemberPreference::getMember)
                                    .distinct()
                                    .toList())
                            .build();
                })
                .toList();
    }

    /**
     * 그룹별로 문제 생성 및 저장
     */
    private Problem generateAndSaveProblem(ProblemGenerationGroup group, LocalDate problemAt) throws Exception {
        ProblemGenerationKey key = group.key();

        ProblemResponse problemResponse = getProblemToAi(key.categoryTopicName(), key.difficulty());

        Problem saved = problemJpaRepository.save(Problem.create(
                problemResponse.title(),
                problemResponse.description(),
                problemResponse.aiAnswer(),
                group.categoryTopic(),  // 그룹에 포함된 CategoryTopic 사용
                key.difficulty(),
                problemAt,
                Prompt.V1_PROMPT.name()
        ));

        log.info("[ProblemGenerator] 문제 생성 완료 - 카테고리: {}, 난이도: {}, 대상 회원 수: {}",
                key.categoryTopicName(),
                key.difficulty(),
                group.getMemberCount()
        );

        return saved;
    }

    private ProblemResponse getProblemToAi(String categoryTopicName, ProblemDifficulty difficulty) throws JsonProcessingException {
        String prompt = Prompt.V1_PROMPT.generate(categoryTopicName, difficulty);
        String jsonResponse = chatService.sendPrompt(prompt);

        return objectMapper.readValue(jsonResponse, ProblemResponse.class);
    }

    private void assignBackupProblem(ProblemGenerationGroup group, LocalDate targetDate) {
        problemJpaRepository.findFirstByCategoryTopicIdAndDifficultyAndStatusOrderByProblemAtDesc(
                        group.key().categoryTopicId(), group.key().difficulty(), EntityStatus.ACTIVE
                ).ifPresentOrElse(
                        backup -> {
                            dailyProblemManager.assignDailyProblemToMembers(backup, group.members(), targetDate);
                            log.info("[ProblemGenerator] 백업 문제 할당 완료 - 카테고리: {}, 난이도: {}, 회원 수: {}",
                                    group.key().categoryTopicName(), group.key().difficulty(), group.getMemberCount());
                        },
                        () -> log.warn("[ProblemGenerator] 백업 문제 없음, 할당 생략 - 카테고리: {}, 난이도: {}",
                                group.key().categoryTopicName(), group.key().difficulty())
                );
    }

}