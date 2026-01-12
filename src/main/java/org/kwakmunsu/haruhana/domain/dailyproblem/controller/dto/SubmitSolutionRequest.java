package org.kwakmunsu.haruhana.domain.dailyproblem.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "문제 제출 요청 DTO")
public record SubmitSolutionRequest(
        @Schema(description = "사용자 답변", example = "Inversion of Control은...")
        @Size(max = 5000, message = "사용자 답변은 최대 5000자까지 허용됩니다.")
        @NotBlank(message = "사용자 답변은 비어 있을 수 없습니다.")
        String userAnswer
) {

}