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
public class Category extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    public static Category create(String name) {
        Category category = new Category();

        category.name = name;

        return category;
    }

    public void updateName(String name) {
        this.name = name;
    }

}