package org.kwakmunsu.haruhana.domain.quote.service;

import java.util.Arrays;
import java.util.List;
import org.kwakmunsu.haruhana.domain.quote.enums.ChallengeQuote;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {

    public List<String> getQuotes() {
        return Arrays.stream(ChallengeQuote.values())
                .map(ChallengeQuote::getMessage)
                .toList();
    }

}