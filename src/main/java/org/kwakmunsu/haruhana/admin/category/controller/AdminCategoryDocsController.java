package org.kwakmunsu.haruhana.admin.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.kwakmunsu.haruhana.admin.category.controller.request.CategoryCreateRequest;
import org.kwakmunsu.haruhana.admin.category.controller.request.CategoryGroupCreateRequest;
import org.kwakmunsu.haruhana.admin.category.controller.request.CategoryNameUpdateRequest;
import org.kwakmunsu.haruhana.admin.category.controller.request.CategoryTopicCreateRequest;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;

@Tag(name = "Admin - Category", description = "관리자 카테고리 API")
public abstract class AdminCategoryDocsController {

    @Operation(
            summary = "카테고리(대분류) 생성 - 관리자",
            description = "새로운 카테고리(대분류)를 생성합니다."
    )
    @ApiExceptions(values = {
            ErrorType.DUPLICATE_CATEGORY_NAME,
            ErrorType.UNAUTHORIZED_ERROR,
            ErrorType.DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<?>> createCategory(CategoryCreateRequest request);

    @Operation(
            summary = "카테고리 그룹(중분류) 생성 - 관리자",
            description = "새로운 카테고리 그룹(중분류)를 생성합니다."
    )
    @ApiExceptions(values = {
            ErrorType.NOT_FOUND_CATEGORY,
            ErrorType.DUPLICATE_CATEGORY_GROUP_NAME,
            ErrorType.UNAUTHORIZED_ERROR,
            ErrorType.DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<?>> createCategoryGroup(
            CategoryGroupCreateRequest request
    );

    @Operation(
            summary = "카테고리 토픽(소분류) 생성 - 관리자",
            description = "새로운 카테고리 토픽(소분류)를 생성합니다."
    )
    @ApiExceptions(values = {
            ErrorType.NOT_FOUND_CATEGORY_GROUP,
            ErrorType.DUPLICATE_CATEGORY_TOPIC_NAME,
            ErrorType.UNAUTHORIZED_ERROR,
            ErrorType.DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<?>> createCategoryTopic(
            CategoryTopicCreateRequest request
    );

    @Operation(
            summary = "카테고리(대분류) 이름 수정 - 관리자",
            description = "카테고리(대분류)의 이름을 수정합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "카테고리 이름이 성공적으로 수정되었습니다."
    )
    @ApiExceptions(values = {
            ErrorType.NOT_FOUND_CATEGORY,
            ErrorType.DUPLICATE_CATEGORY_NAME,
            ErrorType.UNAUTHORIZED_ERROR,
            ErrorType.DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<?>> updateCategory(
            @Parameter(example = "1", description = "카테고리 ID", in = ParameterIn.PATH)
            Long categoryId,
            CategoryNameUpdateRequest request
    );

}