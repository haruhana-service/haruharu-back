package org.kwakmunsu.haruhana.domain.category.controller;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.category.service.CategoryService;
import org.kwakmunsu.haruhana.domain.category.service.dto.response.CategoryListResponse;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CategoryController extends CategoryDocsController {

    private final CategoryService categoryService;

    @Override
    @GetMapping("/v1/categories")
    public ResponseEntity<ApiResponse<CategoryListResponse>> getCategories() {
        CategoryListResponse response = categoryService.getCategories();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

}