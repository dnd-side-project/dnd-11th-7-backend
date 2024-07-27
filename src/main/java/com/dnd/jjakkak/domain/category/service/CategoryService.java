package com.dnd.jjakkak.domain.category.service;

import com.dnd.jjakkak.domain.category.dto.response.CategoryResponseDto;
import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.category.exception.CategoryNotFoundException;
import com.dnd.jjakkak.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 카테고리 서비스 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 24.
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 전체 목록을 조회하는 메서드입니다.
     *
     * @return 카테고리 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getCategoryList() {
        return categoryRepository.getCategories();
    }

    /**
     * 카테고리의 ID로 단건 조회하는 메서드입니다.
     *
     * @param id 카테고리 ID
     * @return 카테고리 응답 DTO
     */
    @Transactional(readOnly = true)
    public CategoryResponseDto getCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);

        return CategoryResponseDto.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }
}
