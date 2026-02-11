package org.kwakmunsu.haruhana.domain.streak.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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

    public static List<WeeklySolvedStatusResponse> from(List<DailyProblem> dailyProblems) {
        LocalDate today = LocalDate.now();
        List<LocalDate> last7Days = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            last7Days.add(today.minusDays(i));
        }

        // solved된 날짜들만
        Set<LocalDate> solvedDates = dailyProblems.stream()
                .filter(DailyProblem::isSolved)
                .map(DailyProblem::getAssignedAt)
                .collect(Collectors.toSet());

        return last7Days.stream()
                .map(date -> WeeklySolvedStatusResponse.builder()
                        .date(date)
                        .isSolved(solvedDates.contains(date))
                        .build())
                .toList();
    }

}