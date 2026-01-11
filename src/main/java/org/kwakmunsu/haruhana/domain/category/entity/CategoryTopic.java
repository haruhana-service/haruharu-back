package org.kwakmunsu.haruhana.domain.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kwakmunsu.haruhana.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CategoryTopic extends BaseEntity {

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false, unique = true)
    private String name;

    public static CategoryTopic create(Long groupId, String name) {
        CategoryTopic categoryTopic = new CategoryTopic();

        categoryTopic.groupId = groupId;
        categoryTopic.name = name;

        return categoryTopic;
    }

    public void updateName(String name) {
        this.name = name;
    }

}