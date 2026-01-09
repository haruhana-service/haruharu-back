package org.kwakmunsu.haruhana.domain.category.repository;

import org.kwakmunsu.haruhana.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {

}