package org.kwakmunsu.haruhana.admin.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.kwakmunsu.haruhana.admin.member.controller.dto.MemberUpdateRoleRequest;
import org.kwakmunsu.haruhana.admin.member.enums.SortBy;
import org.kwakmunsu.haruhana.admin.member.service.dto.AdminMemberPreferenceResponse;
import org.kwakmunsu.haruhana.admin.member.service.dto.AdminMemberPreviewResponse;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.support.response.PageResponse;
import org.kwakmunsu.haruhana.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;

@Tag(name = "Admin - Member", description = "관리자용 Member 관련 API 문서")
public abstract class AdminMemberDocsController {

    @Operation(
            summary = "회원 목록 조회 - 관리자",
            description = """
                    #### 회원 목록을 검색어와 정렬 기준에 따라 조회하는 API입니다.
                    - 관리자는 회원의 로그인 ID 또는 닉네임을 검색하여 회원 목록을 조회할 수 있습니다.
                    - 활성화된 회원과 비활성화된 회원 모두 조회할 수 있습니다.
                    
                    #### Query Parameters
                    - **search**: 회원의 로그인 ID 또는 닉네임을 검색하는 키워드입니다. (예: "john" -> "john123", "john_doe" 등)
                    - **sortBy**: 회원 목록을 정렬하는 기준입니다. (예: "JOIN_ASC" -> 가입일 오름차순, "JOIN_DESC" -> 가입일 내림차순)
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

    @Operation(
            summary = "회원 선호도 조회 - 관리자",
            description = """
                    #### 특정 회원의 선호도를 조회하는 API입니다.
                    - 관리자는 회원의 ID를 통해 해당 회원이 설정한 선호도를 조회할 수 있습니다.
                    - 선호도에는 카테고리 주제, 난이도, 효과 발생 날짜 등의 정보가 포함됩니다.
                    """
    )
    @ApiExceptions(values = {
            ErrorType.DEFAULT_ERROR,
            ErrorType.NOT_FOUND_MEMBER_PREFERENCE,
            ErrorType.UNAUTHORIZED_ERROR
    })
    public abstract ResponseEntity<ApiResponse<AdminMemberPreferenceResponse>> findMemberPreference(
            @Parameter(
                    example = "1",
                    description = "회원 ID",
                    in = ParameterIn.PATH
            )
            Long memberId
    );

    @Operation(
            summary = "회원 역할 수정 - 관리자",
            description = """
                    #### 특정 회원의 역할을 수정하는 API입니다.
                    - 관리자는 회원의 ID와 새로운 역할 정보를 제공하여 해당 회원의 역할을 변경할 수 있습니다.
                    - 역할은 일반 회원(ROLE_MEMBER)과 관리자(ROLE_ADMIN)로 구분됩니다.
                    - 이 API는 성공적으로 역할이 변경되면 204 No Content 상태 코드를 반환합니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "회원 역할이 성공적으로 변경되었습니다."
    )
    @ApiExceptions(values = {
            ErrorType.DEFAULT_ERROR,
            ErrorType.NOT_FOUND_MEMBER,
            ErrorType.UNAUTHORIZED_ERROR
    })
    public abstract ResponseEntity<ApiResponse<?>> updateMemberRole(
            @Parameter(
                    example = "1",
                    description = "회원 ID",
                    in = ParameterIn.PATH
            )
            Long memberId,
            MemberUpdateRoleRequest request
    );

}