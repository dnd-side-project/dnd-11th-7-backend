package com.dnd.jjakkak.domain.meetingcategory.repository;

import org.springframework.data.repository.NoRepositoryBean;

/**
 * 모임 카테고리 Querydsl 메서드 정의 인터페이스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 28.
 */
@NoRepositoryBean
public interface MeetingCategoryRepositoryCustom {

    void deleteByMeetingId(Long meetingId);

}
