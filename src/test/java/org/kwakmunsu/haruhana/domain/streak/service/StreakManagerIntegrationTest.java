package org.kwakmunsu.haruhana.domain.streak.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.streak.entity.Streak;
import org.kwakmunsu.haruhana.domain.streak.repository.StreakJpaRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
class StreakManagerIntegrationTest extends IntegrationTestSupport {

    final MemberJpaRepository memberJpaRepository;
    final StreakJpaRepository streakJpaRepository;
    final StreakManager streakManager;
    final EntityManager entityManager;

    @Test
    void 스트릭을_증가한다() {
        // given
        var member = memberJpaRepository.save(MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER));
        var streak = streakJpaRepository.save(Streak.create(member));

        assertThat(streak.getCurrentStreak()).isZero();

        // when
        streakManager.increase(member.getId());
        entityManager.flush();

        // then
        var updatedStreak = streakJpaRepository.findById(streak.getId()).orElseThrow();

        assertThat(updatedStreak.getCurrentStreak()).isEqualTo(1);
    }

    @Test
    void 스트릭을_리셋한다() {
        // given
        var member = memberJpaRepository.save(MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER));
        var streak = streakJpaRepository.save(Streak.create(member));
        streakManager.increase(member.getId());
        entityManager.flush();
        assertThat(streak.getCurrentStreak()).isEqualTo(1);

        // when
        streakManager.initStreakForMember(member);
        entityManager.flush();

        // then
        var updatedStreak = streakJpaRepository.findById(streak.getId()).orElseThrow();
        assertThat(updatedStreak.getCurrentStreak()).isZero();
    }

    @Test
    void streak이_존재하지_않는_회원_즉_게스트회원_일_경우_예외를_반환한다() {
        // given
        var member = memberJpaRepository.save(MemberFixture.createMemberWithOutId(Role.ROLE_GUEST));

        // when & then
        assertThatThrownBy(() ->streakManager.initStreakForMember(member))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.NOT_FOUND_STREAK.getMessage());
    }

    @Test
    void streak을_생성한다() {
        // given
        var member = MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER);
        memberJpaRepository.save(member);

        // when
        streakManager.create(member);

        // then
        var streak = streakJpaRepository.findByMemberIdAndStatus(member.getId(), EntityStatus.ACTIVE).orElseThrow();
        assertThat(streak).isNotNull().extracting(
                Streak::getMember,
                Streak::getCurrentStreak,
                Streak::getMaxStreak
        ).containsExactly(
                member,
                0L,
                0L
        );

    }

    @Test
    void streak이_이미_존재한다면_아무_일도_일어나지_않는다() {
        // given
        var member = MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER);
        memberJpaRepository.save(member);
        streakManager.create(member);

        // when
        streakManager.create(member);

    }

}