package org.kwakmunsu.haruhana.domain.streak.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.ControllerTestSupport;
import org.kwakmunsu.haruhana.domain.streak.service.dto.response.StreakResponse;
import org.kwakmunsu.haruhana.security.annotation.TestMember;

class StreakControllerTest extends ControllerTestSupport {

    @TestMember
    @Test
    void 회원의_스트릭정보를_조회_요청한다() {
        // given
        var streakResponse = StreakResponse.builder()
                .currentStreak(5L)
                .maxStreak(10L)
                .build();
        given(streakService.getStreak(any())).willReturn(streakResponse);

        // when & then
        assertThat(mvcTester.get().uri("/v1/streaks"))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("data.currentStreak", v -> v.assertThat().isEqualTo(streakResponse.currentStreak().intValue()))
                .hasPathSatisfying("data.maxStreak", v -> v.assertThat().isEqualTo(streakResponse.maxStreak().intValue()));
    }

}