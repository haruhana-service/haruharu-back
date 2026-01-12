package org.kwakmunsu.haruhana.domain.dailyproblem.controller;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.dailyproblem.service.DailyProblemService;
import org.kwakmunsu.haruhana.domain.dailyproblem.service.dto.response.TodayProblemResponse;
import org.kwakmunsu.haruhana.global.annotation.LoginMember;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DailyProblemController extends DailyProblemDocsController {

    private final DailyProblemService dailyProblemService;

    @Override
    @GetMapping("/v1/daily-problem")
    public ResponseEntity<ApiResponse<TodayProblemResponse>> getTodayProblem(@LoginMember Long memberId) {
        TodayProblemResponse response = dailyProblemService.getTodayProblem(memberId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/v1/daily-problem/{dailyProblemId}")
    public ResponseEntity<Void> findDailyProblem(@PathVariable Long dailyProblemId, @LoginMember Long memberId) {
        dailyProblemService.findDailyProblem(dailyProblemId, memberId);
    }

}