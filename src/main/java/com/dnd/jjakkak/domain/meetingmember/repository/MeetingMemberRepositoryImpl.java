package com.dnd.jjakkak.domain.meetingmember.repository;

import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meetingmember.entity.MeetingMember;
import com.dnd.jjakkak.domain.meetingmember.entity.QMeetingMember;
import com.dnd.jjakkak.domain.member.entity.Member;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

/**
 * 회원 모임 Querydsl 레포지토리 구현 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 30.
 */

public class MeetingMemberRepositoryImpl extends QuerydslRepositorySupport implements MeetingMemberRepositoryCustom{

    public MeetingMemberRepositoryImpl() {super(MeetingMember.class);}

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
}
