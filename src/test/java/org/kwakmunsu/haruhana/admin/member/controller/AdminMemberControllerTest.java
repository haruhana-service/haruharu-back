package org.kwakmunsu.haruhana.admin.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.ControllerTestSupport;
import org.kwakmunsu.haruhana.admin.member.controller.dto.MemberUpdateNicknameRequest;
import org.kwakmunsu.haruhana.admin.member.controller.dto.MemberUpdateRoleRequest;
import org.kwakmunsu.haruhana.admin.member.enums.SortBy;
import org.kwakmunsu.haruhana.admin.member.service.dto.AdminMemberPreferenceResponse;
import org.kwakmunsu.haruhana.admin.member.service.dto.AdminMemberPreviewResponse;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.response.PageResponse;
import org.kwakmunsu.haruhana.security.annotation.TestAdmin;
import org.kwakmunsu.haruhana.util.TestDateTimeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class AdminMemberControllerTest extends ControllerTestSupport {

    @TestAdmin
    @Test
    void 관리자용_회원_정보_목록_Api를_요청한다() {
        // given
        LocalDateTime lastLoginAt = TestDateTimeUtils.now();
        LocalDateTime createdAt = TestDateTimeUtils.now();

        List<AdminMemberPreviewResponse> response = List.of(
                new AdminMemberPreviewResponse(
                        1L,
                        "loginId",
                        "nickname",
                        Role.ROLE_MEMBER,
                        lastLoginAt,
                        createdAt,
                        EntityStatus.ACTIVE
                ));
        given(adminMemberService.findMembers(any(), any(), any())).willReturn(
                PageResponse.<AdminMemberPreviewResponse>builder()
                        .contents(response)
                        .hasNext(false)
                        .build()
        );

        // when & then
        assertThat(mvcTester.get().uri("/v1/admin/members")
                .param("search", "test")
                .param("sortBy", SortBy.JOIN_DESC.name())
                .param("size", "10")
                .param("page", "1"))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.data.contents", v -> v.assertThat().isNotEmpty())
                .hasPathSatisfying("$.data.contents[0].id", v -> v.assertThat().isEqualTo(1))
                .hasPathSatisfying("$.data.contents[0].loginId", v -> v.assertThat().isEqualTo("loginId"))
                .hasPathSatisfying("$.data.contents[0].nickname", v -> v.assertThat().isEqualTo("nickname"))
                .hasPathSatisfying("$.data.contents[0].role", v -> v.assertThat().isEqualTo("ROLE_MEMBER"))
                .hasPathSatisfying("$.data.contents[0].lastLoginAt", v -> v.assertThat().isNotNull())
                .hasPathSatisfying("$.data.contents[0].createdAt", v -> v.assertThat().isNotNull());
    }

    @TestAdmin
    @Test
    void 관리자용_회원_학습_정보_조회_Api를_요청한다() {
        // given
        LocalDate effectiveAt = TestDateTimeUtils.now().toLocalDate();

        var response = AdminMemberPreferenceResponse.builder()
                .id(1L)
                .memberId(1L)
                .categoryTopic("JAVA")
                .difficulty(ProblemDifficulty.EASY.name())
                .effectiveAt(effectiveAt)
                .build();

        given(adminMemberService.findMemberPreference(any())).willReturn(response);

        // when & then
        assertThat(mvcTester.get().uri("/v1/admin/members/{memberId}/preferences", 1L))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.data.memberId", v -> v.assertThat().isEqualTo(response.memberId().intValue()))
                .hasPathSatisfying("$.data.categoryTopic", v -> v.assertThat().isEqualTo(response.categoryTopic()))
                .hasPathSatisfying("$.data.difficulty", v -> v.assertThat().isEqualTo(response.difficulty()))
                .hasPathSatisfying("$.data.effectiveAt", v -> v.assertThat().isNotNull());
    }

    @TestAdmin
    @Test
    void 관리자용_회원_닉네임_변경_API를_요청한다() throws JsonProcessingException {
        // given
        var request = new MemberUpdateNicknameRequest("변경닉네임");
        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        assertThat(mvcTester.patch().uri("/v1/admin/members/{memberId}/nicknames", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(adminMemberService, times(1)).updateNickname(any(), any());
    }

    @TestAdmin
    @Test
    void 관리자용_회원_닉네임_변경_시_닉네임이_빈값이면_400을_반환한다() throws JsonProcessingException {
        // given
        var request = new MemberUpdateNicknameRequest("");
        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        assertThat(mvcTester.patch().uri("/v1/admin/members/{memberId}/nicknames", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @TestAdmin
    @Test
    void 관리자용_회원_역할_변경_APi를_요청한다() throws JsonProcessingException {
        // given
        var request = new MemberUpdateRoleRequest(Role.ROLE_ADMIN);
        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        assertThat(mvcTester.patch().uri("/v1/admin/members/{memberId}/roles", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(adminMemberService, times(1)).updateRole(any(), any());
    }

    @TestAdmin
    @Test
    void 관리자용_회원_비활성화_APi를_요청한다() {

        // when & then
        assertThat(mvcTester.delete().uri("/v1/admin/members/{memberId}", 1L))
                .apply(print())
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(adminMemberService, times(1)).deleteMember(any());
    }

}