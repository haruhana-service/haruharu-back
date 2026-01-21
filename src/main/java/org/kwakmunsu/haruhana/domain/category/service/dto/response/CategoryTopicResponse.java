package org.kwakmunsu.haruhana.domain.category.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;

@Schema(description = "카테고리 토픽(소분류) 응답 DTO")
@Builder
public record CategoryTopicResponse(
        @Schema(description = "토픽 ID", example = "1")
        Long id,

        @Schema(description = "토픽 이름", example = "배열")
        String name
) {

    public static CategoryTopicResponse from(CategoryTopic categoryTopic) {
        return CategoryTopicResponse.builder()
                .id(categoryTopic.getId())
                .name(categoryTopic.getName())
                .build();
    }

}
