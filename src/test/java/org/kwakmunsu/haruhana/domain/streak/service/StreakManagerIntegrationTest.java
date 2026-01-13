package org.kwakmunsu.haruhana.domain.streak.service;

import static org.assertj.core.api.Assertions.*;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.streak.entity.Streak;
import org.kwakmunsu.haruhana.domain.streak.repository.StreakJpaRepository;
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

}