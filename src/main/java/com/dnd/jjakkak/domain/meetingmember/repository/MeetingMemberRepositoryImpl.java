package com.dnd.jjakkak.domain.meetingmember.repository;

import com.dnd.jjakkak.domain.category.entity.QCategory;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingMyPageResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.entity.QMeeting;
import com.dnd.jjakkak.domain.meetingcategory.entity.QMeetingCategory;
import com.dnd.jjakkak.domain.meetingmember.entity.MeetingMember;
import com.dnd.jjakkak.domain.meetingmember.entity.QMeetingMember;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.schedule.entity.QSchedule;
import com.querydsl.core.types.Projections;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

/**
 * 회원 모임 Querydsl 레포지토리 구현 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 30.
 */

public class MeetingMemberRepositoryImpl extends QuerydslRepositorySupport implements MeetingMemberRepositoryCustom {

    public MeetingMemberRepositoryImpl() {
        super(MeetingMember.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Meeting> findByMemberId(Long memberId) {
        QMeetingMember meetingMember = QMeetingMember.meetingMember;
        return from(meetingMember)
                .select(meetingMember.meeting)
                .where(meetingMember.pk.memberId.eq(memberId))
                .fetch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Member> findByMeetingId(Long meetingId) {
        QMeetingMember meetingMember = QMeetingMember.meetingMember;
        return from(meetingMember)
                .select(meetingMember.member)
                .where(meetingMember.pk.meetingId.eq(meetingId))
                .fetch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MeetingMyPageResponseDto> findMeetingInfoByMemberId(Long memberId) {
        QMeetingMember meetingMember = QMeetingMember.meetingMember;
        QMeeting meeting = QMeeting.meeting;
        QMeetingCategory meetingCategory = QMeetingCategory.meetingCategory;
        QSchedule schedule = QSchedule.schedule;
        QCategory category = QCategory.category;

        List<MeetingMyPageResponseDto> responseDtoList = from(meetingMember)
                .join(meetingMember.meeting, meeting)
                .join(schedule).on(schedule.member.memberId.eq(meeting.meetingLeaderId))
                .where(meetingMember.pk.memberId.eq(memberId))
                .select(Projections.constructor(MeetingMyPageResponseDto.class,
                        meeting.meetingId,
                        meeting.meetingName,
                        meeting.meetingUuid,
                        meeting.meetingStartDate,
                        meeting.meetingEndDate,
                        meeting.dueDateTime,
                        meeting.numberOfPeople,
                        meeting.isAnonymous,
                        schedule.scheduleNickname
                ))
                .distinct()
                .fetch();

        for (MeetingMyPageResponseDto responseDto : responseDtoList) {

            List<String> categoryNames = from(meetingCategory)
                    .join(meetingCategory.meeting, meeting)
                    .join(meetingCategory.category, category)
                    .where(meeting.meetingId.eq(responseDto.getMeetingId()))
                    .select(category.categoryName)
                    .fetch();

            responseDto.addCategoryNames(categoryNames);
        }

        return responseDtoList;
    }
}
