package org.kwakmunsu.haruhana.admin.problem;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.kwakmunsu.haruhana.domain.problem.entity.Problem;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;

@Schema(description = "관리자 문제 미리보기 응답")
@Builder
public record AdminProblemPreviewResponse(
        @Schema(description = "문제 ID", example = "1")
        Long id,

        @Schema(description = "문제 제목", example = "알고리즘 문제 풀이")
        String title,

        @Schema(description = "문제 설명", example = "주어진 배열에서 최대값을 찾는 문제입니다.")
        String description,

        @Schema(description = "AI 답변", example = "최대값을 찾기 위해 배열을 순회하면서 현재까지의 최대값을 갱신하는 방법이 있습니다.")
        String aiAnswer,

        @Schema(description = "카테고리 주제", example = "알고리즘")
        String categoryTopic,

        @Schema(description = "문제 난이도", example = "EASY")
        ProblemDifficulty difficulty
) {

    public static List<AdminProblemPreviewResponse> from(List<Problem> problems) {
        return problems.stream()
                .map(problem -> AdminProblemPreviewResponse.builder()
                        .id(problem.getId())
                        .title(problem.getTitle())
                        .description(problem.getDescription())
                        .aiAnswer(problem.getAiAnswer())
                        .categoryTopic(problem.getCategoryTopic().getName())
                        .difficulty(problem.getDifficulty())
                        .build())
                .toList();

    }

}