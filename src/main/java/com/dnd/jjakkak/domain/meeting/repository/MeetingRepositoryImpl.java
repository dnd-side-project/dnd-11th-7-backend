package com.dnd.jjakkak.domain.meeting.repository;

import com.dnd.jjakkak.domain.category.entity.QCategory;
import com.dnd.jjakkak.domain.dateofschedule.entity.QDateOfSchedule;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingInfoResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingParticipantResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingTimeResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.entity.QMeeting;
import com.dnd.jjakkak.domain.meetingcategory.entity.QMeetingCategory;
import com.dnd.jjakkak.domain.schedule.entity.QSchedule;
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
                        meeting.meetingEndDate
                ))
                .fetchOne();

        // 2. 카테고리
        List<String> categoryNames = from(meetingCategory)
                .join(meetingCategory.category, category)
                .where(meetingCategory.meeting.eq(meeting))
                .select(category.categoryName)
                .fetch();

        response.addCategoryName(categoryNames);

        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MeetingTimeResponseDto> getBestTime(String uuid) {

        // TODO : 성능 개선 필요해보임
        QMeeting meeting = QMeeting.meeting;
        QSchedule schedule = QSchedule.schedule;
        QDateOfSchedule dateOfSchedule = QDateOfSchedule.dateOfSchedule;

        // 우선순위 순으로 최적 시간 조회
        List<MeetingTimeResponseDto> responseDtoList = from(dateOfSchedule)
                .join(dateOfSchedule.schedule, schedule)
                .join(schedule.meeting, meeting)
                .where(meeting.meetingUuid.eq(uuid))
                .groupBy(dateOfSchedule.dateOfScheduleStart, dateOfSchedule.dateOfScheduleEnd)
                .orderBy(
                        dateOfSchedule.dateOfScheduleRank.count().desc(),
                        dateOfSchedule.dateOfScheduleRank.avg().asc(),
                        dateOfSchedule.dateOfScheduleStart.asc(),
                        dateOfSchedule.dateOfScheduleEnd.asc()
                )
                .select(Projections.constructor(MeetingTimeResponseDto.class,
                        dateOfSchedule.dateOfScheduleStart,
                        dateOfSchedule.dateOfScheduleEnd,
                        dateOfSchedule.dateOfScheduleRank.avg()
                ))
                .fetch();


        // 각 시간대에 해당하는 닉네임 리스트를 조회하여 BestTime 객체에 추가한다.
        for (MeetingTimeResponseDto responseDto : responseDtoList) {
            List<String> nicknames = from(dateOfSchedule)
                    .join(dateOfSchedule.schedule, schedule)
                    .join(schedule.meeting, meeting)
                    .where(meeting.meetingUuid.eq(uuid)
                            .and(dateOfSchedule.dateOfScheduleStart.eq(responseDto.getStartTime()))
                            .and(dateOfSchedule.dateOfScheduleEnd.eq(responseDto.getEndTime())))
                    .select(schedule.scheduleNickname)
                    .fetch();

            responseDto.addMemberNames(nicknames);
        }

        return responseDtoList;
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
                        schedule.isAssigned
                ))
                .fetch();

        responseDto.addParticipantInfoList(infoList);

        return responseDto;
    }
}
