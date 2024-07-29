package com.dnd.jjakkak.domain.schedule.service;

import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.exception.MeetingFullException;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.exception.MemberNotFoundException;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleAssignRequestDto;
import com.dnd.jjakkak.domain.schedule.entity.Schedule;
import com.dnd.jjakkak.domain.schedule.exception.ScheduleAlreadyAssignedException;
import com.dnd.jjakkak.domain.schedule.exception.ScheduleNotFoundException;
import com.dnd.jjakkak.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * 일정 서비스 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 29.
 */
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;

    @Transactional
    public void createDefaultSchedule(Meeting meeting) {
        // 일정 생성 로직

        // 1. 6자리 랜덤 문자열 생성
        String uuid = UUID.randomUUID().toString().substring(0, 6);

        // DB에 해당 UUID가 존재하는지 확인 후 없으면 생성
        // TODO: 개선 방법이 없을까?
        while (scheduleRepository.existsByScheduleUuid(uuid)) {
            uuid = UUID.randomUUID().toString().substring(0, 6);
        }

        // 2. 일정 생성
        Schedule schedule = Schedule.builder()
                .meeting(meeting)
                .scheduleNickname("익명")
                .scheduleUuid(uuid)
                .build();

        scheduleRepository.save(schedule);
    }

    /**
     * 일정을 할당하는 메서드입니다.
     *
     * @param scheduleUuid 일정 UUID
     * @param requestDto   일정 할당 요청 DTO
     */
    @Transactional
    public void assignSchedule(String scheduleUuid, ScheduleAssignRequestDto requestDto) {

        // uuid로 일정을 찾기
        Schedule schedule = scheduleRepository.findByScheduleUuid(scheduleUuid)
                .orElseThrow(ScheduleNotFoundException::new);

        // TODO 1: 이미 할당된 일정인가? (409 Conflict)
        if (Boolean.TRUE.equals(schedule.getIsAssigned())) {
            throw new ScheduleAlreadyAssignedException();
        }

        // TODO 2: 이미 모임의 인원이 다 찼는가? (
        if (meetingRepository.checkMeetingFull(schedule.getMeeting().getMeetingId())) {
            throw new MeetingFullException();
        }

        // 회원 / 비회원을 나눠서 처리
        if (Objects.nonNull(requestDto.getMemberId())) {
            Member member = memberRepository.findById(requestDto.getMemberId())
                    .orElseThrow(MemberNotFoundException::new);

            schedule.assignMember(member);
        }

        schedule.updateScheduleNickname(requestDto.getNickName());
    }
}
