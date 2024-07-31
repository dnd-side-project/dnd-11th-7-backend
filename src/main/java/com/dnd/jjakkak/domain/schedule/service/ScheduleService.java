package com.dnd.jjakkak.domain.schedule.service;

import com.dnd.jjakkak.domain.dateofschedule.dto.request.DateOfScheduleCreateRequestDto;
import com.dnd.jjakkak.domain.dateofschedule.service.DateOfScheduleService;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.exception.MeetingFullException;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.exception.MemberNotFoundException;
import com.dnd.jjakkak.domain.member.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleAssignRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleUpdateRequestDto;
import com.dnd.jjakkak.domain.schedule.entity.Schedule;
import com.dnd.jjakkak.domain.schedule.exception.ScheduleAlreadyAssignedException;
import com.dnd.jjakkak.domain.schedule.exception.ScheduleNotFoundException;
import com.dnd.jjakkak.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final DateOfScheduleService dateOfScheduleService;
    private final JwtProvider jwtProvider;

    /**
     * 기본 일정을 생성하는 메서드입니다.
     *
     * @param meeting 모임
     */
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
     * 비회원 일정 할당 메서드입니다.
     *
     * @param scheduleUuid 일정 UUID
     * @param requestDto   일정 할당 요청 DTO
     */
    @Transactional
    public void assignNonMember(String scheduleUuid, ScheduleAssignRequestDto requestDto) {

        // uuid로 일정을 찾기
        Schedule schedule = scheduleRepository.findByScheduleUuid(scheduleUuid)
                .orElseThrow(ScheduleNotFoundException::new);

        // 이미 할당된 일정인가? (409 Conflict)
        if (Boolean.TRUE.equals(schedule.getIsAssigned())) {
            throw new ScheduleAlreadyAssignedException();
        }

        // 이미 모임의 인원이 다 찼는가? (400 Bad Request)
        if (meetingRepository.checkMeetingFull(schedule.getMeeting().getMeetingId())) {
            throw new MeetingFullException();
        }

        // 익명인 경우에는 닉네임을 변경하지 않음
        if (!meetingRepository.isAnonymous(schedule.getMeeting().getMeetingId())) {
            schedule.updateScheduleNickname(requestDto.getNickName());
        }

        // 일정 날짜 저장
        for (DateOfScheduleCreateRequestDto dateOfScheduleCreateRequestDto : requestDto.getDateOfScheduleList()) {
            dateOfScheduleService.createDateOfSchedule(schedule.getScheduleId(), dateOfScheduleCreateRequestDto);
        }

        // isAssigned -> true
        schedule.scheduleAssign();
    }

    /**
     * 회원 일정 할당 메서드입니다.
     *
     * @param authorization Header AccessToken 값
     * @param requestDto    일정 할당 요청 DTO
     */
    @Transactional
    public void assignMember(String authorization, ScheduleAssignRequestDto requestDto) {

        // accessToken에서 memberId 추출
        String kakaoId = jwtProvider.validate(authorization);

        // kakaoId로 회원 조회
        Member member = memberRepository.findByKakaoId(Long.parseLong(kakaoId))
                .orElseThrow(MemberNotFoundException::new);


        // memberId, meetingId로 schedule 조회
        Schedule schedule = scheduleRepository.findByMemberIdAndMeetingId(member.getMemberId(), requestDto.getMeetingId())
                .orElseThrow(ScheduleNotFoundException::new);

        // 이미 할당된 일정인가? (409 Conflict)
        if (Boolean.TRUE.equals(schedule.getIsAssigned())) {
            throw new ScheduleAlreadyAssignedException();
        }

        // 이미 모임의 인원이 다 찼는가? (400 Bad Request)
        if (meetingRepository.checkMeetingFull(schedule.getMeeting().getMeetingId())) {
            throw new MeetingFullException();
        }

        // 닉네임 변경
        if (!meetingRepository.isAnonymous(schedule.getMeeting().getMeetingId())) {
            schedule.updateScheduleNickname(requestDto.getNickName());
        }

        // 일정 날짜 저장
        for (DateOfScheduleCreateRequestDto dateOfScheduleCreateRequestDto : requestDto.getDateOfScheduleList()) {
            dateOfScheduleService.createDateOfSchedule(schedule.getScheduleId(), dateOfScheduleCreateRequestDto);
        }

        // isAssigned -> true
        schedule.scheduleAssign();
    }

    /**
     * 일정을 수정하는 메서드입니다.
     *
     * @param scheduleId 일정 ID
     * @param requestDto 일정 수정 요청 DTO
     */
    @Transactional
    public void updateSchedule(Long scheduleId, ScheduleUpdateRequestDto requestDto) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);

        schedule.updateScheduleNickname(requestDto.getScheduleNickname());

        dateOfScheduleService.updateDateList(scheduleId, requestDto.getDateOfScheduleList());
    }
}
