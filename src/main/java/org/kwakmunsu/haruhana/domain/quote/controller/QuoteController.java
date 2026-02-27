package org.kwakmunsu.haruhana.domain.quote.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.quote.service.QuoteService;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class QuoteController extends QuoteDocsController {

    private final QuoteService quoteService;

    @Override
    @GetMapping("/v1/quotes")
    public ResponseEntity<ApiResponse<List<String>>> getQuotes() {
        List<String> quotes = quoteService.getQuotes();

        return ResponseEntity.ok(ApiResponse.success(quotes));
    }

}