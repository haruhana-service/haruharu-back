package org.kwakmunsu.haruhana.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.category.entity.Category;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryGroup;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryGroupJpaRepository;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryJpaRepository;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryTopicJpaRepository;
import org.springframework.stereotype.Service;

/**
 * 카테고리 관리 서비스 (생성, 수정, 삭제)
 */
@RequiredArgsConstructor
@Service
public class CategoryManager {

    private final CategoryValidator categoryValidator;
    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryGroupJpaRepository categoryGroupJpaRepository;
    private final CategoryTopicJpaRepository categoryTopicJpaRepository;

    public Category createCategory(String name) {
        categoryValidator.validateNewCategory(name);

        return categoryJpaRepository.save(Category.create(name));
    }

    public CategoryGroup createCategoryGroup(Long categoryId, String name) {
        categoryValidator.validateNewGroup(categoryId, name);

        return categoryGroupJpaRepository.save(CategoryGroup.create(
                categoryId,
                name
        ));
    }

    public CategoryTopic createCategoryTopic(Long groupId, String name) {
        categoryValidator.validateMewTopic(groupId, name);

        return categoryTopicJpaRepository.save(CategoryTopic.create(
                groupId,
                name
        ));
    }

}