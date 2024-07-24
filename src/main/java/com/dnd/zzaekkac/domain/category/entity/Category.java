package com.dnd.zzaekkac.domain.category.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 카테고리 엔티티 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 24.
 */
@Getter
@Entity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false, name = "category_name", length = 30)
    private String categoryName;

    @Builder
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
}
