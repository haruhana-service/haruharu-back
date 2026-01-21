package org.kwakmunsu.haruhana.domain.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.kwakmunsu.haruhana.domain.category.service.dto.response.CategoryListResponse;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;

@Tag(name = "Category Docs", description = "Category 관련 API 문서")
public abstract class CategoryDocsController {

    @Operation(
            summary = "카테고리 목록 조회 API - JWT [O]",
            description = """
                    ### 계층형 카테고리 목록을 조회합니다.
                    - **대분류(Category)**: 최상위 카테고리
                    - **중분류(CategoryGroup)**: 카테고리에 속한 그룹
                    - **소분류(CategoryTopic)**: 그룹에 속한 토픽
                    """
    )
    @ApiExceptions(values = {
            ErrorType.DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<CategoryListResponse>> getCategories();

}