package org.kwakmunsu.haruhana.domain.category.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "카테고리 토픽(소분류) 생성 요청 DTO")
public record CategoryTopicCreateRequest(

        @Schema(description = "그룹 ID", example = "1")
        @NotNull(message = "그룹 ID는 필수입니다.")
        Long groupId,

        @Schema(description = "토픽 이름", example = "리액트")
        @NotBlank(message = "토픽 이름은 필수입니다.")
        String name
) {

}