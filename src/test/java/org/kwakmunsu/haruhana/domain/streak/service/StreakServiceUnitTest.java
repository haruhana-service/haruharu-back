package org.kwakmunsu.haruhana.domain.streak.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.UnitTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.streak.entity.Streak;
import org.kwakmunsu.haruhana.domain.streak.service.dto.response.StreakResponse;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class StreakServiceUnitTest extends UnitTestSupport {

    @Mock
    StreakReader streakReader;

    @InjectMocks
    StreakService streakService;

    @Test
    void 회원의_스트릭_정보를_조회힌다() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);
        var streak = Streak.create(member);

        given(streakReader.getByMemberId(any())).willReturn(streak);

        // when
        StreakResponse response = streakService.getStreak(member.getId());

        // then
        assertThat(response).isNotNull().extracting(
                StreakResponse::currentStreak,
                StreakResponse::maxStreak
        ).containsExactly(
                streak.getCurrentStreak(),
                streak.getMaxStreak()
        );
    }

    @Test
    void 회원의_스트릭_정보가_없을경우_예외를_반환한다() {
        // given
        willThrow(new HaruHanaException(ErrorType.NOT_FOUND_STREAK))
                .given(streakReader).getByMemberId(any());

        // when & then
        assertThatThrownBy(() -> streakService.getStreak(1L))
            .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.NOT_FOUND_STREAK.getMessage());
    }

}