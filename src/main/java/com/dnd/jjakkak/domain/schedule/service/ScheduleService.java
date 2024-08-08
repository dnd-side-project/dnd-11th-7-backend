package com.dnd.jjakkak.domain.schedule.service;

import com.dnd.jjakkak.domain.dateofschedule.dto.request.DateOfScheduleCreateRequestDto;
import com.dnd.jjakkak.domain.dateofschedule.service.DateOfScheduleService;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.exception.MeetingFullException;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
import com.dnd.jjakkak.domain.meetingmember.entity.MeetingMember;
import com.dnd.jjakkak.domain.meetingmember.repository.MeetingMemberRepository;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleAssignRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleUpdateRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.response.ScheduleAssignResponseDto;
import com.dnd.jjakkak.domain.schedule.dto.response.ScheduleResponseDto;
import com.dnd.jjakkak.domain.schedule.entity.Schedule;
import com.dnd.jjakkak.domain.schedule.exception.ScheduleAlreadyAssignedException;
import com.dnd.jjakkak.domain.schedule.exception.ScheduleNotFoundException;
import com.dnd.jjakkak.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    private final MeetingRepository meetingRepository;
    private final DateOfScheduleService dateOfScheduleService;
    private final MeetingMemberRepository meetingMemberRepository;

    /**
     * 기본 일정을 생성하는 메서드입니다.
     *
     * @param meeting 모임
     */
    @Transactional
    public void createDefaultSchedule(Meeting meeting) {

        // 6자리 랜덤 문자열 생성
        String uuid = generatedUuid();

        // 일정 생성
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
     * @param requestDto 일정 할당 요청 DTO
     */
    @Transactional
    public ScheduleAssignResponseDto assignScheduleToGuest(ScheduleAssignRequestDto requestDto) {

        // meetingId로 할당되지 않은 schedule 조회
        Schedule schedule = scheduleRepository.findNotAssignedScheduleByMeetingId(requestDto.getMeetingId())
                .orElseThrow(ScheduleNotFoundException::new);

        validateAndAssignSchedule(requestDto, schedule);

        return ScheduleAssignResponseDto.builder()
                .scheduleUuid(schedule.getScheduleUuid())
                .build();
    }

    /**
     * 회원 일정 할당 메서드입니다.
     *
     * @param user       인증된 회원 정보 (Member - OAuth2User)
     * @param requestDto 일정 할당 요청 DTO
     */
    @Transactional
    public void assignScheduleToMember(OAuth2User user, ScheduleAssignRequestDto requestDto) {

        // meetingId로 할당되지 않은 schedule 조회

        Member member = (Member) user;

        Schedule schedule = scheduleRepository.findNotAssignedScheduleByMeetingId(requestDto.getMeetingId())
                .orElseThrow(ScheduleNotFoundException::new);

        schedule.assignMember(member);
        validateAndAssignSchedule(requestDto, schedule);

        // TODO : MeetingMember 객체를 만들어서 DB에 저장
        MeetingMember meetingMember = MeetingMember.builder()
                .member(member)
                .meeting(schedule.getMeeting())
                .build();

        meetingMemberRepository.save(meetingMember);
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

    /**
     * 비회원 일정 조회 메서드입니다.
     *
     * @param meetingId 모임 ID
     * @param uuid      일정 UUID
     * @return 일정 응답 DTO
     */
    @Transactional(readOnly = true)
    public ScheduleResponseDto getGuestSchedule(Long meetingId, String uuid) {

        Schedule schedule = scheduleRepository.findByScheduleUuid(uuid)
                .orElseThrow(ScheduleNotFoundException::new);

        if (!schedule.getMeeting().getMeetingId().equals(meetingId)) {
            throw new ScheduleNotFoundException();
        }

        return new ScheduleResponseDto(schedule);
    }


    /**
     * 회원 일정 조회 메서드입니다.
     *
     * @param meetingId 모임 ID
     * @param user      인증된 회원 정보 (Member - OAuth2User)
     * @return 일정 응답 DTO
     */
    @Transactional(readOnly = true)
    public ScheduleResponseDto getMemberSchedule(Long meetingId, OAuth2User user) {

        Member member = (Member) user;

        Schedule schedule = scheduleRepository.findByMemberIdAndMeetingId(member.getMemberId(), meetingId)
                .orElseThrow(ScheduleNotFoundException::new);

        return new ScheduleResponseDto(schedule);
    }

    /**
     * UUID 생성 메서드입니다.
     *
     * @return 중복되지 않은 UUID
     */
    private String generatedUuid() {
        String uuid;
        do {
            uuid = UUID.randomUUID().toString().substring(0, 6);
        } while (scheduleRepository.existsByScheduleUuid(uuid));

        return uuid;
    }

    /**
     * 일정 유효성 검사 및 할당 메서드입니다.
     *
     * @param requestDto 일정 할당 요청 DTO
     * @param schedule   할당할 일정
     */
    private void validateAndAssignSchedule(ScheduleAssignRequestDto requestDto, Schedule schedule) {

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
}