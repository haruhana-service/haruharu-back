package org.kwakmunsu.haruhana.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminCategoryService {

    private final CategoryManager categoryManager;

    public void createCategory(String name) {
        categoryManager.createCategory(name);
    }

    public void createCategoryGroup(Long categoryId, String name) {
        categoryManager.createCategoryGroup(categoryId, name);
    }

    public void createCategoryTopic(Long groupId, String name) {
        categoryManager.createCategoryTopic(groupId, name);
    }

}