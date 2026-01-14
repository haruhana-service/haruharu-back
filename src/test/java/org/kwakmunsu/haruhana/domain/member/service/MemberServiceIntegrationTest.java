package org.kwakmunsu.haruhana.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.category.CategoryFactory;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryTopicJpaRepository;
import org.kwakmunsu.haruhana.domain.dailyproblem.repository.DailyProblemJpaRepository;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.member.repository.MemberPreferenceJpaRepository;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewPreference;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;
import org.kwakmunsu.haruhana.domain.problem.repository.ProblemJpaRepository;
import org.kwakmunsu.haruhana.domain.problem.service.ProblemGenerator;
import org.kwakmunsu.haruhana.domain.streak.repository.StreakJpaRepository;
import org.kwakmunsu.haruhana.domain.streak.service.StreakManager;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * MemberService 통합 테스트 - registerPreference 전체 플로우 검증 - 실제 DB 사용 - 이벤트 처리 검증
 */
@RequiredArgsConstructor
@Transactional
class MemberServiceIntegrationTest extends IntegrationTestSupport {

    final CategoryFactory categoryFactory;
    final MemberService memberService;
    final MemberJpaRepository memberJpaRepository;
    final MemberPreferenceJpaRepository memberPreferenceJpaRepository;
    final CategoryTopicJpaRepository categoryTopicJpaRepository;
    final ProblemJpaRepository problemJpaRepository;
    final DailyProblemJpaRepository dailyProblemJpaRepository;
    final StreakJpaRepository streakJpaRepository;

    @MockitoSpyBean
    StreakManager streakManager;

    @MockitoSpyBean
    ProblemGenerator problemGenerator;

    private CategoryTopic categoryTopic;

    @BeforeEach
    void setUp() {
        categoryFactory.deleteAll();
        categoryFactory.saveAll();

        categoryTopic = categoryTopicJpaRepository.findByName("Java")
                .orElseThrow(() -> new RuntimeException("Java 토픽이 존재하지 않습니다"));
    }

    @Test
    void 게스트_회원의_학습_정보를_등록하고_문제를_생성한다() {
        // given
        var guest = memberJpaRepository.save(MemberFixture.createMemberWithOutId(Role.ROLE_GUEST));
        var newPreference = new NewPreference(categoryTopic.getId(), ProblemDifficulty.MEDIUM);

        // ProblemGenerator 모킹 (실제 AI 호출 방지)
        doNothing().when(problemGenerator).generateInitialProblem(any(), any(), any());

        // when
        memberService.registerPreference(newPreference, guest.getId());

        // then
        // 1. 회원 정보 업데이트 확인
        var updatedMember = memberJpaRepository.findById(guest.getId()).orElseThrow();
        assertThat(updatedMember.getRole()).isEqualTo(Role.ROLE_MEMBER);

        // 2. 학습 정보 저장 확인
        var memberPreference = memberPreferenceJpaRepository.findByMemberIdAndStatus(guest.getId(), EntityStatus.ACTIVE)
                .orElseThrow();
        assertThat(memberPreference.getCategoryTopic().getName()).isEqualTo("Java");
        assertThat(memberPreference.getDifficulty()).isEqualTo(ProblemDifficulty.MEDIUM);

        // 3. ProblemGenerator 호출 확인
        verify(problemGenerator, times(1)).generateInitialProblem(
                any(Member.class),
                any(CategoryTopic.class),
                any(ProblemDifficulty.class)
        );

        // 4. 스트릭 생성은 이벤트로 처리됨 (별도 테스트에서 검증)
        // Note: @TransactionalEventListener(AFTER_COMMIT)는 트랜잭션 커밋 후 실행
        // 테스트에서는 롤백되므로 이벤트 발행 안됨 - 이벤트 핸들러는 별도 테스트
    }

    @Test
    void 이미_정회원인_경우_학습_정보_등록에_실패한다() {
        // given
        var member = memberJpaRepository.save(Member.createMember("member", "password123!", "정회원", Role.ROLE_MEMBER));

        var newPreference = new NewPreference(categoryTopic.getId(), ProblemDifficulty.MEDIUM);

        // when & then
        assertThatThrownBy(() -> memberService.registerPreference(newPreference, member.getId()))
                .isInstanceOf(HaruHanaException.class);

        // 학습 정보가 저장되지 않아야 함
        assertThat(memberPreferenceJpaRepository.findByMemberIdAndStatus(
                member.getId(),
                EntityStatus.ACTIVE
        )).isEmpty();
    }

    @Test
    void 존재하지_않는_회원은_학습_정보_등록에_실패한다() {
        // given
        var invalidMemberId = 999L;
        var newPreference = new NewPreference(categoryTopic.getId(), ProblemDifficulty.MEDIUM);

        // when & then
        assertThatThrownBy(() -> memberService.registerPreference(newPreference, invalidMemberId))
                .isInstanceOf(HaruHanaException.class);
    }

    @Test
    @Transactional
    void 여러_난이도로_학습_정보를_등록할_수_있다() {
        // ProblemGenerator 모킹
        doNothing().when(problemGenerator).generateInitialProblem(
                any(Member.class),
                any(CategoryTopic.class),
                any(ProblemDifficulty.class)
        );

        // given - EASY
        var guest1 = memberJpaRepository.save(Member.createMember("guest_easy", "password123!", "게스트Easy", Role.ROLE_GUEST));
        var easyPreference = new NewPreference(categoryTopic.getId(), ProblemDifficulty.EASY);

        // when
        memberService.registerPreference(easyPreference, guest1.getId());

        // then
        var preference1 = memberPreferenceJpaRepository
                .findByMemberIdAndStatus(guest1.getId(), EntityStatus.ACTIVE)
                .orElseThrow();
        assertThat(preference1.getDifficulty()).isEqualTo(ProblemDifficulty.EASY);

        // given - HARD
        var guest2 = memberJpaRepository.save(Member.createMember("guest_hard", "password123!", "게스트Hard", Role.ROLE_GUEST));
        var hardPreference = new NewPreference(categoryTopic.getId(), ProblemDifficulty.HARD);

        // when
        memberService.registerPreference(hardPreference, guest2.getId());

        // then
        var preference2 = memberPreferenceJpaRepository.findByMemberIdAndStatus(guest2.getId(), EntityStatus.ACTIVE)
                .orElseThrow();
        assertThat(preference2.getDifficulty()).isEqualTo(ProblemDifficulty.HARD);

        // ProblemGenerator가 각 난이도로 호출되었는지 검증
        verify(problemGenerator, times(2)).generateInitialProblem(
                any(Member.class),
                any(CategoryTopic.class),
                any(ProblemDifficulty.class)
        );
    }

}