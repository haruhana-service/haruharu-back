package org.kwakmunsu.haruhana.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.kwakmunsu.haruhana.domain.auth.controller.dto.LoginRequest;
import org.kwakmunsu.haruhana.domain.auth.controller.dto.TokenReissueRequest;
import org.kwakmunsu.haruhana.global.annotation.LoginMember;
import org.kwakmunsu.haruhana.global.security.jwt.dto.TokenResponse;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "Auth 관련 API 문서")
public abstract class AuthDocsController {

    @Operation(
            summary = "로그인 요청 - JWT [X]",
            description = """
                    ### 로그인 API 입니다.
                    - 로그인에 성공하면 access token과 refresh token을 발급합니다.
                    - 발급된 토큰은 이후 인증이 필요한 API 요청 시 사용됩니다.
                    - 요청 시 유효한 로그인 아이디와 비밀번호를 전달해야 합니다.
                    """
    )
    @ApiExceptions(values = {
            ErrorType.BAD_REQUEST,
            ErrorType.NOT_FOUND_MEMBER,
            ErrorType.INVALID_ACCOUNT,
            ErrorType.DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<TokenResponse>> login(LoginRequest request);

    @Operation(
            summary = "토큰 재발급 요청 - JWT [X]",
            description = """
                    ### 토큰 재발급 API 입니다.
                    - 토큰 재발급에 성공하면 access token과 refresh token을 발급합니다.
                    - 발급된 토큰은 이후 인증이 필요한 API 요청 시 사용됩니다.
                    - 회원이 가지고 있던 refreshToken과 요청온 refreshToken이 다르다면 **토큰 탈취 감지** 로 **기존 토큰은 무효화** 하고 예외를 반환합니다.
                    """
    )
    @ApiExceptions(values = {
            ErrorType.BAD_REQUEST,
            ErrorType.NOT_FOUND_MEMBER,
            ErrorType.TOKEN_THEFT_DETECTED,
            ErrorType.DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<TokenResponse>> reissue(
            TokenReissueRequest request,
            Long memberId
    );

}