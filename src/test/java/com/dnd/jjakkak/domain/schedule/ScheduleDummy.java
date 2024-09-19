package com.dnd.jjakkak.domain.schedule;

import com.dnd.jjakkak.domain.dateofschedule.dto.request.DateOfScheduleCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleAssignRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleUpdateRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.response.ScheduleResponseDto;
import com.dnd.jjakkak.domain.schedule.entity.Schedule;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 일정 관련 Dummy 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 09. 06.
 */
public class ScheduleDummy {

    private ScheduleDummy() {
    }


    public static ScheduleAssignRequestDto invalidAssignRequestDto() {
        return new ScheduleAssignRequestDto();
    }

    public static ScheduleAssignRequestDto assignRequestDto() {
        ScheduleAssignRequestDto requestDto = new ScheduleAssignRequestDto();

        DateOfScheduleCreateRequestDto firstDto = new DateOfScheduleCreateRequestDto();
        ReflectionTestUtils.setField(firstDto, "startTime", LocalDateTime.of(2024, 9, 6, 9, 0, 0));
        ReflectionTestUtils.setField(firstDto, "endTime", LocalDateTime.of(2024, 9, 6, 12, 0, 0));
        ReflectionTestUtils.setField(firstDto, "rank", 1);

        DateOfScheduleCreateRequestDto secondDto = new DateOfScheduleCreateRequestDto();
        ReflectionTestUtils.setField(secondDto, "startTime", LocalDateTime.of(2024, 9, 6, 18, 0, 0));
        ReflectionTestUtils.setField(secondDto, "endTime", LocalDateTime.of(2024, 9, 6, 21, 0, 0));
        ReflectionTestUtils.setField(secondDto, "rank", 2);

        List<DateOfScheduleCreateRequestDto> dateOfScheduleList = List.of(firstDto, secondDto);

        ReflectionTestUtils.setField(requestDto, "dateOfScheduleList", dateOfScheduleList);
        ReflectionTestUtils.setField(requestDto, "nickname", "정승조");

        return requestDto;
    }


    public static ScheduleResponseDto memberScheduleResponseDto() {

        Member member = Member.builder()
                .memberNickname("정승조")
                .kakaoId(123L)
                .build();

        ReflectionTestUtils.setField(member, "memberId", 1L);

        Meeting meeting = Meeting.builder()
                .meetingName("세븐일레븐")
                .meetingStartDate(LocalDate.of(2024, 9, 6))
                .meetingEndDate(LocalDate.of(2024, 9, 7))
                .numberOfPeople(6)
                .isAnonymous(false)
                .dueDateTime(LocalDateTime.of(2024, 9, 5, 12, 0, 0))
                .meetingLeaderId(member.getMemberId())
                .meetingUuid("met123")
                .build();

        ReflectionTestUtils.setField(meeting, "meetingId", 1L);

        Schedule schedule = Schedule.builder()
                .meeting(meeting)
                .member(member)
                .scheduleUuid("sch123")
                .scheduleNickname("정승조")
                .build();

        ReflectionTestUtils.setField(schedule, "scheduleId", 1L);

        return new ScheduleResponseDto(schedule);
    }

    public static ScheduleResponseDto guestScheduleResponseDto() {

        Meeting meeting = Meeting.builder()
                .meetingStartDate(LocalDate.of(2024, 9, 6))
                .meetingEndDate(LocalDate.of(2024, 9, 7))
                .numberOfPeople(4)
                .isAnonymous(false)
                .dueDateTime(LocalDateTime.of(2024, 9, 5, 12, 0, 0))
                .meetingLeaderId(1L)
                .meetingUuid("met123")
                .build();

        ReflectionTestUtils.setField(meeting, "meetingId", 1L);

        Schedule schedule = Schedule.builder()
                .meeting(meeting)
                .scheduleUuid("sch123")
                .scheduleNickname("비회원")
                .build();

        ReflectionTestUtils.setField(schedule, "scheduleId", 1L);

        return new ScheduleResponseDto(schedule);
    }

    public static ScheduleUpdateRequestDto updateRequestDto() {

        ScheduleUpdateRequestDto requestDto = new ScheduleUpdateRequestDto();

        DateOfScheduleCreateRequestDto firstDto = new DateOfScheduleCreateRequestDto();
        ReflectionTestUtils.setField(firstDto, "startTime", LocalDateTime.of(2024, 9, 6, 9, 0, 0));
        ReflectionTestUtils.setField(firstDto, "endTime", LocalDateTime.of(2024, 9, 6, 12, 0, 0));
        ReflectionTestUtils.setField(firstDto, "rank", 1);

        DateOfScheduleCreateRequestDto secondDto = new DateOfScheduleCreateRequestDto();
        ReflectionTestUtils.setField(secondDto, "startTime", LocalDateTime.of(2024, 9, 6, 18, 0, 0));
        ReflectionTestUtils.setField(secondDto, "endTime", LocalDateTime.of(2024, 9, 6, 21, 0, 0));
        ReflectionTestUtils.setField(secondDto, "rank", 2);

        List<DateOfScheduleCreateRequestDto> dateOfScheduleList = List.of(firstDto, secondDto);

        ReflectionTestUtils.setField(requestDto, "dateOfScheduleList", dateOfScheduleList);

        return requestDto;
    }

    public static ScheduleUpdateRequestDto invalidUpdateRequestDto() {
        return new ScheduleUpdateRequestDto();
    }

    public static Schedule defaultSchedule() {

        Meeting meeting = Meeting.builder()
                .meetingName("세븐일레븐")
                .meetingStartDate(LocalDate.of(2024, 9, 6))
                .meetingEndDate(LocalDate.of(2024, 9, 7))
                .numberOfPeople(6)
                .isAnonymous(false)
                .dueDateTime(LocalDateTime.of(2024, 9, 5, 12, 0, 0))
                .meetingLeaderId(1L)
                .meetingUuid("met123")
                .build();

        ReflectionTestUtils.setField(meeting, "meetingId", 1L);

        Schedule schedule = Schedule.builder()
                .meeting(meeting)
                .scheduleUuid("sch123")
                .scheduleNickname("멤버")
                .build();

        ReflectionTestUtils.setField(schedule, "scheduleId", 1L);

        return schedule;
    }
}
