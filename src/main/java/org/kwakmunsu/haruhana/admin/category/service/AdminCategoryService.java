package org.kwakmunsu.haruhana.admin.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.category.service.CategoryManager;
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

    public void updateCategory(Long categoryId, String name) {
        categoryManager.updateCategory(categoryId, name);

        log.info("[AdminCategoryService] 카테고리명 수정 완료 - categoryId: {}, name: {}", categoryId, name);
    }

    public void deleteCategory(Long categoryId) {
        categoryManager.deleteCategory(categoryId);

        log.info("[AdminCategoryService] 카테고리 삭제 완료 - categoryId: {}", categoryId);
    }

    public void updateCategoryGroup(Long groupId, String name) {
        categoryManager.updateCategoryGroup(groupId, name);

        log.info("[AdminCategoryService] 카테고리 그룹명 수정 완료 - groupId: {}, name: {}", groupId, name);
    }

    public void deleteCategoryGroup(Long groupId) {
        categoryManager.deleteCategoryGroup(groupId);

        log.info("[AdminCategoryService] 카테고리 그룹 삭제 완료 - groupId: {}", groupId);
    }

    public void updateCategoryTopic(Long topicId, String name) {
        categoryManager.updateCategoryTopic(topicId, name);

        log.info("[AdminCategoryService] 카테고리 토픽명 수정 완료 - topicId: {}, name: {}", topicId, name);
    }

}