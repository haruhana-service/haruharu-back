package org.kwakmunsu.haruhana.domain.streak.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.category.CategoryFactory;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.streak.entity.Streak;
import org.kwakmunsu.haruhana.domain.streak.repository.StreakJpaRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

/**
 * StreakService 통합 테스트
 * - @Retryable이 실제로 작동하는지 검증
 * - Spring AOP 프록시를 통한 재시도 메커니즘 검증
 */
@RequiredArgsConstructor
class StreakServiceIntegrationTest extends IntegrationTestSupport {

    final CategoryFactory categoryFactory;
    final MemberJpaRepository memberJpaRepository;
    final StreakJpaRepository streakJpaRepository;
    final StreakService streakService;

    @MockitoSpyBean
    StreakManager streakManager;

    Member member;
    Streak streak;

    @BeforeEach
    void setUp() {
        categoryFactory.deleteAll();
        categoryFactory.saveAll();

        member = memberJpaRepository.save(Member.createMember("testUser", "password123!", "테스트유저", Role.ROLE_MEMBER));
        streak = streakJpaRepository.save(Streak.create(member));
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 데이터 정리
        streakJpaRepository.deleteAll();
        memberJpaRepository.deleteAll();
    }

    @Test
    void 정상적으로_스트릭을_증가시킨다() {
        // when
        streakService.increaseWithRetry(member.getId());

        // then
        Streak updatedStreak = streakJpaRepository.findByMemberIdAndStatus(member.getId(), EntityStatus.ACTIVE).orElseThrow();

        assertThat(updatedStreak.getCurrentStreak()).isEqualTo(1L);
        verify(streakManager, times(1)).increase(member.getId());
    }

    @Test
    void OptimisticLockException_발생_시_재시도한다() {
        // given - 첫 번째는 실패, 두 번째는 성공
        willThrow(OptimisticLockException.class)
                .willCallRealMethod()
                .given(streakManager).increase(member.getId());

        // when
        streakService.increaseWithRetry(member.getId());

        // then - 2회 호출되어야 함
        verify(streakManager, times(2)).increase(member.getId());

        // 최종적으로 스트릭이 증가했는지 확인
        Streak updatedStreak = streakJpaRepository.findByMemberIdAndStatus(member.getId(), EntityStatus.ACTIVE).orElseThrow();
        assertThat(updatedStreak.getCurrentStreak()).isEqualTo(1L);
    }

    @Test
    void OptimisticLockingFailureException_발생_시_재시도한다() {
        // given
        willThrow(OptimisticLockingFailureException.class)
                .willCallRealMethod()
                .given(streakManager).increase(member.getId());

        // when
        streakService.increaseWithRetry(member.getId());

        // then
        verify(streakManager, times(2)).increase(member.getId());

        Streak updatedStreak = streakJpaRepository.findByMemberIdAndStatus(member.getId(), EntityStatus.ACTIVE).orElseThrow();
        assertThat(updatedStreak.getCurrentStreak()).isEqualTo(1L);
    }

    @Test
    void 최대_재시도_횟수_3회_초과_시_예외가_발생한다() {
        // given - 3번 모두 실패하도록 설정
        willThrow(OptimisticLockException.class)
                .given(streakManager).increase(member.getId());

        // when & then
        assertThatThrownBy(() -> streakService.increaseWithRetry(member.getId()))
                .isInstanceOf(OptimisticLockException.class);

        // 정확히 3회 시도했는지 확인
        verify(streakManager, times(3)).increase(member.getId());

        // 스트릭이 증가하지 않았는지 확인
        Streak unchangedStreak = streakJpaRepository.findByMemberIdAndStatus(member.getId(), EntityStatus.ACTIVE).orElseThrow();
        assertThat(unchangedStreak.getCurrentStreak()).isZero();
    }

    @Test
    void 다른_예외는_재시도하지_않는다() {
        // given - RuntimeException은 재시도 대상이 아님
        willThrow(new RuntimeException("다른 예외"))
                .given(streakManager).increase(member.getId());

        // when & then
        assertThatThrownBy(() -> streakService.increaseWithRetry(member.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("다른 예외");

        // 1번만 호출되어야 함 (재시도 안함)
        verify(streakManager, times(1)).increase(member.getId());
    }

    @Test
    void 여러_번_충돌_후_성공하면_최종_결과가_정상이다() {
        // given - 2번 실패 후 성공
        doThrow(new OptimisticLockException("충돌 1"))
                .doThrow(new OptimisticLockException("충돌 2"))
                .doCallRealMethod()
                .when(streakManager).increase(member.getId());

        // when
        streakService.increaseWithRetry(member.getId());

        // then
        verify(streakManager, times(3)).increase(member.getId());

        Streak updatedStreak = streakJpaRepository.findByMemberIdAndStatus(member.getId(), EntityStatus.ACTIVE).orElseThrow();
        assertThat(updatedStreak.getCurrentStreak()).isEqualTo(1L);
    }

}