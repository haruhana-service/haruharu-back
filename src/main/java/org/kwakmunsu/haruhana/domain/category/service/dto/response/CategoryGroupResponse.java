package org.kwakmunsu.haruhana.domain.category.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryGroup;

@Schema(description = "카테고리 그룹(중분류) 응답 DTO")
@Builder
public record CategoryGroupResponse(
        @Schema(description = "그룹 ID", example = "1")
        Long id,

        @Schema(description = "그룹 이름", example = "자료구조")
        String name,

        @Schema(description = "토픽 목록")
        List<CategoryTopicResponse> topics
) {

    public static CategoryGroupResponse of(CategoryGroup categoryGroup, List<CategoryTopicResponse> topics) {
        return CategoryGroupResponse.builder()
                .id(categoryGroup.getId())
                .name(categoryGroup.getName())
                .topics(topics)
                .build();
    }

}