package org.kwakmunsu.haruhana.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryTopicJpaRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CategoryReader {

    private final CategoryTopicJpaRepository categoryTopicJpaRepository;

    public CategoryTopic findCategoryTopic(Long categoryTopicId) {
        return categoryTopicJpaRepository.findByIdAndStatus(categoryTopicId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new HaruHanaException(ErrorType.NOT_FOUND_CATEGORY_TOPIC));
    }

}