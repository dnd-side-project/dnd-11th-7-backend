package com.dnd.zzaekkac.domain.category.repository;

import com.dnd.zzaekkac.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 카테고리 JPA 레포지토리입니다.
 *
 * @author 정승조
 * @version 2024. 07. 24.
 */
public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {
}
