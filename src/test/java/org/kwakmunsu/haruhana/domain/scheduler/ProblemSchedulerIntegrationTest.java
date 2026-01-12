package org.kwakmunsu.haruhana.domain.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.category.CategoryFactory;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryTopicJpaRepository;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.member.repository.MemberPreferenceJpaRepository;
import org.kwakmunsu.haruhana.domain.problem.entity.Problem;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;
import org.kwakmunsu.haruhana.domain.problem.repository.ProblemJpaRepository;
import org.kwakmunsu.haruhana.domain.problem.service.ProblemGenerator;
import org.kwakmunsu.haruhana.infrastructure.gemini.ChatService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
class ProblemSchedulerIntegrationTest extends IntegrationTestSupport {

    final ProblemGenerator problemGenerator;
    final ProblemJpaRepository problemJpaRepository;
    final MemberJpaRepository memberJpaRepository;
    final MemberPreferenceJpaRepository memberPreferenceJpaRepository;
    final CategoryTopicJpaRepository categoryTopicJpaRepository;
    final CategoryFactory categoryFactory;

    @MockitoBean
    ChatService chatService;

    CategoryTopic javaTopic;
    LocalDate targetDate;

    @BeforeEach
    void setUp() {
        // 카테고리 데이터 생성 (필요한 경우에만)
        categoryFactory.deleteAll();
        categoryFactory.saveAll();

        targetDate = LocalDate.now().plusDays(1);

        Member testMember = MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER);
        memberJpaRepository.save(testMember);

        // CategoryTopic 조회 (IntegrationTestSupport 에서 자동 생성됨)
        javaTopic = categoryTopicJpaRepository.findAll().stream()
                .filter(topic -> "Java".equals(topic.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Java CategoryTopic이 생성되지 않았습니다."));

        memberPreferenceJpaRepository.save(MemberPreference.create(
                testMember,
                javaTopic,
                ProblemDifficulty.EASY,
                targetDate
        ));
    }

    @Test
    void 회원_선호도에_맞는_문제가_실제로_생성된다() {
        // given
        long beforeCount = problemJpaRepository.count();

        // AI Mock 설정
        String mockAiResponse = """
                {
                  "title": "Java의 JVM이란?",
                  "description": "Java Virtual Machine(JVM)의 역할과 동작 원리에 대해 설명하세요.",
                  "aiAnswer": "JVM은 자바 바이트코드를 실행하는 가상 머신입니다..."
                }
                """;
        given(chatService.sendPrompt(any())).willReturn(mockAiResponse);

        // when
        problemGenerator.generateProblem(targetDate);

        // then
        long afterCount = problemJpaRepository.count();
        assertThat(afterCount).isEqualTo(beforeCount + 1);

        // 생성된 문제 검증
        List<Problem> problems = problemJpaRepository.findAll();
        assertThat(problems).isNotEmpty();

        Problem createdProblem = problems.getFirst();
        assertThat(createdProblem).isNotNull().extracting(
                Problem::getTitle,
                Problem::getDifficulty,
                problem -> problem.getCategoryTopic().getName(),
                Problem::getProblemAt
        ).containsExactly(
                "Java의 JVM이란?",
                ProblemDifficulty.EASY,
                "Java",
                targetDate
        );
    }

    @Test
    void 여러_회원이_같은_카테고리_난이도를_선택하면_문제는_1개만_생성된다() {
        // given
        // 추가 회원 생성
        var member2 = Member.createMember("user2", "pass", "회원2", Role.ROLE_MEMBER);
        var member3 = Member.createMember("user3", "pass", "회원3", Role.ROLE_MEMBER);
        memberJpaRepository.save(member2);
        memberJpaRepository.save(member3);

        // 같은 선호도 등록
        memberPreferenceJpaRepository.save(MemberPreference.create(member2, javaTopic, ProblemDifficulty.EASY, targetDate));
        memberPreferenceJpaRepository.save(MemberPreference.create(member3, javaTopic, ProblemDifficulty.EASY, targetDate));

        // AI Mock
        var mockAiResponse = """
                {
                  "title": "Java 테스트",
                  "description": "테스트 질문",
                  "aiAnswer": "테스트 답변"
                }
                """;
        given(chatService.sendPrompt(any())).willReturn(mockAiResponse);

        // when
        problemGenerator.generateProblem(targetDate);

        // then - 3명이지만 문제는 1개만 생성
        long problemCount = problemJpaRepository.count();
        assertThat(problemCount).isEqualTo(1);
    }

    @Test
    void 다른_난이도를_선택한_회원에게는_각각_다른_문제가_생성된다() {
        // given
        var hardUser = Member.createMember("hardUser", "pass", "고급회원", Role.ROLE_MEMBER);
        memberJpaRepository.save(hardUser);

        memberPreferenceJpaRepository.save(MemberPreference.create(hardUser, javaTopic, ProblemDifficulty.HARD, targetDate));

        // AI Mock - 호출마다 다른 응답
        given(chatService.sendPrompt(any()))
                .willReturn("""
                        {
                          "title": "초급 문제",
                          "description": "초급 질문",
                          "aiAnswer": "초급 답변"
                        }
                        """)
                .willReturn("""
                        {
                          "title": "고급 문제",
                          "description": "고급 질문",
                          "aiAnswer": "고급 답변"
                        }
                        """);

        // when
        problemGenerator.generateProblem(targetDate);

        // then - EASY 1개 + HARD 1개 = 총 2개
        long problemCount = problemJpaRepository.count();
        assertThat(problemCount).isEqualTo(2);

        // 난이도별 검증
        assertThat(problemJpaRepository.findAll())
                .extracting(Problem::getDifficulty)
                .containsExactlyInAnyOrder(ProblemDifficulty.EASY, ProblemDifficulty.HARD);
    }

    @Test
    void 회원_선호도가_없으면_문제가_생성되지_않는다() {
        // given - 모든 선호도 삭제
        memberPreferenceJpaRepository.deleteAll();

        // when
        problemGenerator.generateProblem(targetDate);

        // then
        long problemCount = problemJpaRepository.count();
        assertThat(problemCount).isZero();
    }

}