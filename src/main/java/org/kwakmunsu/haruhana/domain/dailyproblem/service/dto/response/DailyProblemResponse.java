package org.kwakmunsu.haruhana.domain.dailyproblem.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;

@Schema(description = "오늘의 문제 응답 DTO")
@Builder
public record DailyProblemResponse(
        @Schema(description = "Daily Problem ID", example = "1")
        Long id,

        @Schema(description = "오늘의 문제 제목", example = "Two Sum")
        String title,

        @Schema(description = "오늘의 문제 설명", example = "Given an array of integers...")
        String description,

        @Schema(description = "오늘의 문제 난이도", example = "EASY")
        String difficulty,

        @Schema(description = "오늘의 문제 카테고리 토픽 이름", example = "Java")
        String categoryTopicName,

        @Schema(description = "문제 풀이 여부", example = "true")
        boolean isSolved
) {

    public static DailyProblemResponse from(DailyProblem dailyProblem) {
        return DailyProblemResponse.builder()
                .id(dailyProblem.getId())
                .title(dailyProblem.getProblem().getTitle())
                .description(dailyProblem.getProblem().getDescription())
                .difficulty(dailyProblem.getProblem().getDifficulty().name())
                .categoryTopicName(dailyProblem.getProblem().getCategoryTopic().getName())
                .isSolved(dailyProblem.isSolved())
                .build();
    }

}