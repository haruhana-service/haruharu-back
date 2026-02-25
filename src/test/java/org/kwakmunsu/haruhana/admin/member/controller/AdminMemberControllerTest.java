package org.kwakmunsu.haruhana.admin.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.ControllerTestSupport;
import org.kwakmunsu.haruhana.admin.member.enums.SortBy;
import org.kwakmunsu.haruhana.admin.member.service.dto.AdminMemberPreviewResponse;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.response.PageResponse;
import org.kwakmunsu.haruhana.security.annotation.TestAdmin;
import org.kwakmunsu.haruhana.util.TestDateTimeUtils;

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
                .hasPathSatisfying("$.data.contents[0].lastLoginAt", v -> v.assertThat().isEqualTo(lastLoginAt.toString()))
                .hasPathSatisfying("$.data.contents[0].createdAt", v -> v.assertThat().isEqualTo(createdAt.toString()));
    }

}