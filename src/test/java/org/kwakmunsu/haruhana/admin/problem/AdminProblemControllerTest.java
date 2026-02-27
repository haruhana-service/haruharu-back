package org.kwakmunsu.haruhana.admin.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.ControllerTestSupport;
import org.kwakmunsu.haruhana.admin.problem.service.dto.AdminProblemPreviewResponse;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;
import org.kwakmunsu.haruhana.global.support.response.PageResponse;
import org.kwakmunsu.haruhana.security.annotation.TestAdmin;
import org.kwakmunsu.haruhana.security.annotation.TestMember;
import org.springframework.http.HttpStatus;

class AdminProblemControllerTest extends ControllerTestSupport {

    @TestAdmin
    @Test
    void 관리자가_문제_목록을_조회한다() {
        // given
        List<AdminProblemPreviewResponse> contents = List.of(
                AdminProblemPreviewResponse.builder()
                        .id(1L)
                        .title("알고리즘 문제")
                        .description("배열에서 최대값을 찾는 문제")
                        .aiAnswer("순회하면서 최대값을 갱신합니다.")
                        .categoryTopic("알고리즘")
                        .difficulty(ProblemDifficulty.EASY)
                        .build()
        );

        given(adminProblemService.findProblems(any(), any())).willReturn(
                PageResponse.<AdminProblemPreviewResponse>builder()
                        .contents(contents)
                        .hasNext(false)
                        .build()
        );

        // when & then
        assertThat(mvcTester.get().uri("/v1/admin/problems")
                .param("date", "2025-01-01")
                .param("page", "1")
                .param("size", "20"))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.data.contents", v -> v.assertThat().isNotEmpty())
                .hasPathSatisfying("$.data.hasNext", v -> v.assertThat().isEqualTo(false))
                .hasPathSatisfying("$.data.contents[0].id", v -> v.assertThat().isEqualTo(1))
                .hasPathSatisfying("$.data.contents[0].title", v -> v.assertThat().isEqualTo("알고리즘 문제"))
                .hasPathSatisfying("$.data.contents[0].categoryTopic", v -> v.assertThat().isEqualTo("알고리즘"))
                .hasPathSatisfying("$.data.contents[0].difficulty", v -> v.assertThat().isEqualTo("EASY"));
    }

    @TestAdmin
    @Test
    void 날짜_파라미터_없이_문제_목록을_조회한다() {
        // given
        given(adminProblemService.findProblems(any(), any())).willReturn(
                PageResponse.<AdminProblemPreviewResponse>builder()
                        .contents(List.of())
                        .hasNext(false)
                        .build()
        );

        // when & then
        assertThat(mvcTester.get().uri("/v1/admin/problems"))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.data.hasNext", v -> v.assertThat().isEqualTo(false));
    }

    @TestMember
    @Test
    void 일반_회원은_문제_목록_조회에_접근할_수_없다() {
        // when & then
        assertThat(mvcTester.get().uri("/v1/admin/problems"))
                .apply(print())
                .hasStatus(HttpStatus.FORBIDDEN);

        verifyNoInteractions(adminProblemService); //  보안 필터에서 요청이 차단되는지 확인
    }

    @Test
    void 인증되지_않은_사용자는_문제_목록_조회에_접근할_수_없다() {
        // when & then
        assertThat(mvcTester.get().uri("/v1/admin/problems"))
                .apply(print())
                .hasStatus(HttpStatus.FORBIDDEN);

        verifyNoInteractions(adminProblemService);
    }

}
