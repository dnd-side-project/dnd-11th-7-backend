package com.dnd.jjakkak.domain.meeting.repository;

import com.dnd.jjakkak.domain.dateofschedule.entity.QDateOfSchedule;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.entity.QMeeting;
import com.dnd.jjakkak.domain.schedule.entity.QSchedule;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

/**
 * 모임 Querydsl 레포지토리 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 29.
 */
public class MeetingRepositoryImpl extends QuerydslRepositorySupport implements MeetingRepositoryCustom {

    public MeetingRepositoryImpl() {
        super(Meeting.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkMeetingFull(Long meetingId) {
        QMeeting meeting = QMeeting.meeting;
        QSchedule schedule = QSchedule.schedule;

        Integer maxPeople = from(meeting)
                .where(meeting.meetingId.eq(meetingId))
                .select(meeting.numberOfPeople)
                .fetchOne();

        Long currentPeople = from(schedule)
                .where(schedule.meeting.meetingId.eq(meetingId))
                .select(schedule.scheduleId.count())
                .fetchOne();

        // 모임의 최대 인원과 현재 인원을 비교하여 모임이 꽉 찼는지 확인합니다.
        return maxPeople <= currentPeople;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAnonymous(Long meetingId) {
        QMeeting meeting = QMeeting.meeting;

        return Boolean.TRUE.equals(from(meeting)
                .where(meeting.meetingId.eq(meetingId))
                .select(meeting.isAnonymous)
                .fetchOne());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeetingResponseDto getMeetingResponse(String meetingUuid) {

        /**
         * SELECT
         *     ds.date_of_schedule_start,
         *     ds.date_of_schedule_end,
         *     AVG(ds.date_of_schedule_rank) AS avg_rank,
         *     COUNT(s.schedule_id) AS participants_count
         * FROM Date_of_Schedule ds
         * JOIN Schedule s ON ds.schedule_id = s.schedule_id
         * JOIN Meeting m ON s.meeting_id = m.meeting_id
         * WHERE m.meeting_id = 1 -- 조회할 모임 ID
         * GROUP BY ds.date_of_schedule_start, ds.date_of_schedule_end
         * ORDER BY avg_rank ASC -- 우선순위가 낮을수록 높은 우선순위로 간주
         * LIMIT 2; -- 상위 2개만 조회
         */

        QMeeting meeting = QMeeting.meeting;
        QSchedule schedule = QSchedule.schedule;
        QDateOfSchedule dateOfSchedule = QDateOfSchedule.dateOfSchedule;

//        from(dateOfSchedule)
//                .join(dateOfSchedule.schedule, schedule)
//                .join(schedule.meeting, meeting)
//                .where(meeting.meetingUuid.eq(meetingUuid))
//                .groupBy(dateOfSchedule.dateOfScheduleStart, dateOfSchedule.dateOfScheduleEnd)
//                .orderBy(dateOfSchedule.dateOfScheduleRank.asc())
//                .limit(2)
//                .select(dateOfSchedule.dateOfScheduleStart, dateOfSchedule.dateOfScheduleEnd, dateOfSchedule.dateOfScheduleRank.avg(), schedule.scheduleId.count())
//                .fetch();

        return null;
    }


}
