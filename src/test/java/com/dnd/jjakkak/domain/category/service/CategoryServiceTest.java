package com.dnd.jjakkak.domain.category.service;

import com.dnd.jjakkak.domain.category.dto.response.CategoryResponseDto;
import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.category.exception.CategoryNotFoundException;
import com.dnd.jjakkak.domain.category.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 서비스 테스트 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 25.
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    CategoryService categoryService;

    @Mock
    CategoryRepository categoryRepository;


    @Test
    @DisplayName("카테고리 전체 목록 조회 테스트")
    void testGetCategoryList() {
        // given

        CategoryResponseDto school = CategoryResponseDto.builder()
                .categoryName("학교")
                .build();

        CategoryResponseDto meeting = CategoryResponseDto.builder()
                .categoryName("회의")
                .build();

        List<CategoryResponseDto> categoryResponseList = List.of(school, meeting);
        Mockito.when(categoryRepository.getCategories()).thenReturn(categoryResponseList);

        // when
        List<CategoryResponseDto> actual = categoryService.getCategoryList();

        // then

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, actual.size()),
                () -> Assertions.assertEquals("학교", actual.get(0).getCategoryName()),
                () -> Assertions.assertEquals("회의", actual.get(1).getCategoryName())
        );
    }

    @Test
    @DisplayName("카테고리 개별 조회 - 성공")
    void testGetCategory_Success() {

        // given
        Category response = Category.builder()
                .categoryName("팀플")
                .build();

        Mockito.when(categoryRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(response));

        // when
        CategoryResponseDto actual = categoryService.getCategory(1L);

        // then
        Assertions.assertEquals(response.getCategoryId(), actual.getCategoryId());
        Assertions.assertEquals("팀플", actual.getCategoryName());
    }

    @Test
    @DisplayName("카테고리 개별 조회 - 실패")
    void testGetCategory_Fail_404() {

        // given
        Mockito.when(categoryRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        // expected
        Assertions.assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getCategory(1L));
    }

}