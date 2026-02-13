package org.kwakmunsu.haruhana.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminCategoryService {

    private final CategoryManager categoryManager;

    public void createCategory(String name) {
        categoryManager.createCategory(name);

        log.info("[AdminCategoryService] 카테고리 생성 완료 - name: {}", name);
    }

    public void createCategoryGroup(Long categoryId, String name) {
        categoryManager.createCategoryGroup(categoryId, name);

        log.info("[AdminCategoryService] 카테고리 그룹 생성 완료 - categoryId: {}, name: {}", categoryId, name);
    }

    public void createCategoryTopic(Long groupId, String name) {
        categoryManager.createCategoryTopic(groupId, name);

        log.info("[AdminCategoryService] 카테고리 토픽 생성 완료 - groupId: {}, name: {}", groupId, name);
    }

}