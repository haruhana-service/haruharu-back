package org.kwakmunsu.haruhana.domain.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.ControllerTestSupport;
import org.kwakmunsu.haruhana.domain.auth.controller.dto.LoginRequest;
import org.kwakmunsu.haruhana.domain.auth.controller.dto.TokenReissueRequest;
import org.kwakmunsu.haruhana.global.security.jwt.dto.TokenResponse;
import org.kwakmunsu.haruhana.security.annotation.TestMember;
import org.springframework.http.MediaType;

class AuthControllerTest extends ControllerTestSupport {

    @Test
    void 로그인_API_호출_성공_시_AccessToken과_RefreshToken을_발급한다() throws JsonProcessingException {
        // given
        var request = new LoginRequest("testLoginId", "testPassword123!");
        var requestJson = objectMapper.writeValueAsString(request);
        var tokenResponse = new TokenResponse("testAccessToken", "testRefreshToken");

        given(authService.login(any(), any())).willReturn(tokenResponse);

        // when & then
        assertThat(mvcTester.post().uri("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.data.accessToken", v -> v.assertThat().isEqualTo(tokenResponse.accessToken()))
                .hasPathSatisfying("$.data.refreshToken", v -> v.assertThat().isEqualTo(tokenResponse.refreshToken()));
    }

    @TestMember
    @Test
    void AccessToken과_RefreshToken을_재발급한다() throws JsonProcessingException {
        // given
        var request = new TokenReissueRequest("oldRefreshToken");
        var requestJson = objectMapper.writeValueAsString(request);
        var tokenResponse = new TokenResponse("testAccessToken", "testRefreshToken");

        given(authService.reissue(any(), any())).willReturn(tokenResponse);

        // when & then
        assertThat(mvcTester.post().uri("/v1/auth/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.data.accessToken", v -> v.assertThat().isEqualTo(tokenResponse.accessToken()))
                .hasPathSatisfying("$.data.refreshToken", v -> v.assertThat().isEqualTo(tokenResponse.refreshToken()));
    }

}