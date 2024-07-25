package com.dnd.jjakkak.domain.category.repository;

import com.dnd.jjakkak.domain.category.dto.response.CategoryResponseDto;
import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.category.entity.QCategory;
import com.querydsl.core.types.Projections;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

/**
 * 카테고리 Querydsl 구현 레포지토리 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 24.
 */
public class CategoryRepositoryImpl extends QuerydslRepositorySupport implements CategoryRepositoryCustom {

    public CategoryRepositoryImpl() {
        super(Category.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CategoryResponseDto> getCategories() {
        QCategory category = QCategory.category;

        return from(category)
                .select(Projections.constructor(CategoryResponseDto.class,
                        category.categoryId,
                        category.categoryName))
                .fetch();
    }
}
