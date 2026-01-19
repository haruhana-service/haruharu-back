package org.kwakmunsu.haruhana.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.auth.controller.dto.LoginRequest;
import org.kwakmunsu.haruhana.domain.auth.controller.dto.TokenReissueRequest;
import org.kwakmunsu.haruhana.domain.auth.service.AuthService;
import org.kwakmunsu.haruhana.global.annotation.LoginMember;
import org.kwakmunsu.haruhana.global.security.jwt.dto.TokenResponse;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController extends AuthDocsController {

    private final AuthService authService;

    @Override
    @PostMapping("/v1/auth/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request.loginId(), request.password());

        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    @Override
    @PostMapping("/v1/auth/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(@RequestBody @Valid TokenReissueRequest request) {
        TokenResponse response = authService.reissue(request.refreshToken());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    @PostMapping("/v1/auth/logout")
    public ResponseEntity<Void> logout(@LoginMember Long memberId) {
        authService.logout(memberId);

        return ResponseEntity.noContent().build();
    }

}