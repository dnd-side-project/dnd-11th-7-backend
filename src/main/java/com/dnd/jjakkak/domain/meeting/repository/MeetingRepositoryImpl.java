package com.dnd.jjakkak.domain.meeting.repository;

import com.dnd.jjakkak.domain.category.entity.QCategory;
import com.dnd.jjakkak.domain.dateofschedule.entity.QDateOfSchedule;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingInfoResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingParticipantResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingTime;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingTimeResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.entity.QMeeting;
import com.dnd.jjakkak.domain.meeting.exception.MeetingNotFoundException;
import com.dnd.jjakkak.domain.meetingcategory.entity.QMeetingCategory;
import com.dnd.jjakkak.domain.member.entity.QMember;
import com.dnd.jjakkak.domain.schedule.entity.QSchedule;
import com.dnd.jjakkak.global.common.PageInfo;
import com.dnd.jjakkak.global.common.PagedResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public boolean checkMeetingFull(String meetingUuid) {
        QMeeting meeting = QMeeting.meeting;
        QSchedule schedule = QSchedule.schedule;

        Integer maxPeople = from(meeting)
                .where(meeting.meetingUuid.eq(meetingUuid))
                .select(meeting.numberOfPeople)
                .fetchOne();

        Long currentPeople = from(schedule)
                .where(schedule.meeting.meetingUuid.eq(meetingUuid)
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
    public PagedResponse<MeetingTimeResponseDto> getMeetingTimes(String uuid, Pageable pageable, LocalDateTime requestTime) {

        QMeeting meeting = QMeeting.meeting;
        QSchedule schedule = QSchedule.schedule;
        QDateOfSchedule dateOfSchedule = QDateOfSchedule.dateOfSchedule;

        // 1. order by 설정
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        pageable.getSort().stream()
                .forEach(sort -> {
                    switch (sort.getProperty()) {
                        case "count" -> orderSpecifiers.addAll(
                                List.of(
                                        dateOfSchedule.dateOfScheduleRank.count().desc(),
                                        dateOfSchedule.dateOfScheduleRank.avg().asc(),
                                        dateOfSchedule.dateOfScheduleStart.asc(),
                                        dateOfSchedule.dateOfScheduleEnd.asc()
                                )
                        );
                        case "latest" -> orderSpecifiers.add(dateOfSchedule.dateOfScheduleStart.asc());
                        default -> throw new MeetingNotFoundException();
                    }
                });


        // 2. 페이징 데이터 및 전체 요소 수 조회
        List<MeetingTime> meetingTimeList = from(dateOfSchedule)
                .join(dateOfSchedule.schedule, schedule)
                .join(schedule.meeting, meeting)
                .where(meeting.meetingUuid.eq(uuid)
                        .and(schedule.assignedAt.loe(requestTime)))
                .groupBy(dateOfSchedule.dateOfScheduleStart, dateOfSchedule.dateOfScheduleEnd)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .select(Projections.constructor(MeetingTime.class,
                        dateOfSchedule.dateOfScheduleStart,
                        dateOfSchedule.dateOfScheduleEnd,
                        dateOfSchedule.dateOfScheduleRank.avg()
                ))
                .fetch();


        long totalElements = from(dateOfSchedule)
                .join(dateOfSchedule.schedule, schedule)
                .join(schedule.meeting, meeting)
                .where(meeting.meetingUuid.eq(uuid)
                        .and(schedule.assignedAt.isNotNull())
                        .and(schedule.assignedAt.loe(requestTime)))
                .groupBy(dateOfSchedule.dateOfScheduleStart, dateOfSchedule.dateOfScheduleEnd)
                .select(dateOfSchedule.dateOfScheduleRank.count())
                .fetchCount();

        // 3. 익명 모임이 아닌 경우, 일정을 할당한 사용자의 닉네임 조회 후 추가
        Boolean isAnonymous = from(meeting)
                .where(meeting.meetingUuid.eq(uuid))
                .select(meeting.isAnonymous)
                .fetchOne();

        if (Boolean.FALSE.equals(isAnonymous)) {
            for (MeetingTime meetingTime : meetingTimeList) {
                List<String> nicknames = from(dateOfSchedule)
                        .join(dateOfSchedule.schedule, schedule)
                        .join(schedule.meeting, meeting)
                        .where(meeting.meetingUuid.eq(uuid)
                                .and(dateOfSchedule.dateOfScheduleStart.eq(meetingTime.getStartTime()))
                                .and(dateOfSchedule.dateOfScheduleEnd.eq(meetingTime.getEndTime()))
                                .and(schedule.assignedAt.loe(requestTime)))
                        .select(schedule.scheduleNickname)
                        .fetch();

                meetingTime.addMemberNames(nicknames);
            }
        }

        // 4. PageInfo 생성
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
        PageInfo pageInfo = PageInfo.builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements((int) totalElements)
                .totalPages(totalPages)
                .build();

        // 5. 응답 DTO 생성
        MeetingTimeResponseDto responseDto = from(meeting)
                .where(meeting.meetingUuid.eq(uuid))
                .select(Projections.constructor(MeetingTimeResponseDto.class,
                        meeting.numberOfPeople,
                        meeting.isAnonymous,
                        meeting.meetingStartDate,
                        meeting.meetingEndDate
                ))
                .fetchOne();

        responseDto.addMeetingTimeList(meetingTimeList);

        return new PagedResponse<>(responseDto, pageInfo);
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
                        schedule.member.memberId.eq(meeting.meetingLeaderId)
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

        QSchedule schedule = QSchedule.schedule;
        QMeeting meeting = QMeeting.meeting;
        QMember member = QMember.member;

        return from(schedule)
                .join(schedule.meeting, meeting)
                .join(schedule.member, member)
                .where(meeting.meetingUuid.eq(meetingUuid)
                        .and(member.memberId.eq(memberId)))
                .fetchCount() > 0;

    }

    @Override
    public LocalDateTime getBestTime(String uuid) {

        QMeeting meeting = QMeeting.meeting;
        QSchedule schedule = QSchedule.schedule;
        QDateOfSchedule dateOfSchedule = QDateOfSchedule.dateOfSchedule;

        return from(dateOfSchedule)
                .join(dateOfSchedule.schedule, schedule)
                .join(schedule.meeting, meeting)
                .where(meeting.meetingUuid.eq(uuid))
                .groupBy(dateOfSchedule.dateOfScheduleStart, dateOfSchedule.dateOfScheduleEnd)
                .orderBy(dateOfSchedule.dateOfScheduleRank.count().desc(),
                        dateOfSchedule.dateOfScheduleRank.avg().asc(),
                        dateOfSchedule.dateOfScheduleStart.asc())
                .select(dateOfSchedule.dateOfScheduleStart)
                .fetchFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeetingTimeResponseDto getMeetingAllTimes(String uuid) {

        QMeeting meeting = QMeeting.meeting;
        QSchedule schedule = QSchedule.schedule;
        QDateOfSchedule dateOfSchedule = QDateOfSchedule.dateOfSchedule;

        // 빠른 시간 순으로 조회
        List<MeetingTime> meetingTimeList = from(dateOfSchedule)
                .join(dateOfSchedule.schedule, schedule)
                .join(schedule.meeting, meeting)
                .where(meeting.meetingUuid.eq(uuid))
                .groupBy(dateOfSchedule.dateOfScheduleStart, dateOfSchedule.dateOfScheduleEnd)
                .orderBy(dateOfSchedule.dateOfScheduleStart.asc())
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
                        meeting.isAnonymous,
                        meeting.meetingStartDate,
                        meeting.meetingEndDate
                ))
                .fetchOne();

        responseDto.addMeetingTimeList(meetingTimeList);

        return responseDto;
    }
}
