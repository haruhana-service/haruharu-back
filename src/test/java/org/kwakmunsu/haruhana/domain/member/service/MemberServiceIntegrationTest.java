package org.kwakmunsu.haruhana.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.member.repository.MemberPreferenceJpaRepository;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewPreference;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;
import org.kwakmunsu.haruhana.domain.problem.repository.ProblemJpaRepository;
import org.kwakmunsu.haruhana.domain.problem.service.ProblemGenerator;
import org.kwakmunsu.haruhana.domain.streak.repository.StreakJpaRepository;
import org.kwakmunsu.haruhana.domain.streak.service.StreakManager;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
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
    void 회원가입_시_회원_학습_설정과_스트릭_생성_문제_생성에_성공한다() {
        // given
        var newProfile = MemberFixture.createNewProfile();
        var newPreference = new NewPreference(categoryTopic.getId(), ProblemDifficulty.EASY);

        // when
        Long memberId = memberService.createMember(newProfile, newPreference);

        // then
        assertThat(memberJpaRepository.findById(memberId).orElseThrow()).isNotNull();
        assertThat(
                memberPreferenceJpaRepository.findByMemberIdAndStatus(memberId, EntityStatus.ACTIVE).orElseThrow()).isNotNull();
        assertThat(streakJpaRepository.findByMemberIdAndStatus(memberId, EntityStatus.ACTIVE).orElseThrow()).isNotNull();
        verify(problemGenerator, times(1)).generateInitialProblem(any(), any(), any());
    }

}