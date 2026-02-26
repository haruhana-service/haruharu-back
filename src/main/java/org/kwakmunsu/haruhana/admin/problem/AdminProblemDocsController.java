package org.kwakmunsu.haruhana.admin.problem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.support.response.PageResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Admin - Problem", description = "관리자용 Problem 관련 API 문서")
public abstract class AdminProblemDocsController {

    @Operation(
            summary = "문제 목록 조회 - 관리자",
            description = """
                    #### 문제 목록을 날짜 기준으로 조회하는 API입니다.
                    - 관리자는 특정 날짜에 등록된 문제 목록을 페이지네이션하여 조회할 수 있습니다.
                    
                    #### Query Parameters
                    - **date**: 문제의 등록 날짜를 기준으로 문제 목록을 조회하는 필터입니다. (예: "2024-06-01")
                    - **page**: 조회할 페이지 번호입니다. (기본값: 1)
                    - **size**: 한 페이지에 포함될 문제 수입니다. (기본값: 20)
                    """
    )

    public abstract ResponseEntity<ApiResponse<PageResponse<AdminProblemPreviewResponse>>> findProblem(
            @Parameter(
                    example = "2024-06-01",
                    in = ParameterIn.QUERY
            )
            LocalDate date,

            @Parameter(
                    example = "1",
                    in = ParameterIn.QUERY
            )
            int page,

            @Parameter(
                    example = "20",
                    in = ParameterIn.QUERY
            )
            int size
    );


}