package org.kwakmunsu.haruhana.domain.quote.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.ControllerTestSupport;

class QuoteControllerTest extends ControllerTestSupport {

    @Test
    void 인증_없이_문구_목록을_전체_조회할_수_있다() {
        // given
        List<String> quotes = List.of("오늘도 한 걸음, 같이 가요", "작은 습관이 큰 변화를 만들어요");
        given(quoteService.getQuotes()).willReturn(quotes);

        // when & then
        assertThat(mvcTester.get().uri("/v1/quotes"))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.result", v -> v.assertThat().isEqualTo("SUCCESS"))
                .hasPathSatisfying("$.data", v -> v.assertThat().isEqualTo(quotes));
    }

}