package com.dnd.jjakkak.domain.schedule.service;

import com.dnd.jjakkak.domain.dateofschedule.dto.request.DateOfScheduleCreateRequestDto;
import com.dnd.jjakkak.domain.dateofschedule.service.DateOfScheduleService;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.exception.MeetingAlreadyEndedException;
import com.dnd.jjakkak.domain.meeting.exception.MeetingFullException;
import com.dnd.jjakkak.domain.meeting.exception.MeetingNotFoundException;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
import com.dnd.jjakkak.domain.meetingmember.service.MeetingMemberService;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.exception.MemberNotFoundException;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleAssignRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleUpdateRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.response.ScheduleAssignResponseDto;
import com.dnd.jjakkak.domain.schedule.dto.response.ScheduleResponseDto;
import com.dnd.jjakkak.domain.schedule.entity.Schedule;
import com.dnd.jjakkak.domain.schedule.exception.InvalidScheduleUuidException;
import com.dnd.jjakkak.domain.schedule.exception.ScheduleAlreadyAssignedException;
import com.dnd.jjakkak.domain.schedule.exception.ScheduleNotFoundException;
import com.dnd.jjakkak.domain.schedule.repository.ScheduleRepository;
import com.dnd.jjakkak.global.annotation.redisson.RedissonLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
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
    private final MemberRepository memberRepository;
    private final MeetingMemberService meetingMemberService;

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
                .scheduleNickname("멤버")
                .scheduleUuid(uuid)
                .build();

        scheduleRepository.save(schedule);
    }

    /**
     * 비회원 일정 할당 메서드입니다.
     *
     * @param meetingUuid 모임 UUID
     * @param requestDto  일정 할당 요청 DTO
     * @return 일정 할당 응답 DTO (UUID)
     */
    @RedissonLock(value = "#meetingUuid")
    public ScheduleAssignResponseDto assignScheduleToGuest(String meetingUuid, ScheduleAssignRequestDto requestDto) {
        // 이미 모임의 인원이 다 찼는가? (400 Bad Request)
        if (meetingRepository.checkMeetingFull(meetingUuid)) {
            throw new MeetingFullException();
        }

        // meetingId로 할당되지 않은 schedule 조회
        Schedule schedule = scheduleRepository.findNotAssignedScheduleByMeetingUuid(meetingUuid)
                .orElseThrow(ScheduleNotFoundException::new);

        // 모임의 일정 마감시간 이후 요청일 경우 예외 처리
        LocalDateTime now = checkMeetingEnded(schedule.getMeeting());

        schedule.changeAssignedAt(now);
        validateAndAssignSchedule(requestDto, schedule);

        return ScheduleAssignResponseDto.builder()
                .scheduleUuid(schedule.getScheduleUuid())
                .build();
    }


    /**
     * 회원 일정 할당 메서드입니다.
     *
     * @param memberId    로그인한 회원 ID
     * @param meetingUuid 모임 UUID
     * @param requestDto  일정 할당 요청 DTO
     */
    @RedissonLock(value = "#meetingUuid")
    public void assignScheduleToMember(Long memberId, String meetingUuid, ScheduleAssignRequestDto requestDto) {

        // 이미 모임의 인원이 다 찼는가? (400 Bad Request)
        if (meetingRepository.checkMeetingFull(meetingUuid)) {
            throw new MeetingFullException();
        }

        // meetingId로 할당되지 않은 schedule 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        if (meetingRepository.existsByMemberIdAndMeetingUuid(memberId, meetingUuid)) {
            throw new ScheduleAlreadyAssignedException();
        }

        Schedule schedule = scheduleRepository.findNotAssignedScheduleByMeetingUuid(meetingUuid)
                .orElseThrow(ScheduleNotFoundException::new);

        schedule.assignMember(member);
        schedule.updateScheduleNickname(member.getMemberNickname());

        // 모임의 일정 마감시간 이후 요청일 경우 예외 처리
        LocalDateTime now = checkMeetingEnded(schedule.getMeeting());

        schedule.changeAssignedAt(now);
        validateAndAssignSchedule(requestDto, schedule);

        meetingMemberService.createMeetingMemberBySchedule(schedule.getScheduleId(), memberId);
    }

    /**
     * 비회원 일정 수정 메서드입니다.
     *
     * @param meetingUuid  모임 UUID
     * @param scheduleUuid 일정 UUID
     * @param requestDto   일정 수정 요청 DTO
     */
    @Transactional
    public void updateGuestSchedule(String meetingUuid, String scheduleUuid, ScheduleUpdateRequestDto requestDto) {

        Schedule schedule = scheduleRepository.findByScheduleUuid(scheduleUuid)
                .orElseThrow(ScheduleNotFoundException::new);

        Meeting meeting = schedule.getMeeting();

        checkMeetingEnded(meeting);

        if (!(meeting.getMeetingUuid().equals(meetingUuid))) {
            throw new MeetingNotFoundException();
        }


        dateOfScheduleService.updateDateList(schedule.getScheduleId(), requestDto.getDateOfScheduleList());
    }

    /**
     * 회원 일정 수정 메서드입니다.
     *
     * @param memberId    회원 ID
     * @param meetingUuid 모임 UUID
     * @param requestDto  일정 수정 요청 DTO
     */
    @Transactional
    public void updateMemberSchedule(Long memberId, String meetingUuid, ScheduleUpdateRequestDto requestDto) {

        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException();
        }

        Schedule schedule = scheduleRepository.findByMemberIdAndMeetingUuid(memberId, meetingUuid)
                .orElseThrow(ScheduleNotFoundException::new);

        Meeting meeting = schedule.getMeeting();
        checkMeetingEnded(meeting);

        dateOfScheduleService.updateDateList(schedule.getScheduleId(), requestDto.getDateOfScheduleList());
    }

    /**
     * 비회원 일정 조회 메서드입니다.
     *
     * @param meetingUuid  모임 UUID
     * @param scheduleUuid 일정 UUID
     * @return 일정 응답 DTO
     */
    @Transactional(readOnly = true)
    public ScheduleResponseDto getGuestSchedule(String meetingUuid, String scheduleUuid) {

        Schedule schedule = scheduleRepository.findByScheduleUuid(scheduleUuid)
                .orElseThrow(InvalidScheduleUuidException::new);

        if (!schedule.getMeeting().getMeetingUuid().equals(meetingUuid)) {
            throw new MeetingNotFoundException();
        }

        schedule.changeAssignedAt(LocalDateTime.now());
        return scheduleRepository.findScheduleWithDateOfSchedule(schedule.getScheduleId());
    }


    /**
     * 회원 일정 조회 메서드입니다.
     *
     * @param meetingUuid 모임 UUID
     * @param memberId    요청 회원 ID
     * @return 일정 응답 DTO
     */
    @Transactional(readOnly = true)
    public ScheduleResponseDto getMemberSchedule(String meetingUuid, Long memberId) {

        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException();
        }

        if (!meetingRepository.existsByMeetingUuid(meetingUuid)) {
            throw new MeetingNotFoundException();
        }

        Schedule schedule = scheduleRepository.findByMemberIdAndMeetingUuid(memberId, meetingUuid)
                .orElseThrow(ScheduleNotFoundException::new);

        return scheduleRepository.findScheduleWithDateOfSchedule(schedule.getScheduleId());
    }

    /**
     * 회원의 일정 작성 여부 확인 메서드입니다.
     *
     * @param meetingUuid 모임 UUID
     * @param memberId    요청 회원 ID
     * @return 회원의 일정 작성 여부
     */
    @Transactional(readOnly = true)
    public Boolean getMemberScheduleWrite(String meetingUuid, Long memberId) {
        Optional<Schedule> schedule = scheduleRepository.findByMemberIdAndMeetingUuid(memberId, meetingUuid);
        return schedule.isPresent();
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

        // 닉네임 변경
        schedule.updateScheduleNickname(requestDto.getNickname() == null ? schedule.getScheduleNickname() : requestDto.getNickname());

        // 일정 날짜 저장
        for (DateOfScheduleCreateRequestDto dateOfScheduleCreateRequestDto : requestDto.getDateOfScheduleList()) {
            dateOfScheduleService.createDateOfSchedule(schedule.getScheduleId(), dateOfScheduleCreateRequestDto);
        }

        // isAssigned -> true
        schedule.scheduleAssign();
    }

    /**
     * 모임의 일정 마감시간 이후 요청일 경우 예외 처리
     *
     * @param meeting 모임 일정
     * @return 현재 시간
     */
    private LocalDateTime checkMeetingEnded(Meeting meeting) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(meeting.getDueDateTime())) {
            throw new MeetingAlreadyEndedException();
        }
        return now;
    }

}
