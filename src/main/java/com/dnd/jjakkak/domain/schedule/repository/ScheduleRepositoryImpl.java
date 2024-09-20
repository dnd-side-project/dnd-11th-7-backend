package com.dnd.jjakkak.domain.schedule.repository;

import com.dnd.jjakkak.domain.dateofschedule.dto.response.DateOfScheduleResponseDto;
import com.dnd.jjakkak.domain.dateofschedule.entity.QDateOfSchedule;
import com.dnd.jjakkak.domain.schedule.dto.response.ScheduleResponseDto;
import com.dnd.jjakkak.domain.schedule.entity.QSchedule;
import com.dnd.jjakkak.domain.schedule.entity.Schedule;
import com.querydsl.core.types.Projections;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
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
    public Optional<Schedule> findByMemberIdAndMeetingUuid(Long memberId, String meetingUuid) {

        QSchedule schedule = QSchedule.schedule;

        return Optional.ofNullable(
                from(schedule)
                        .where(schedule.member.memberId.eq(memberId)
                                .and(schedule.meeting.meetingUuid.eq(meetingUuid)))
                        .select(schedule)
                        .fetchOne());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Schedule> findNotAssignedScheduleByMeetingUuid(String meetingUuid) {
        QSchedule schedule = QSchedule.schedule;

        return Optional.ofNullable(
                from(schedule)
                        .where(schedule.meeting.meetingUuid.eq(meetingUuid)
                                .and(schedule.isAssigned.eq(false)))
                        .select(schedule)
                        .limit(1L)
                        .fetchOne());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduleResponseDto findScheduleWithDateOfSchedule(Long scheduleId) {
        QSchedule schedule = QSchedule.schedule;
        QDateOfSchedule dateOfSchedule = QDateOfSchedule.dateOfSchedule;

        ScheduleResponseDto responseDto = from(schedule)
                .where(schedule.scheduleId.eq(scheduleId))
                .select(Projections.constructor(ScheduleResponseDto.class,
                        schedule.scheduleNickname,
                        schedule.scheduleUuid
                ))
                .fetchOne();


        List<DateOfScheduleResponseDto> dateOfScheduleList = from(dateOfSchedule)
                .where(dateOfSchedule.schedule.scheduleId.eq(scheduleId))
                .select(Projections.constructor(DateOfScheduleResponseDto.class,
                        dateOfSchedule.dateOfScheduleStart,
                        dateOfSchedule.dateOfScheduleEnd))
                .fetch();

        responseDto.addAllDateOfSchedule(dateOfScheduleList);

        return responseDto;
    }
}
