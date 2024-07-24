package com.dnd.jjakkak.domain.category.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 카테고리 응답 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 24.
 */
@Getter
public class CategoryResponseDto {
    private Long categoryId;
    private String categoryName;

    @Builder
    public CategoryResponseDto(Long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
