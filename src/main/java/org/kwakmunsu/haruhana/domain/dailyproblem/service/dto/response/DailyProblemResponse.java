package org.kwakmunsu.haruhana.domain.dailyproblem.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.problem.entity.Problem;

@Schema(description = "데일리 문제 미리보기 응답 DTO")
@Builder
public record DailyProblemResponse(
        @Schema(description = "Daily Problem ID", example = "1")
        Long id,

        @Schema(description = "문제 난이도", example = "EASY")
        String difficulty,

        @Schema(description = "문제 카테고리 토픽 이름", example = "Java")
        String categoryTopic,

        @Schema(description = "문제 제목", example = "Spring IOC/DI")
        String title,

        @Schema(description = "풀이 여부", example = "true")
        boolean isSolved
) {

    public static DailyProblemResponse from(DailyProblem dailyProblem) {
        Problem problem = dailyProblem.getProblem();

        return DailyProblemResponse.builder()
                .id(dailyProblem.getId())
                .difficulty(problem.getDifficulty().name())
                .categoryTopic(problem.getCategoryTopic().getName())
                .title(problem.getTitle())
                .isSolved(dailyProblem.isSolved())
                .build();
    }

}