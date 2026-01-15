package org.kwakmunsu.haruhana.domain.streak.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.kwakmunsu.haruhana.domain.streak.entity.Streak;

@Schema(description = "Streak 응답 DTO")
@Builder
public record StreakResponse(
        @Schema(description = "현재 연속 일수", example = "5")
        Long currentStreak,

        @Schema(description = "최대 연속 일수", example = "10")
        Long maxStreak
) {

    public static StreakResponse from(Streak streak) {
        return StreakResponse.builder()
                .currentStreak(streak.getCurrentStreak())
                .maxStreak(streak.getMaxStreak())
                .build();
    }

}