package org.kwakmunsu.haruhana.domain.dailyproblem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.kwakmunsu.haruhana.domain.dailyproblem.service.dto.response.DailyProblemDetailResponse;
import org.kwakmunsu.haruhana.domain.dailyproblem.service.dto.response.TodayProblemResponse;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;

@Tag(name = "DailyProblem Docs", description = "DailyProblem 관련 API 문서")
public abstract class DailyProblemDocsController {

    @Operation(
            summary = "오늘의 문제 조회 - JWT [O]",
            description = """
                    ### 회원의 오늘의 문제를 조회합니다.
                    - 매일 자정에 새로운 문제가 자동으로 할당됩니다.
                    - 회원이 문제를 푼 경우, isSolved 필드가 true로 표시됩니다.
                    """
    )
    @ApiExceptions(values = {
            ErrorType.FORBIDDEN_ERROR,
            ErrorType.NOT_FOUND_DAILY_PROBLEM,
            ErrorType.UNAUTHORIZED_ERROR,
            ErrorType.DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<TodayProblemResponse>> getTodayProblem(Long memberId);

    @Operation(
            summary = "문제 상세 조회 - JWT [O]",
            description = """
                    ### 문제 상세 정보를 조회합니다.
                    - 문제의 제목, 설명, 난이도, 카테고리 토픽, 제출 기록 등을 포함합니다.
                    - 회원이 문제를 푼 경우, 제출 기록이 함께 반환됩니다.
                    - 제출 기록이 없는 경우 aiAnswer, submittedAt, userAnswer 필드는 null로 표시됩니다.
                    - 요청한 회원이 해당 문제에 접근할 수 없는 경우, 예외가 발생합니다.
                    - 문제를 찾을 수 없는 경우, 예외가 발생합니다.
                    """
    )
    @ApiExceptions(values = {
            ErrorType.FORBIDDEN_ERROR,
            ErrorType.NOT_FOUND_DAILY_PROBLEM,
            ErrorType.UNAUTHORIZED_ERROR,
            ErrorType.DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<DailyProblemDetailResponse>> findDailyProblem(Long dailyProblemId, Long memberId);
}