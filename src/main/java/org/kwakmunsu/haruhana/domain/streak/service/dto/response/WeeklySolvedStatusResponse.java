package org.kwakmunsu.haruhana.domain.streak.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import lombok.Builder;

@Schema(description = "주간 풀이 여부 응답 DTO")
@Builder
public record WeeklySolvedStatusResponse(
        @Schema(description = "날짜", example = "2024-06-15")
        LocalDate date,

        @Schema(description = "해당 날짜에 문제를 풀었는지 여부", example = "true")
        boolean isSolved
) {

    public static List<WeeklySolvedStatusResponse> from(List<LocalDate> onTimeDates, LocalDate today) {
        Set<LocalDate> onTimeDateSet = new HashSet<>(onTimeDates);

        return IntStream.rangeClosed(0, 6)
                .mapToObj(i -> today.minusDays(6 - i))
                .map(date -> WeeklySolvedStatusResponse.builder()
                        .date(date)
                        .isSolved(onTimeDateSet.contains(date))
                        .build())
                .toList();
    }

}