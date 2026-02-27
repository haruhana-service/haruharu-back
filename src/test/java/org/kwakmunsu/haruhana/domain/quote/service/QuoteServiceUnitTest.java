package org.kwakmunsu.haruhana.domain.quote.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.UnitTestSupport;
import org.kwakmunsu.haruhana.domain.quote.enums.ChallengeQuote;
import org.mockito.InjectMocks;

class QuoteServiceUnitTest extends UnitTestSupport {

    @InjectMocks
    private QuoteService quoteService;

    @Test
    void 문구_목록은_ChallengeQuote_enum_전체와_일치한다() {
        // given
        List<String> expected = Arrays.stream(ChallengeQuote.values())
                .map(ChallengeQuote::getMessage)
                .toList();

        // when
        List<String> result = quoteService.getQuotes();

        // then
        assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    void 문구_목록은_비어있지_않다() {
        // when
        List<String> result = quoteService.getQuotes();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).allSatisfy(message -> assertThat(message).isNotBlank());
    }

    @Test
    void 문구_목록에_중복이_없다() {
        // when
        List<String> result = quoteService.getQuotes();

        // then
        Set<String> deduplicated = new HashSet<>(result);
        assertThat(result).hasSameSizeAs(deduplicated);
    }

}