package org.kwakmunsu.haruhana.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.kwakmunsu.haruhana.domain.auth.controller.dto.LoginRequest;
import org.kwakmunsu.haruhana.global.security.jwt.dto.TokenResponse;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;

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

}