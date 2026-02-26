package org.kwakmunsu.haruhana.admin.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.ControllerTestSupport;
import org.kwakmunsu.haruhana.admin.category.controller.request.CategoryCreateRequest;
import org.kwakmunsu.haruhana.admin.category.controller.request.CategoryGroupCreateRequest;
import org.kwakmunsu.haruhana.admin.category.controller.request.CategoryNameUpdateRequest;
import org.kwakmunsu.haruhana.admin.category.controller.request.CategoryTopicCreateRequest;
import org.kwakmunsu.haruhana.security.annotation.TestAdmin;
import org.springframework.http.MediaType;

class AdminCategoryControllerTest extends ControllerTestSupport {

    @TestAdmin
    @Test
    void 카테고리를_생성한다() throws JsonProcessingException {
        // given
        var request = new CategoryCreateRequest("알고리즘");

        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        assertThat(mvcTester.post().uri("/v1/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatusOk();
    }

    @TestAdmin
    @Test
    void 카테고리_그룹을_생성한다() throws JsonProcessingException {
        // given
        var request = new CategoryGroupCreateRequest(1L, "자료구조");
        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        assertThat(mvcTester.post().uri("/v1/admin/categories/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatusOk();
    }

    @TestAdmin
    @Test
    void 카테고리_토픽을_생성한다() throws JsonProcessingException {
        // given
        var request = new CategoryTopicCreateRequest(1L, "배열");
        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        assertThat(mvcTester.post().uri("/v1/admin/categories/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatusOk();
    }

    @TestAdmin
    @Test
    void 카테고리명을_수정한다() throws JsonProcessingException {
        // given
        var request = new CategoryNameUpdateRequest("자료구조");
        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        assertThat(mvcTester.patch().uri("/v1/admin/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatus(204);
    }

    @TestAdmin
    @Test
    void 카테고리를_삭제한다() {
        // when & then
        assertThat(mvcTester.delete().uri("/v1/admin/categories/1"))
                .apply(print())
                .hasStatus(204);
    }

}