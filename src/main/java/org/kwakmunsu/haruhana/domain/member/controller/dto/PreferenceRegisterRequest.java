package org.kwakmunsu.haruhana.domain.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewPreference;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;

@Schema(description = "회원 선호도 등록 요청 DTO")
public record PreferenceRegisterRequest(
        @Schema(description = "카테고리 ID", example = "1")
        @NotNull(message = "카테고리는 필수입니다.")
        Long categoryTopicId,

        @Schema(description = "문제 난이도", example = "EASY")
        @NotNull(message = "난이도는 필수입니다.")
        ProblemDifficulty difficulty
) {

    public NewPreference toNewPreference() {
        return new NewPreference(categoryTopicId, difficulty);
    }

}