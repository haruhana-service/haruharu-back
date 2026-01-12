package org.kwakmunsu.haruhana.domain.category;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.springframework.test.util.ReflectionTestUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryTopicFixture {

    public static final Long GROUP_ID = 1L;
    public static final String CATEGORY_TOPIC_NAME = "Java";

    public static CategoryTopic createCategoryTopic() {
        CategoryTopic categoryTopic = CategoryTopic.create(GROUP_ID, CATEGORY_TOPIC_NAME);
        ReflectionTestUtils.setField(categoryTopic, "id", 1L);
        return categoryTopic;
    }

    public static CategoryTopic createCategoryTopic(Long id, String name) {
        CategoryTopic categoryTopic = CategoryTopic.create(GROUP_ID, name);
        ReflectionTestUtils.setField(categoryTopic, "id", id);
        return categoryTopic;
    }

}

