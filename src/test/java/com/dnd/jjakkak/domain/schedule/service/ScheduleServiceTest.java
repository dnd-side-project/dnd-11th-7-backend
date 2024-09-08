package com.dnd.jjakkak.domain.schedule.service;

import com.dnd.jjakkak.domain.dateofschedule.service.DateOfScheduleService;
import com.dnd.jjakkak.domain.meeting.MeetingDummy;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.exception.MeetingNotFoundException;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
import com.dnd.jjakkak.domain.meetingmember.repository.MeetingMemberRepository;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.exception.MemberNotFoundException;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import com.dnd.jjakkak.domain.schedule.ScheduleDummy;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleAssignRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleUpdateRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.response.ScheduleResponseDto;
import com.dnd.jjakkak.domain.schedule.entity.Schedule;
import com.dnd.jjakkak.domain.schedule.exception.ScheduleAlreadyAssignedException;
import com.dnd.jjakkak.domain.schedule.exception.ScheduleNotFoundException;
import com.dnd.jjakkak.domain.schedule.repository.ScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 일정 서비스 테스트입니다.
 *
 * @author 정승조
 * @version 2024. 09. 06.
 */
@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    private static final String MEETING_UUID = "met123";
    private static final String SCHEDULE_UUID = "sch123";
    @InjectMocks
    ScheduleService scheduleService;
    @Mock
    ScheduleRepository scheduleRepository;
    @Mock
    MeetingRepository meetingRepository;
    @Mock
    DateOfScheduleService dateOfScheduleService;
    @Mock
    MeetingMemberRepository meetingMemberRepository;
    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("기본 일정 생성")
    void create_default_schedule() {
        // given
        Meeting meeting = MeetingDummy.createMeeting();

        // when
        scheduleService.createDefaultSchedule(meeting);

        // then
        verify(scheduleRepository).existsByScheduleUuid(any());
        verify(scheduleRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("비회원 일정 할당 - 실패 (모임에 남은 일정이 없는 경우)")
    void assign_schedule_to_guest_fail() {

        // given
        when(scheduleRepository.findNotAssignedScheduleByMeetingUuid(MEETING_UUID))
                .thenReturn(Optional.empty());

        ScheduleAssignRequestDto requestDto = ScheduleDummy.assignRequestDto();

        // when & then
        assertThrows(ScheduleNotFoundException.class,
                () -> scheduleService.assignScheduleToGuest(MEETING_UUID, requestDto));

        verify(scheduleRepository, times(1)).findNotAssignedScheduleByMeetingUuid(MEETING_UUID);
    }

    @Test
    @DisplayName("비회원 일정 할당 - 성공")
    void assign_schedule_to_guest_success() {

        // given
        when(scheduleRepository.findNotAssignedScheduleByMeetingUuid(MEETING_UUID))
                .thenReturn(Optional.of(ScheduleDummy.defaultSchedule()));

        // when
        scheduleService.assignScheduleToGuest(MEETING_UUID, ScheduleDummy.assignRequestDto());

        // then
        verify(scheduleRepository, times(1)).findNotAssignedScheduleByMeetingUuid(MEETING_UUID);
        verify(meetingRepository, times(1)).checkMeetingFull(any());
        verify(meetingRepository, times(1)).isAnonymous(any());
        verify(dateOfScheduleService, times(2)).createDateOfSchedule(anyLong(), any());
    }

    @Test
    @DisplayName("회원 일정 할당 - 존재하지 않는 회원의 요청")
    void assign_schedule_to_member_fail_not_exists_member() {

        // given
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ScheduleAssignRequestDto requestDto = ScheduleDummy.assignRequestDto();

        // when & then
        assertThrows(MemberNotFoundException.class,
                () -> scheduleService.assignScheduleToMember(1L, MEETING_UUID, requestDto));

        verify(memberRepository, times(1)).findById(anyLong());
        verify(meetingRepository, times(0)).existsByMemberIdAndMeetingUuid(anyLong(), any());
        verify(scheduleRepository, times(0)).findNotAssignedScheduleByMeetingUuid(MEETING_UUID);
    }

    @Test
    @DisplayName("회원 일정 할당 - 실패 (모임에 남은 일정이 없는 경우)")
    void assign_schedule_to_member_fail_meeting_full() {

        // given
        when(scheduleRepository.findNotAssignedScheduleByMeetingUuid(MEETING_UUID))
                .thenReturn(Optional.empty());

        Member member = Member.builder()
                .memberNickname("정승조")
                .kakaoId(123L)
                .build();

        ReflectionTestUtils.setField(member, "memberId", 1L);

        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.of(member));

        ScheduleAssignRequestDto requestDto = ScheduleDummy.assignRequestDto();

        // when & then
        assertThrows(ScheduleNotFoundException.class,
                () -> scheduleService.assignScheduleToMember(1L, MEETING_UUID, requestDto));

        verify(scheduleRepository, times(1)).findNotAssignedScheduleByMeetingUuid(MEETING_UUID);
    }

    @Test
    @DisplayName("회원 일정 할당 - 실패 (이미 일정을 할당한 경우)")
    void assign_schedule_to_member_fail_already_assigned() {

        // given
        Member member = Member.builder()
                .memberNickname("정승조")
                .kakaoId(123L)
                .build();

        ReflectionTestUtils.setField(member, "memberId", 1L);

        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.of(member));

        when(meetingRepository.existsByMemberIdAndMeetingUuid(anyLong(), any()))
                .thenReturn(true);

        ScheduleAssignRequestDto requestDto = ScheduleDummy.assignRequestDto();

        // when & then
        assertThrows(ScheduleAlreadyAssignedException.class,
                () -> scheduleService.assignScheduleToMember(1L, MEETING_UUID, requestDto));

        verify(memberRepository, times(1)).findById(anyLong());
        verify(meetingRepository, times(1)).existsByMemberIdAndMeetingUuid(anyLong(), any());
    }

    @Test
    @DisplayName("회원 일정 할당 - 성공")
    void assign_schedule_to_member_success() {

        // given
        when(scheduleRepository.findNotAssignedScheduleByMeetingUuid(MEETING_UUID))
                .thenReturn(Optional.of(ScheduleDummy.defaultSchedule()));

        Member member = Member.builder()
                .memberNickname("정승조")
                .kakaoId(123L)
                .build();

        ReflectionTestUtils.setField(member, "memberId", 1L);

        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.of(member));

        ScheduleAssignRequestDto requestDto = ScheduleDummy.assignRequestDto();

        // when
        scheduleService.assignScheduleToMember(1L, MEETING_UUID, requestDto);

        // then
        verify(scheduleRepository, times(1)).findNotAssignedScheduleByMeetingUuid(MEETING_UUID);
        verify(meetingRepository, times(1)).checkMeetingFull(any());
        verify(meetingRepository, times(1)).isAnonymous(any());
        verify(dateOfScheduleService, times(2)).createDateOfSchedule(anyLong(), any());
    }

    @Test
    @DisplayName("일정 수정 - 성공")
    void update_schedule_success() {

        // given
        when(scheduleRepository.findByScheduleUuid(any()))
                .thenReturn(Optional.of(ScheduleDummy.defaultSchedule()));

        // when
        scheduleService.updateSchedule(MEETING_UUID, SCHEDULE_UUID, ScheduleDummy.updateRequestDto());

        // then
        verify(scheduleRepository, times(1)).findByScheduleUuid(anyString());
        verify(dateOfScheduleService, times(1)).updateDateList(anyLong(), any());
    }

    @Test
    @DisplayName("일정 수정 - 실패 (일정이 존재하지 않는 경우)")
    void update_schedule_fail() {

        // given
        when(scheduleRepository.findByScheduleUuid(any()))
                .thenReturn(Optional.empty());

        ScheduleUpdateRequestDto requestDto = ScheduleDummy.updateRequestDto();
        // when & then
        assertThrows(ScheduleNotFoundException.class,
                () -> scheduleService.updateSchedule(MEETING_UUID, SCHEDULE_UUID, requestDto));

        verify(scheduleRepository, times(1)).findByScheduleUuid(anyString());
        verify(dateOfScheduleService, times(0)).updateDateList(anyLong(), any());

    }

    @Test
    @DisplayName("일정 수정 - 실패 (모임과 일정의 모임이 다른 경우)")
    void update_schedule_fail_different_meeting() {

        // given
        when(scheduleRepository.findByScheduleUuid(any()))
                .thenReturn(Optional.of(ScheduleDummy.defaultSchedule()));

        ScheduleUpdateRequestDto requestDto = ScheduleDummy.updateRequestDto();

        // when & then
        assertThrows(MeetingNotFoundException.class,
                () -> scheduleService.updateSchedule("different", SCHEDULE_UUID, requestDto));

        verify(scheduleRepository, times(1)).findByScheduleUuid(anyString());
        verify(dateOfScheduleService, times(0)).updateDateList(anyLong(), any());
    }

    @Test
    @DisplayName("비회원 일정 조회 - 성공")
    void find_schedule_by_guest_success() {

        // given
        Schedule schedule = ScheduleDummy.defaultSchedule();
        when(scheduleRepository.findByScheduleUuid(anyString()))
                .thenReturn(Optional.of(schedule));

        // when
        ScheduleResponseDto responseDto = scheduleService.getGuestSchedule(MEETING_UUID, SCHEDULE_UUID);

        // then
        assertAll(
                () -> assertEquals(schedule.getScheduleId(), responseDto.getScheduleId()),
                () -> assertEquals(schedule.getScheduleUuid(), responseDto.getScheduleUuid()),
                () -> assertEquals(schedule.getScheduleNickname(), responseDto.getScheduleNickname()),
                () -> assertEquals(schedule.getIsAssigned(), responseDto.getIsAssigned())
        );

        verify(scheduleRepository, times(1)).findByScheduleUuid(anyString());
    }

    @Test
    @DisplayName("비회원 일정 조회 - 실패 (일정이 존재하지 않는 경우)")
    void find_schedule_by_guest_fail() {
        // given
        when(scheduleRepository.findByScheduleUuid(any()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(ScheduleNotFoundException.class,
                () -> scheduleService.getGuestSchedule(MEETING_UUID, SCHEDULE_UUID));

        verify(scheduleRepository, times(1)).findByScheduleUuid(anyString());
    }

    @Test
    @DisplayName("비회원 일정 조회 - 실패 (모임과 일정의 모임이 다른 경우)")
    void find_schedule_by_guest_fail_different_meeting() {

        // given
        Schedule schedule = ScheduleDummy.defaultSchedule();
        when(scheduleRepository.findByScheduleUuid(anyString()))
                .thenReturn(Optional.of(schedule));

        // when & then
        assertThrows(MeetingNotFoundException.class,
                () -> scheduleService.getGuestSchedule("different", SCHEDULE_UUID));

        verify(scheduleRepository, times(1)).findByScheduleUuid(anyString());
    }

    @Test
    @DisplayName("회원 일정 조회 - 성공")
    void find_schedule_by_member_success() {

        // given
        Member member = Member.builder()
                .memberNickname("정승조")
                .kakaoId(123L)
                .build();

        ReflectionTestUtils.setField(member, "memberId", 1L);

        when(memberRepository.existsById(anyLong()))
                .thenReturn(true);

        Schedule schedule = ScheduleDummy.defaultSchedule();
        when(scheduleRepository.findByMemberIdAndMeetingUuid(anyLong(), anyString()))
                .thenReturn(Optional.of(schedule));

        // when
        ScheduleResponseDto responseDto = scheduleService.getMemberSchedule(MEETING_UUID, 1L);

        // then
        assertAll(
                () -> assertEquals(schedule.getScheduleId(), responseDto.getScheduleId()),
                () -> assertEquals(schedule.getScheduleUuid(), responseDto.getScheduleUuid()),
                () -> assertEquals(schedule.getScheduleNickname(), responseDto.getScheduleNickname()),
                () -> assertEquals(schedule.getIsAssigned(), responseDto.getIsAssigned())
        );

        verify(scheduleRepository, times(1)).findByMemberIdAndMeetingUuid(anyLong(), anyString());
    }

    @Test
    @DisplayName("회원 일정 조회 - 실패 (존재하지 않는 회원의 요청)")
    void find_schedule_by_member_fail_not_exists_member() {

        // given
        when(memberRepository.existsById(anyLong()))
                .thenReturn(false);

        // when & then
        assertThrows(MemberNotFoundException.class,
                () -> scheduleService.getMemberSchedule(MEETING_UUID, 1L));

        verify(memberRepository, times(1)).existsById(anyLong());
        verify(scheduleRepository, times(0)).findByMemberIdAndMeetingUuid(anyLong(), anyString());
    }

    @Test
    @DisplayName("회원 일정 조회 - 실패 (일정이 존재하지 않는 경우)")
    void find_schedule_by_member_fail() {
        // given
        when(memberRepository.existsById(anyLong()))
                .thenReturn(true);

        when(scheduleRepository.findByMemberIdAndMeetingUuid(anyLong(), anyString()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(ScheduleNotFoundException.class,
                () -> scheduleService.getMemberSchedule(MEETING_UUID, 1L));

        verify(memberRepository, times(1)).existsById(anyLong());
        verify(scheduleRepository, times(1)).findByMemberIdAndMeetingUuid(anyLong(), anyString());
    }

    @Test
    @DisplayName("회원 일정 조회 - 실패 (모임과 일정의 모임이 다른 경우)")
    void find_schedule_by_member_fail_different_meeting() {

        // given
        when(memberRepository.existsById(anyLong()))
                .thenReturn(true);

        when(scheduleRepository.findByMemberIdAndMeetingUuid(anyLong(), anyString()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(ScheduleNotFoundException.class,
                () -> scheduleService.getMemberSchedule("different", 1L));

        verify(memberRepository, times(1)).existsById(anyLong());
        verify(scheduleRepository, times(1)).findByMemberIdAndMeetingUuid(anyLong(), anyString());
    }


}