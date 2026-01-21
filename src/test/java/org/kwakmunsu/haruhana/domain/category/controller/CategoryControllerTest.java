package org.kwakmunsu.haruhana.domain.category.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.ControllerTestSupport;
import org.kwakmunsu.haruhana.security.annotation.TestMember;

class CategoryControllerTest extends ControllerTestSupport {

    @TestMember
    @Test
    void 카테고리_목록을_조회한다() {
        // when & then
        assertThat(mvcTester.get().uri("/v1/categories"))
                .apply(print())
                .hasStatusOk();
    }

}