package org.kwakmunsu.haruhana.domain.category.repository;

import java.util.List;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryGroupJpaRepository extends JpaRepository<CategoryGroup, Long> {

    List<CategoryGroup> findByCategoryId(Long categoryId);

}