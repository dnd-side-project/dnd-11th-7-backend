package com.dnd.jjakkak.domain.meeting.repository;

import com.dnd.jjakkak.domain.dateofschedule.entity.QDateOfSchedule;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.entity.QMeeting;
import com.dnd.jjakkak.domain.schedule.entity.QSchedule;
import com.querydsl.core.types.Projections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

/**
 * 모임 Querydsl 레포지토리 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 29.
 */
@Slf4j
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
                .where(schedule.meeting.meetingId.eq(meetingId)
                        .and(schedule.isAssigned.isTrue()))
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
    public MeetingResponseDto findByMeetingUuidWithBestTime(String uuid) {
        QMeeting meeting = QMeeting.meeting;
        QSchedule schedule = QSchedule.schedule;
        QDateOfSchedule dateOfSchedule = QDateOfSchedule.dateOfSchedule;

        // 1. 모임의 UUID로 모임을 조회한다.
        MeetingResponseDto responseDto = from(meeting)
                .where(meeting.meetingUuid.eq(uuid))
                .select(Projections.constructor(MeetingResponseDto.class,
                        meeting.meetingId,
                        meeting.meetingName,
                        meeting.meetingStartDate,
                        meeting.meetingEndDate,
                        meeting.numberOfPeople,
                        meeting.isAnonymous,
                        meeting.voteEndDate,
                        meeting.meetingLeaderId,
                        meeting.meetingUuid))
                .fetchOne();

        log.info("responseDto: {}", responseDto);

        // 2. 모임의 일정과 일정 날짜에서 우선순위가 가장 높은 2개를 조회한다.
        /**
         * SELECT
         *     ds.date_of_schedule_start,
         *     ds.date_of_schedule_end,
         *     AVG(ds.date_of_schedule_rank) AS avg_rank
         * FROM `DateOfSchedule` ds
         * JOIN `Schedule` s ON ds.schedule_id = s.schedule_id
         * JOIN `Meeting` m ON s.meeting_id = m.meeting_id
         * WHERE m.meeting_uuid = 1234 -- 조회할 모임 UUID
         * GROUP BY ds.date_of_schedule_start, ds.date_of_schedule_end
         * ORDER BY avg_rank ASC -- 우선순위가 낮을수록 높은 우선순위로 간주
         * LIMIT 2;
         */

        List<MeetingResponseDto.BestTime> bestTimeList = from(dateOfSchedule)
                .join(dateOfSchedule.schedule, schedule)
                .join(schedule.meeting, meeting)
                .where(meeting.meetingUuid.eq(uuid))
                .groupBy(dateOfSchedule.dateOfScheduleStart, dateOfSchedule.dateOfScheduleEnd)
                .orderBy(dateOfSchedule.dateOfScheduleRank.avg().asc())
                .limit(2)
                .select(Projections.constructor(MeetingResponseDto.BestTime.class,
                        dateOfSchedule.dateOfScheduleStart,
                        dateOfSchedule.dateOfScheduleEnd))
                .fetch();

        log.info("bestTimeList: {}", bestTimeList);

        // 3. 각 시간대에 해당하는 닉네임 리스트를 조회하여 BestTime 객체에 추가한다.
        for (MeetingResponseDto.BestTime bestTime : bestTimeList) {
            List<String> nicknames = from(schedule)
                    .join(dateOfSchedule.schedule, schedule)
                    .where(schedule.meeting.meetingUuid.eq(uuid)
                            .and(dateOfSchedule.dateOfScheduleStart.eq(bestTime.getStartTime()))
                            .and(dateOfSchedule.dateOfScheduleEnd.eq(bestTime.getEndTIme())))
                    .select(schedule.scheduleNickname)
                    .fetch();

            log.info("nicknames: {}", nicknames);

            bestTime.getMemberNickname().addAll(nicknames);
        }

        // 4. 최종적으로 BestTime 리스트를 MeetingResponseDto에 추가한다.
        if (!bestTimeList.isEmpty()) {
            bestTimeList.forEach(responseDto::addBestTime);
        }

        return responseDto;
    }
}
