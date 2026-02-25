package org.kwakmunsu.haruhana.admin.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.kwakmunsu.haruhana.admin.member.enums.SortBy;
import org.kwakmunsu.haruhana.admin.member.service.dto.AdminMemberPreviewResponse;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.support.response.PageResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Admin - Member", description = "관리자용 Member 관련 API 문서")
public abstract class AdminMemberDocsController {

    @Operation(
            summary = "회원 목록 조회 - 관리자",
            description = """
                    #### 회원 목록을 검색어와 정렬 기준에 따라 조회하는 API입니다.
                    - 관리자는 회원의 로그인 ID 또는 닉네임을 검색하여 회원 목록을 조회할 수 있습니다.
                    - 활솽화된 회원과 비활성화된 회원 모두 조회할 수 있습니다.
                    
                    #### Query Parameters
                    - **search**: 회원의 로그인 ID 또는 닉네임을 검색하는 키워드입니다. (예: "john" -> "john123", "john_doe" 등)
                    - **sortBy**: 회원 목록을 정렬하는 기준입니다. (예: "lastLoginAt" -> 최근 로그인 순, "createdAt" -> 가입일 순)
                    - **page**: 조회할 페이지 번호입니다. (기본값: 1)
                    - **size**: 한 페이지에 포함될 회원 수입니다. (기본값: 20)
                    """
    )
    public abstract ResponseEntity<ApiResponse<PageResponse<AdminMemberPreviewResponse>>> findMembers(
            @Parameter(
                    example = "john",
                    in = ParameterIn.QUERY
            )
            String search,
            @Parameter(
                    in = ParameterIn.QUERY
            )
            SortBy sortBy,
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
