package org.kwakmunsu.haruhana.domain.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.ControllerTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class MemberControllerTest extends ControllerTestSupport {

    @Test
    void 회원가입_Api를_요청한다() {
        // given
        var request = MemberFixture.createMemberCreateRequest();
        var jsonRequest = objectMapper.writeValueAsString(request);

        given(memberService.createMember(any())).willReturn(1L);

        // when
        assertThat(mvcTester.post().uri("/v1/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .apply(print())
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .hasPathSatisfying("$.result", v -> v.assertThat().isEqualTo("SUCCESS"))
                .hasPathSatisfying("$.data", v -> v.assertThat().isEqualTo(1))
                .hasPathSatisfying("$.error", v -> v.assertThat().isNull());
    }

}