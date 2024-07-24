package com.dnd.zzaekkac.domain.category.service;

import com.dnd.zzaekkac.domain.category.dto.response.CategoryResponseDto;
import com.dnd.zzaekkac.domain.category.entity.Category;
import com.dnd.zzaekkac.domain.category.exception.CategoryNotFoundException;
import com.dnd.zzaekkac.domain.category.repository.CategoryRepository;
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

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getCategoryList() {
        return categoryRepository.getCategories();
    }

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
