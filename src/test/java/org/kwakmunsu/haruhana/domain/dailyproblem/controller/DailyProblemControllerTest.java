package org.kwakmunsu.haruhana.domain.dailyproblem.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.ControllerTestSupport;
import org.kwakmunsu.haruhana.domain.dailyproblem.service.dto.response.DailyProblemResponse;
import org.kwakmunsu.haruhana.security.annotation.TestMember;

class DailyProblemControllerTest extends ControllerTestSupport {

    @TestMember
    @Test
    void 오늘의_문제_Api를_요청한다() {
        // given
        var dailyProblemResponse = new DailyProblemResponse(1L, "문제 제목", "문제 설명", "EASY", "자바", false);

        given(dailyProblemService.getTodayProblem(any())).willReturn(dailyProblemResponse);

        // when & then
        assertThat(mvcTester.get().uri("/v1/daily-problem"))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("data.id", v -> v.assertThat().isEqualTo(dailyProblemResponse.id().intValue()))
                .hasPathSatisfying("data.title", v -> v.assertThat().isEqualTo(dailyProblemResponse.title()))
                .hasPathSatisfying("data.description", v -> v.assertThat().isEqualTo(dailyProblemResponse.description()))
                .hasPathSatisfying("data.difficulty", v -> v.assertThat().isEqualTo(dailyProblemResponse.difficulty()))
                .hasPathSatisfying("data.categoryTopicName", v -> v.assertThat().isEqualTo(dailyProblemResponse.categoryTopicName()))
                .hasPathSatisfying("data.isSolved", v -> v.assertThat().isEqualTo(dailyProblemResponse.isSolved()));
    }

}