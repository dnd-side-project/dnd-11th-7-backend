package com.dnd.jjakkak.domain.meetingcategory.repository;

import com.dnd.jjakkak.domain.meetingcategory.entity.MeetingCategory;
import com.dnd.jjakkak.domain.meetingcategory.entity.QMeetingCategory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

/**
 * 모임 카테고리 Querydsl 레포지토리 구현 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 28.
 */
public class MeetingCategoryRepositoryImpl extends QuerydslRepositorySupport implements MeetingCategoryRepositoryCustom {

    public MeetingCategoryRepositoryImpl() {
        super(MeetingCategory.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByMeetingId(Long meetingId) {
        QMeetingCategory meetingCategory = QMeetingCategory.meetingCategory;

        delete(meetingCategory)
                .where(meetingCategory.pk.meetingId.eq(meetingId))
                .execute();
    }
}
