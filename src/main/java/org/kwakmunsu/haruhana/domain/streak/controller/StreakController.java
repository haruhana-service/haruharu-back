package org.kwakmunsu.haruhana.domain.streak.controller;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.streak.service.StreakService;
import org.kwakmunsu.haruhana.domain.streak.service.dto.response.StreakResponse;
import org.kwakmunsu.haruhana.global.annotation.LoginMember;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StreakController extends StreakDocsController {

    private final StreakService streakService;

    @Override
    @GetMapping("/v1/streaks")
    public ResponseEntity<ApiResponse<StreakResponse>> getStreak(@LoginMember Long memberId) {
        StreakResponse response = streakService.getStreak(memberId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

}