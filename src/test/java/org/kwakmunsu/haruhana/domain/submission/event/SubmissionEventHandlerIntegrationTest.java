package org.kwakmunsu.haruhana.domain.submission.event;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.category.CategoryFactory;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryTopicJpaRepository;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.dailyproblem.repository.DailyProblemJpaRepository;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.problem.entity.Problem;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;
import org.kwakmunsu.haruhana.domain.problem.repository.ProblemJpaRepository;
import org.kwakmunsu.haruhana.domain.streak.service.StreakService;
import org.kwakmunsu.haruhana.domain.submission.service.SubmissionService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * SubmissionEventHandler 통합 테스트
 * - 실제 이벤트 발행 및 핸들러 호출 검증
 * - @Async로 인한 비동기 처리 검증
 */
@RequiredArgsConstructor
@Transactional
class SubmissionEventHandlerIntegrationTest extends IntegrationTestSupport {

    final CategoryFactory categoryFactory;
    final CategoryTopicJpaRepository categoryTopicJpaRepository;
    final MemberJpaRepository memberJpaRepository;
    final ProblemJpaRepository problemJpaRepository;
    final DailyProblemJpaRepository dailyProblemJpaRepository;
    final SubmissionService submissionService;

    @MockitoBean
    StreakService streakService;

    @BeforeEach
    void setUp() {
        categoryFactory.deleteAll();
        categoryFactory.saveAll();
    }

    @Test
    void 최초_제출_시_이벤트가_발행되고_스트릭이_증가한다() {
        // given
        var member = memberJpaRepository.save(MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER));

        var categoryTopic = categoryTopicJpaRepository.findByName("Java")
                .orElseThrow(() -> new RuntimeException("Java 토픽이 존재하지 않습니다"));

        var problem = problemJpaRepository.save(Problem.create(
                "테스트 문제",
                "테스트 설명",
                "AI 답변",
                categoryTopic,
                ProblemDifficulty.MEDIUM,
                LocalDate.now(),
                "V1_PROMPT"
        ));

        var dailyProblem = dailyProblemJpaRepository.save(DailyProblem.create(member, problem, LocalDate.now()));

        var userAnswer = "사용자 답변입니다.";

        // when
        submissionService.submitSolution(dailyProblem.getId(), member.getId(), userAnswer);

        // then - @Async로 인한 비동기 처리를 기다림 (최대 5초)
        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() ->
                    verify(streakService, times(1)).increaseWithRetry(member.getId())
                );
    }

    @Test
    void 재제출_시_이벤트는_발행되지만_스트릭은_증가하지_않는다() {
        // given
        var member = memberJpaRepository.save(MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER));

        var categoryTopic = categoryTopicJpaRepository.findByName("Java")
                .orElseThrow(() -> new RuntimeException("Java 토픽이 존재하지 않습니다"));

        var problem = problemJpaRepository.save(Problem.create(
                "테스트 문제",
                "테스트 설명",
                "AI 답변",
                categoryTopic,
                ProblemDifficulty.MEDIUM,
                LocalDate.now(),
                "V1_PROMPT"
        ));

        var dailyProblem = dailyProblemJpaRepository.save(
                DailyProblem.create(member, problem, LocalDate.now())
        );

        var firstAnswer = "첫 번째 답변";
        var secondAnswer = "수정된 답변";

        // when - 첫 번째 제출
        submissionService.submitSolution(dailyProblem.getId(), member.getId(), firstAnswer);

        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() ->
                    verify(streakService, times(1)).increaseWithRetry(member.getId())
                );

        // when - 두 번째 제출 (재제출)
        submissionService.submitSolution(dailyProblem.getId(), member.getId(), secondAnswer);

        // then - 스트릭 증가가 한 번만 호출되어야 함
        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() ->
                    verify(streakService, times(1)).increaseWithRetry(member.getId())
                );
    }

}