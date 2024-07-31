package com.dnd.jjakkak.domain.schedule.repository;

import com.dnd.jjakkak.domain.schedule.entity.QSchedule;
import com.dnd.jjakkak.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Optional;

/**
 * 일정 Querydsl 레포지토리 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 31.
 */
public class ScheduleRepositoryImpl extends QuerydslRepositorySupport implements ScheduleRepositoryCustom {

    public ScheduleRepositoryImpl() {
        super(Schedule.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Schedule> findByMemberIdAndMeetingId(Long memberId, Long meetingId) {
        QSchedule schedule = QSchedule.schedule;

        return Optional.ofNullable(
                from(schedule)
                        .where(schedule.member.memberId.eq(memberId)
                                .and(schedule.meeting.meetingId.eq(meetingId)))
                        .select(schedule)
                        .fetchOne());
    }
}
