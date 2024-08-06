package com.dnd.jjakkak.domain.category;

import com.dnd.jjakkak.domain.category.dto.response.CategoryResponseDto;

import java.util.List;

/**
 * 카테고리 더미 데이터 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 06.
 */
public class CategoryDummy {

    public static List<CategoryResponseDto> getCategoryResponseDtoList() {

        return List.of(
                CategoryResponseDto.builder()
                        .categoryId(1L)
                        .categoryName("학교")
                        .build(),

                CategoryResponseDto.builder()
                        .categoryId(2L)
                        .categoryName("친구")
                        .build(),

                CategoryResponseDto.builder()
                        .categoryId(3L)
                        .categoryName("팀플")
                        .build(),

                CategoryResponseDto.builder()
                        .categoryId(4L)
                        .categoryName("회의")
                        .build(),

                CategoryResponseDto.builder()
                        .categoryId(5L)
                        .categoryName("스터디")
                        .build(),

                CategoryResponseDto.builder()
                        .categoryId(6L)
                        .categoryName("취미")
                        .build(),

                CategoryResponseDto.builder()
                        .categoryId(7L)
                        .categoryName("봉사")
                        .build(),

                CategoryResponseDto.builder()
                        .categoryId(8L)
                        .categoryName("기타")
                        .build()
        );
    }

    public static CategoryResponseDto getCategoryResponseDto() {
        return CategoryResponseDto.builder()
                .categoryId(1L)
                .categoryName("학교")
                .build();
    }
}
