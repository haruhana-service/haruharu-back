package org.kwakmunsu.haruhana.domain.category;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.category.entity.Category;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryGroup;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryGroupJpaRepository;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryJpaRepository;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryTopicJpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Profile("test")
public class CategoryFactory {

    private final CategoryGroupJpaRepository categoryGroupJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryTopicJpaRepository categoryTopicJpaRepository;

    private final List<String> categoryNames = List.of("개발");
    private final Map<String, List<String>> categoryToGroups = Map.of(
            "개발", List.of("프로그래밍 언어", "프레임워크", "데이터베이스")
    );
    private final Map<String, List<String>> groupToTopics = Map.of(
            "프로그래밍 언어", List.of("Java", "Python", "C/C++", "JavaScript", "Ruby", "Go", "Kotlin", "Swift"),
            "프레임워크", List.of("Spring", "Django", "Flask", "React", "Vue.js", "Angular", "Ruby on Rails", "Laravel"),
            "데이터베이스", List.of("MySQL", "PostgreSQL", "Oracle", "MongoDB", "Redis")
    );

    @Transactional
    public void saveAll() {
        // 1. Category 저장
        for (String categoryName : categoryNames) {
            Category category = Category.create(categoryName);
            Category savedCategory = categoryJpaRepository.save(category);

            // 2. CategoryGroup 저장 (해당 Category에 속하는 그룹들)
            List<String> groupNames = categoryToGroups.get(categoryName);
            if (groupNames != null) {
                for (String groupName : groupNames) {
                    CategoryGroup categoryGroup = CategoryGroup.create(savedCategory.getId(), groupName);
                    CategoryGroup savedGroup = categoryGroupJpaRepository.save(categoryGroup);

                    // 3. CategoryTopic 저장 (해당 Group에 속하는 토픽들)
                    List<String> topicNames = groupToTopics.get(groupName);
                    if (topicNames != null) {
                        for (String topicName : topicNames) {
                            CategoryTopic categoryTopic = CategoryTopic.create(savedGroup.getId(), topicName);
                            categoryTopicJpaRepository.save(categoryTopic);
                        }
                    }
                }
            }
        }
    }

    @Transactional
    public void deleteAll() {
        categoryTopicJpaRepository.deleteAll();
        categoryGroupJpaRepository.deleteAll();
        categoryJpaRepository.deleteAll();
    }

}