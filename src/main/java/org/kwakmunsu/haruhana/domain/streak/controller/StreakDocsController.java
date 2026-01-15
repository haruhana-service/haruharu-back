package org.kwakmunsu.haruhana.domain.streak.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.kwakmunsu.haruhana.domain.streak.service.dto.response.StreakResponse;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;

@Tag(name = "Streak Docs", description = "Streak 관련 API 문서")
public abstract class StreakDocsController {

    @Operation(
            summary = "Streak 조회 API - JWT [O]",
            description = """
                    ### 로그인한 사용자의 현재 연속 일수와 최대 연속 일수를 조회합니다.
                    - **현재 연속 일수**: 사용자가 연속으로 목표를 달성한 일수
                    - **최대 연속 일수**: 사용자가 지금까지 달성한 가장 긴 연속 일수
                    """
    )
    @ApiExceptions(values = {
            ErrorType.NOT_FOUND_STREAK,
            ErrorType.FORBIDDEN_ERROR,
            ErrorType.DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<StreakResponse>> getStreak(Long memberId);

}