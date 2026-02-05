package org.kwakmunsu.haruhana.domain.category.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "카테고리 그룹(중분류) 생성 요청 DTO")
public record CategoryGroupCreateRequest(

        @Schema(description = "카테고리 ID", example = "1")
        @NotNull(message = "카테고리 ID는 필수입니다.")
        @Positive(message = "카테고리 ID는 양수여야 합니다.")
        Long categoryId,

        @Schema(description = "그룹 이름", example = "프론트엔드")
        @NotBlank(message = "그룹 이름은 필수입니다.")
        String name
) {

}