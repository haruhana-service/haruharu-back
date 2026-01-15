package org.kwakmunsu.haruhana.domain.member.controller;

import static org.kwakmunsu.haruhana.global.support.error.ErrorType.BAD_REQUEST;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.DEFAULT_ERROR;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.DUPLICATE_LOGIN_ID;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.DUPLICATE_NICKNAME;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.FORBIDDEN_ERROR;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.NOT_FOUND_CATEGORY;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.NOT_FOUND_MEMBER;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.UNAUTHORIZED_ERROR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.kwakmunsu.haruhana.domain.member.controller.dto.MemberCreateRequest;
import org.kwakmunsu.haruhana.domain.member.controller.dto.PreferenceRegisterRequest;
import org.kwakmunsu.haruhana.domain.member.service.MemberProfileResponse;
import org.kwakmunsu.haruhana.global.annotation.LoginMember;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

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
            summary = "회원 학습 정보 등록 - JWT [O]",
            description = """
                    ### 회원의 학습 정보를 등록합니다.
                    - 카테고리 주제 ID와 난이도를 포함한 요청을 받습니다.
                    - 회원가입을 마친 GUEST 회원이 자신의 학습 정보를 등록할 때 사용합니다.
                    - GUEST 회원만 접근할 수 있습니다.
                    - 첫 등록 시 스트릭이 초기화됩니다,
                    - 첫 등록 시 오늘의 문제가 생성됩니다.
                    - 성공 시 빈 응답을 반환합니다.
                    - 첫 등록 시 ROLE_MEMBER로 권한이 변경됩니다.
                    """
    )
    @ApiExceptions(values = {
            BAD_REQUEST,
            FORBIDDEN_ERROR,
            NOT_FOUND_CATEGORY,
            DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<?>> registerPreference(
            PreferenceRegisterRequest request,
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