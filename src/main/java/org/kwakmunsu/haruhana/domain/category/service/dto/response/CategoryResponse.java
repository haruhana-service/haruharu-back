package org.kwakmunsu.haruhana.domain.category.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.kwakmunsu.haruhana.domain.category.entity.Category;

@Schema(description = "카테고리(대분류) 응답 DTO")
@Builder
public record CategoryResponse(
        @Schema(description = "카테고리 ID", example = "1")
        Long id,

        @Schema(description = "카테고리 이름", example = "알고리즘")
        String name,

        @Schema(description = "그룹 목록")
        List<CategoryGroupResponse> groups
) {

    public static CategoryResponse of(Category category, List<CategoryGroupResponse> groups) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .groups(groups)
                .build();
    }

}