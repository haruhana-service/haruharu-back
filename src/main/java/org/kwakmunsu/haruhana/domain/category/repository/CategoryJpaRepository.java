package org.kwakmunsu.haruhana.domain.category.repository;

import org.kwakmunsu.haruhana.domain.category.entity.Category;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {

    boolean existsByNameAndStatus(String name, EntityStatus status);
    boolean existsByIdAndStatus(Long id, EntityStatus status);

}