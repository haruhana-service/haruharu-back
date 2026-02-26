package org.kwakmunsu.haruhana.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryGroupJpaRepository;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryJpaRepository;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryTopicJpaRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class CategoryValidator {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryGroupJpaRepository categoryGroupJpaRepository;
    private final CategoryTopicJpaRepository categoryTopicJpaRepository;

    public void validateNewCategory(String name) {
        if (categoryJpaRepository.existsByNameAndStatus(name, EntityStatus.ACTIVE)) {
            throw new HaruHanaException(ErrorType.DUPLICATE_CATEGORY_NAME);
        }
    }

    public void validateTopicName(String name) {
        if (categoryTopicJpaRepository.existsByNameAndStatus(name, EntityStatus.ACTIVE)) {
            throw new HaruHanaException(ErrorType.DUPLICATE_CATEGORY_TOPIC_NAME);
        }
    }

    public void validateNewGroup(Long categoryId, String name) {
        if (!categoryJpaRepository.existsByIdAndStatus(categoryId, EntityStatus.ACTIVE)) {
            throw new HaruHanaException(ErrorType.NOT_FOUND_CATEGORY);
        }

        validateGroupName(name);
    }

    public void validateGroupName(String name) {
        if (categoryGroupJpaRepository.existsByNameAndStatus(name, EntityStatus.ACTIVE)) {
            throw new HaruHanaException(ErrorType.DUPLICATE_CATEGORY_GROUP_NAME);
        }
    }

    public void validateNewTopic(Long groupId, String name) {
        if (!categoryGroupJpaRepository.existsByIdAndStatus(groupId, EntityStatus.ACTIVE)) {
            throw new HaruHanaException(ErrorType.NOT_FOUND_CATEGORY_GROUP);
        }

        if (categoryTopicJpaRepository.existsByNameAndStatus(name, EntityStatus.ACTIVE)) {
            throw new HaruHanaException(ErrorType.DUPLICATE_CATEGORY_TOPIC_NAME);
        }
    }

}