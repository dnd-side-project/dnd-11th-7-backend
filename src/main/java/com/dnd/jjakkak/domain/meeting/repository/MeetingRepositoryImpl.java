package com.dnd.jjakkak.domain.meeting.repository;

import com.dnd.jjakkak.domain.category.entity.QCategory;
import com.dnd.jjakkak.domain.dateofschedule.entity.QDateOfSchedule;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingInfoResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingParticipantResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingTime;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingTimeResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.entity.QMeeting;
import com.dnd.jjakkak.domain.meeting.enums.MeetingSort;
import com.dnd.jjakkak.domain.meetingcategory.entity.QMeetingCategory;
import com.dnd.jjakkak.domain.schedule.entity.QSchedule;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

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
    public MeetingInfoResponseDto getMeetingInfo(String uuid) {
        QMeeting meeting = QMeeting.meeting;
        QMeetingCategory meetingCategory = QMeetingCategory.meetingCategory;
        QCategory category = QCategory.category;

        // 1. 모임 정보
        MeetingInfoResponseDto response = from(meeting)
                .where(meeting.meetingUuid.eq(uuid))
                .select(Projections.constructor(MeetingInfoResponseDto.class,
                        meeting.meetingId,
                        meeting.meetingName,
                        meeting.meetingStartDate,
                        meeting.meetingEndDate,
                        meeting.dueDateTime
                ))
                .fetchOne();

        // 2. 카테고리
        List<String> categoryNames = from(meetingCategory)
                .join(meetingCategory.category, category)
                .where(meetingCategory.meeting.meetingUuid.eq(uuid))
                .select(category.categoryName)
                .fetch();

        response.addCategoryNames(categoryNames);

        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeetingTimeResponseDto getMeetingTimes(String uuid, MeetingSort sort) {

        // TODO : 성능 개선 필요해보임
        QMeeting meeting = QMeeting.meeting;
        QSchedule schedule = QSchedule.schedule;
        QDateOfSchedule dateOfSchedule = QDateOfSchedule.dateOfSchedule;

        List<OrderSpecifier<?>> orderSpecifier = switch (sort) {
            case COUNT -> List.of(dateOfSchedule.dateOfScheduleRank.count().desc(),
                    dateOfSchedule.dateOfScheduleRank.avg().asc(),
                    dateOfSchedule.dateOfScheduleStart.asc(),
                    dateOfSchedule.dateOfScheduleEnd.asc());
            case LATEST -> List.of(dateOfSchedule.dateOfScheduleStart.asc());
        };

        // 우선순위 순으로 최적 시간 조회
        List<MeetingTime> meetingTimeList = from(dateOfSchedule)
                .join(dateOfSchedule.schedule, schedule)
                .join(schedule.meeting, meeting)
                .where(meeting.meetingUuid.eq(uuid))
                .groupBy(dateOfSchedule.dateOfScheduleStart, dateOfSchedule.dateOfScheduleEnd)
                .orderBy(orderSpecifier.toArray(new OrderSpecifier[0]))
                .select(Projections.constructor(MeetingTime.class,
                        dateOfSchedule.dateOfScheduleStart,
                        dateOfSchedule.dateOfScheduleEnd,
                        dateOfSchedule.dateOfScheduleRank.avg()
                ))
                .fetch();

        for (MeetingTime meetingTime : meetingTimeList) {
            List<String> nicknames = from(dateOfSchedule)
                    .join(dateOfSchedule.schedule, schedule)
                    .join(schedule.meeting, meeting)
                    .where(meeting.meetingUuid.eq(uuid)
                            .and(dateOfSchedule.dateOfScheduleStart.eq(meetingTime.getStartTime()))
                            .and(dateOfSchedule.dateOfScheduleEnd.eq(meetingTime.getEndTime())))
                    .select(schedule.scheduleNickname)
                    .fetch();

            meetingTime.addMemberNames(nicknames);
        }

        MeetingTimeResponseDto responseDto = from(meeting)
                .where(meeting.meetingUuid.eq(uuid))
                .select(Projections.constructor(MeetingTimeResponseDto.class,
                        meeting.numberOfPeople,
                        meeting.isAnonymous
                ))
                .fetchOne();

        responseDto.addMeetingTimeList(meetingTimeList);

        return responseDto;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public MeetingParticipantResponseDto getParticipant(String uuid) {
        QMeeting meeting = QMeeting.meeting;
        QSchedule schedule = QSchedule.schedule;

        MeetingParticipantResponseDto responseDto = from(meeting)
                .where(meeting.meetingUuid.eq(uuid))
                .select(Projections.constructor(MeetingParticipantResponseDto.class,
                        meeting.numberOfPeople,
                        meeting.isAnonymous))
                .fetchOne();

        List<MeetingParticipantResponseDto.ParticipantInfo> infoList = from(schedule)
                .join(schedule.meeting, meeting)
                .where(meeting.meetingUuid.eq(uuid))
                .select(Projections.constructor(MeetingParticipantResponseDto.ParticipantInfo.class,
                        schedule.scheduleNickname,
                        schedule.isAssigned,
                        meeting.meetingLeaderId.eq(schedule.member.memberId)
                ))
                .fetch();

        responseDto.addParticipantInfoList(infoList);

        return responseDto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByMemberIdAndMeetingUuid(Long memberId, String meetingUuid) {

        QMeeting meeting = QMeeting.meeting;
        QSchedule schedule = QSchedule.schedule;

        return from(schedule)
                .join(schedule.meeting, meeting)
                .where(meeting.meetingUuid.eq(meetingUuid)
                        .and(schedule.member.memberId.eq(memberId)))
                .select(schedule.count())
                .fetchOne() > 0;
    }
}
