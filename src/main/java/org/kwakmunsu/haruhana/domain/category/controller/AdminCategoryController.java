package org.kwakmunsu.haruhana.domain.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.category.service.AdminCategoryService;
import org.kwakmunsu.haruhana.domain.category.controller.dto.request.CategoryGroupCreateRequest;
import org.kwakmunsu.haruhana.domain.category.controller.dto.request.CategoryCreateRequest;
import org.kwakmunsu.haruhana.domain.category.controller.dto.request.CategoryTopicCreateRequest;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.springframework.http.ResponseEntity;
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

}