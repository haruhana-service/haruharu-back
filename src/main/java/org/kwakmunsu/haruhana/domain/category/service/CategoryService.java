package org.kwakmunsu.haruhana.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.category.service.dto.response.CategoryListResponse;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryReader categoryReader;

    public CategoryListResponse getCategories() {
        return categoryReader.getCategories();
    }


}