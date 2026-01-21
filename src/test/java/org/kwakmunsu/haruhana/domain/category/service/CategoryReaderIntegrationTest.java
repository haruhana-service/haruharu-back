package org.kwakmunsu.haruhana.domain.category.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.category.service.dto.response.CategoryListResponse;
import org.kwakmunsu.haruhana.domain.category.service.dto.response.CategoryResponse;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("local")
@Slf4j
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
@RequiredArgsConstructor
@SpringBootTest
class CategoryReaderIntegrationTest extends IntegrationTestSupport {

    final CategoryReader categoryReader;

    @Test
    void 카테고리_목록을_조회한다() {
        // given

        // when
        CategoryListResponse categories = categoryReader.getCategories();

        // then
        log.info("categories={}", categories.toString());
    }

}