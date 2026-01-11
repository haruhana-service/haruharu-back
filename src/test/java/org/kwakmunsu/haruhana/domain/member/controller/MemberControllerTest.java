package org.kwakmunsu.haruhana.domain.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.ControllerTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.security.annotation.TestGuest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class MemberControllerTest extends ControllerTestSupport {

    @Test
    void 회원가입_Api를_요청한다() throws JsonProcessingException {
        // given
        var request = MemberFixture.createMemberCreateRequest();
        var jsonRequest = objectMapper.writeValueAsString(request);

        given(memberService.createMember(any())).willReturn(1L);

        // when & then
        assertThat(mvcTester.post().uri("/v1/members/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .apply(print())
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .hasPathSatisfying("$.result", v -> v.assertThat().isEqualTo("SUCCESS"))
                .hasPathSatisfying("$.data", v -> v.assertThat().isEqualTo(1))
                .hasPathSatisfying("$.error", v -> v.assertThat().isNull());
    }

    @TestGuest
    @Test
    void 회원_학습_정보등록_Api를_요청한다() throws JsonProcessingException {
        // given
        var request = MemberFixture.createPreferenceRegisterRequest(1L);
        var jsonRequest = objectMapper.writeValueAsString(request);

        // when & then
        assertThat(mvcTester.post().uri("/v1/members/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.result", v -> v.assertThat().isEqualTo("SUCCESS"))
                .hasPathSatisfying("$.data", v -> v.assertThat().isNull())
                .hasPathSatisfying("$.error", v -> v.assertThat().isNull());
    }

}