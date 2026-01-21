package org.kwakmunsu.haruhana.domain.category.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Schema(description = "카테고리 목록 응답 DTO")
@Builder
public record CategoryListResponse(
        @Schema(description = "카테고리 목록")
        List<CategoryResponse> categories
) {

    public static CategoryListResponse from(List<CategoryResponse> categories) {
        return CategoryListResponse.builder()
                .categories(categories)
                .build();
    }

}
