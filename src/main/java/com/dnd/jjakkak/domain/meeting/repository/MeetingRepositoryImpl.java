package com.dnd.jjakkak.domain.meeting.repository;

import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.entity.QMeeting;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

/**
 * 모임 Querydsl 레포지토리입니다.
 *
 * @author 정승조
 * @version 2024. 08. 08.
 */
public class MeetingRepositoryImpl extends QuerydslRepositorySupport implements MeetingRepositoryCustom {

    public MeetingRepositoryImpl() {
        super(Meeting.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeetingResponseDto.BestTime findBestTimeByMeetingId(Long meetingId) {
        QMeeting meeting = QMeeting.meeting;

        // TODO: 겹치는 시간 구하는 쿼리문 어떻게 작성해야됨?..

        return null;
    }
}
