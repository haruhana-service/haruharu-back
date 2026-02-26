package org.kwakmunsu.haruhana.admin.category.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "카테고리 이름 수정 요청 DTO")
public record CategoryNameUpdateRequest(

        @Schema(description = "변경할 이름", example = "알고리즘")
        @NotBlank(message = "이름은 필수입니다.")
        String name
) {

}
