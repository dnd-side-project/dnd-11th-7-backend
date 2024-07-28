package com.dnd.jjakkak.domain.meetingcategory.repository;

import com.dnd.jjakkak.domain.meetingcategory.entity.MeetingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 모임 카테고리 JPA 레포지토리입니다.
 *
 * @author 정승조
 * @version 2024. 07. 25.
 */
public interface MeetingCategoryRepository
        extends JpaRepository<MeetingCategory, MeetingCategory.Pk>, MeetingCategoryRepositoryCustom {
}
