package com.dnd.jjakkak.domain.meetingmember.service;

import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
import com.dnd.jjakkak.domain.meetingmember.entity.MeetingMember;
import com.dnd.jjakkak.domain.meetingmember.repository.MeetingMemberRepository;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.exception.MemberNotFoundException;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import com.dnd.jjakkak.domain.schedule.entity.Schedule;
import com.dnd.jjakkak.domain.schedule.exception.ScheduleNotFoundException;
import com.dnd.jjakkak.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 모임 회원 서비스 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 09. 18.
 */
@Service
@RequiredArgsConstructor
public class MeetingMemberService {


    private final MeetingMemberRepository meetingMemberRepository;
    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;


    /**
     * 모임에 회원에 대한 정보를 생성합니다.
     *
     * @param scheduleId 모임 일정 ID
     * @param memberId   회원 ID
     */
    @Transactional
    public void createMeetingMemberBySchedule(Long scheduleId, Long memberId) {
        MeetingMember.Pk pk = new MeetingMember.Pk(scheduleId, memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);

        // 이미 리더는 모임을 생성할 때 회원으로 추가되어 있으므로 제외
        if (!schedule.getMeeting().getMeetingLeaderId().equals(memberId)) {
            MeetingMember meetingMember = MeetingMember.builder()
                    .pk(pk)
                    .member(member)
                    .meeting(schedule.getMeeting())
                    .build();

            meetingMemberRepository.save(meetingMember);
        }
    }

    /**
     * 모임에 회원에 대한 정보를 생성합니다.
     *
     * @param meetingId 모임 ID
     * @param memberId  회원 ID
     */
    @Transactional
    public void createMeetingMemberByMeeting(Long meetingId, Long memberId) {
        MeetingMember.Pk pk = new MeetingMember.Pk(meetingId, memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(ScheduleNotFoundException::new);

        MeetingMember meetingMember = MeetingMember.builder()
                .pk(pk)
                .member(member)
                .meeting(meeting)
                .build();

        meetingMemberRepository.save(meetingMember);
    }

}
