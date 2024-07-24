package com.dnd.zzaekkac.domain.category.repository;

import com.dnd.zzaekkac.domain.category.dto.response.CategoryResponseDto;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * 카테고리에 Querydsl 메서드 정의 인터페이스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 24.
 */
@NoRepositoryBean
public interface CategoryRepositoryCustom {

    /**
     * 카테고리 전체 목록을 DTO 형태로 반환하는 메서드입니다.
     *
     * @return 카테고리 응답 DTO 리스트
     */
    List<CategoryResponseDto> getCategories();
}
