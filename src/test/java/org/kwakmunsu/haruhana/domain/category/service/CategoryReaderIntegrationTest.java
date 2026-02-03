package org.kwakmunsu.haruhana.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.category.CategoryFactory;
import org.kwakmunsu.haruhana.domain.category.service.dto.response.CategoryListResponse;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@RequiredArgsConstructor
class CategoryReaderIntegrationTest extends IntegrationTestSupport {

    final CategoryReader categoryReader;
    final CategoryFactory categoryFactory;

    @BeforeEach
    void setUp() {
        categoryFactory.deleteAll();
        categoryFactory.saveAll();
    }

    @Test
    void 카테고리_목록을_조회한다() {
        // when
        CategoryListResponse categories = categoryReader.getCategories();

        // then
        log.info("categories={}", categories.toString());
    }

}