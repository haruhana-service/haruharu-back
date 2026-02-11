package org.kwakmunsu.haruhana.domain.streak.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Builder;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;

@Schema(description = "주간 풀이 여부 응답 DTO")
@Builder
public record WeeklySolvedStatusResponse(
        @Schema(description = "날짜", example = "2024-06-15")
        LocalDate date,

        @Schema(description = "해당 날짜에 문제를 풀었는지 여부", example = "true")
        boolean isSolved
) {

    public static List<WeeklySolvedStatusResponse> from(List<DailyProblem> dailyProblems, LocalDate today) {
        Set<LocalDate> solvedDates = dailyProblems.stream()
                .map(DailyProblem::getAssignedAt)
                .collect(Collectors.toSet());

        return IntStream.rangeClosed(0, 6)
                .mapToObj(i -> today.minusDays(6 - i))
                .map(date -> WeeklySolvedStatusResponse.builder()
                        .date(date)
                        .isSolved(solvedDates.contains(date))
                        .build())
                .toList();
    }

}