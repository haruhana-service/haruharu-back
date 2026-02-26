package org.kwakmunsu.haruhana.admin.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.admin.category.controller.request.CategoryCreateRequest;
import org.kwakmunsu.haruhana.admin.category.controller.request.CategoryGroupCreateRequest;
import org.kwakmunsu.haruhana.admin.category.controller.request.CategoryNameUpdateRequest;
import org.kwakmunsu.haruhana.admin.category.controller.request.CategoryTopicCreateRequest;
import org.kwakmunsu.haruhana.admin.category.service.AdminCategoryService;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminCategoryController extends AdminCategoryDocsController {

    private final AdminCategoryService categoryService;

    @Override
    @PostMapping("/v1/admin/categories")
    public ResponseEntity<ApiResponse<?>> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        categoryService.createCategory(request.name());

        return ResponseEntity.ok(ApiResponse.success());
    }

    @Override
    @PostMapping("/v1/admin/categories/groups")
    public ResponseEntity<ApiResponse<?>> createCategoryGroup(@Valid @RequestBody CategoryGroupCreateRequest request) {
        categoryService.createCategoryGroup(
                request.categoryId(),
                request.name()
        );

        return ResponseEntity.ok(ApiResponse.success());
    }

    @Override
    @PostMapping("/v1/admin/categories/topics")
    public ResponseEntity<ApiResponse<?>> createCategoryTopic(@Valid @RequestBody CategoryTopicCreateRequest request) {
        categoryService.createCategoryTopic(
                request.groupId(),
                request.name()
        );

        return ResponseEntity.ok(ApiResponse.success());
    }

    @Override
    @PatchMapping("/v1/admin/categories/{categoryId}")
    public ResponseEntity<ApiResponse<?>> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryNameUpdateRequest request
    ) {
        categoryService.updateCategory(categoryId, request.name());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success());
    }

    @Override
    @DeleteMapping("/v1/admin/categories/{categoryId}")
    public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success());
    }

}