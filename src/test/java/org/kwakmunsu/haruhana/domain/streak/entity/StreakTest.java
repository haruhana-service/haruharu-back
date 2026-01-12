package org.kwakmunsu.haruhana.domain.streak.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.enums.Role;

class StreakTest {

    Streak streak;

    @BeforeEach
    void setUp() {
        streak = Streak.create(MemberFixture.createMember(Role.ROLE_MEMBER));
    }

    @Test
    void 스트릭을_생성한다() {
        // then
        assertThat(streak).extracting(
                Streak::getCurrentStreak,
                Streak::getMaxStreak,
                Streak::getLastSolvedAt
        ).containsExactly(
                0L,
                0L,
                null
        );
    }

    @Test
    void 스트릭을_증가시킨다() {
        // given
        long streakCnt = streak.getCurrentStreak();

        // when
        streak.increase();

        // then
        assertThat(streak.getCurrentStreak()).isEqualTo(streakCnt + 1);
    }

    @Test
    void 스트릭을_리셋한다() {
        // given
        streak.increase();
        assertThat(streak.getCurrentStreak()).isNotZero();

        // when
        streak.reset();

        // then
        assertThat(streak.getCurrentStreak()).isZero();
    }

}