package com.dnd.jjakkak.domain.category.repository;

import com.dnd.jjakkak.domain.category.entity.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

/**
 * 카테고리 레포지토리 테스트 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 25.
 */
@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;


    @Test
    @DisplayName("카테고리 전체 목록 조회 테스트")
    void testGetCategories() {
        // given
        Category study = Category.builder()
                .categoryName("스터디")
                .build();

        Category volunteer = Category.builder()
                .categoryName("봉사")
                .build();

        Category friend = Category.builder()
                .categoryName("친구")
                .build();

        categoryRepository.saveAll(List.of(study, volunteer, friend));

        // when
        List<Category> actual = categoryRepository.findAll();

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(3, actual.size()),
                () -> Assertions.assertEquals("스터디", actual.get(0).getCategoryName()),
                () -> Assertions.assertEquals("봉사", actual.get(1).getCategoryName()),
                () -> Assertions.assertNotEquals("친구", actual.get(0).getCategoryName())
        );
    }

}