package org.kwakmunsu.haruhana.domain.member.controller;

import static org.kwakmunsu.haruhana.global.support.error.ErrorType.BAD_REQUEST;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.DEFAULT_ERROR;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.DUPLICATE_LOGIN_ID;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.DUPLICATE_NICKNAME;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.NOT_FOUND_CATEGORY;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.NOT_FOUND_MEMBER;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.UNAUTHORIZED_ERROR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.kwakmunsu.haruhana.domain.member.controller.dto.MemberCreateRequest;
import org.kwakmunsu.haruhana.domain.member.controller.dto.PreferenceUpdateRequest;
import org.kwakmunsu.haruhana.domain.member.service.MemberProfileResponse;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;

@Tag(name = "Member Docs", description = "Member 관련 API 문서")
public abstract class MemberDocsController {

    @Operation(
            summary = "회원 가입 - JWT [X]",
            description = """
                    ### 새로운 회원을 생성합니다.
                    - 로그인 아이디, 비밀번호, 닉네임을 포함한 요청을 받습니다.
                    - 성공 시 생성된 회원의 ID를 반환합니다.
                    """
    )
    @ApiExceptions(values = {
            BAD_REQUEST,
            DUPLICATE_LOGIN_ID,
            DUPLICATE_NICKNAME,
            DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<Long>> create(MemberCreateRequest request);

    @Operation(
            summary = "회원 학습 정보 수정 - JWT [O]",
            description = """
                    ### 회원의 학습 정보를 수정합니다.
                    - 카테고리 주제 ID와 난이도를 포함한 요청을 받습니다.
                    - 성공 시 빈 응답을 반환합니다.
                    - 변경 사항은 다음 날부터 적용됩니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "학습 정보 수정 성공")
    @ApiExceptions(values = {
            BAD_REQUEST,
            NOT_FOUND_MEMBER,
            NOT_FOUND_CATEGORY,
            DEFAULT_ERROR
    })
    public abstract ResponseEntity<Void> updatePreference(
            PreferenceUpdateRequest request,
            Long memberId
    );

    @Operation(
            summary = "회원 프로필 조회 - JWT [O]",
            description = """
                    ### 회원의 프로필 정보를 조회합니다.
                    - 회원의 ID를 통해 프로필 정보를 조회합니다.
                    - 성공 시 회원의 프로필 정보를 반환합니다.
                    """
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            NOT_FOUND_MEMBER,
            DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<MemberProfileResponse>> getProfile(Long memberId);

}