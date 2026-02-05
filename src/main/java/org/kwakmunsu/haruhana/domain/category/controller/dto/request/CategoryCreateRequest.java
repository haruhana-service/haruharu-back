package org.kwakmunsu.haruhana.domain.category.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "카테고리(대분류) 생성 요청 DTO")
public record CategoryCreateRequest(
        @Schema(description = "카테고리 이름", example = "개발")
        @NotBlank(message = "카테고리 이름은 필수입니다.")
        String name
) {

}