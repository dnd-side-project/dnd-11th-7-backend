package com.dnd.jjakkak.domain.meeting;

import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.response.*;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 모임 객체 Dummy 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 25.
 */
public class MeetingDummy {


    /**
     * MeetingCreateRequestDto 객체를 생성하여 반환합니다.
     *
     * <li>카테고리 아이디는 1과 2를 가지고 있습니다.</li>
     *
     * @return MeetingCreateRequestDto 객체
     */
    public static MeetingCreateRequestDto createRequestDto(List<Long> categoryIds) {

        MeetingCreateRequestDto requestDto = new MeetingCreateRequestDto();
        ReflectionTestUtils.setField(requestDto, "meetingName", "세븐일레븐");
        ReflectionTestUtils.setField(requestDto, "meetingStartDate", LocalDate.of(2024, 7, 27));
        ReflectionTestUtils.setField(requestDto, "meetingEndDate", LocalDate.of(2024, 7, 29));
        ReflectionTestUtils.setField(requestDto, "numberOfPeople", 6);
        ReflectionTestUtils.setField(requestDto, "isAnonymous", false);
        ReflectionTestUtils.setField(requestDto, "dueDateTime", LocalDateTime.of(2024, 7, 26, 23, 59, 59));
        ReflectionTestUtils.setField(requestDto, "categoryIds", categoryIds);

        return requestDto;
    }

    /**
     * 유효하지 않은 MeetingCreateRequestDto 객체를 생성하여 반환합니다.
     *
     * @return MeetingCreateRequestDto 객체
     */
    public static MeetingCreateRequestDto createInvalidRequestDto() {
        return new MeetingCreateRequestDto();
    }

    public static MeetingInfoResponseDto createInfoResponse() {

        MeetingInfoResponseDto responseDto = MeetingInfoResponseDto.builder()
                .meetingId(1L)
                .meetingName("세븐일레븐")
                .meetingStartDate(LocalDate.of(2024, 8, 27))
                .meetingEndDate(LocalDate.of(2024, 8, 29))
                .dueDateTime(LocalDateTime.of(2024, 8, 26, 23, 59, 59))
                .build();

        List<String> categories = List.of("팀플", "회의");
        responseDto.addCategoryNames(categories);

        return responseDto;
    }

    public static MeetingTimeResponseDto createMeetingTimeResponseDto() {
        MeetingTime response = MeetingTime.builder()
                .startTime(LocalDateTime.of(2024, 8, 27, 10, 0))
                .endTime(LocalDateTime.of(2024, 8, 27, 12, 0))
                .rank(1.0)
                .build();

        response.addMemberNames(List.of("고래", "상어"));

        MeetingTimeResponseDto responseDto = new MeetingTimeResponseDto(2, false);
        responseDto.addMeetingTimeList(List.of(response));

        return responseDto;
    }


    public static MeetingParticipantResponseDto createParticipants() {

        MeetingParticipantResponseDto response = MeetingParticipantResponseDto.builder()
                .numberOfPeople(2)
                .anonymousStatus(false)
                .build();

        MeetingParticipantResponseDto.ParticipantInfo whale
                = new MeetingParticipantResponseDto.ParticipantInfo("고래", true, true);

        MeetingParticipantResponseDto.ParticipantInfo shark
                = new MeetingParticipantResponseDto.ParticipantInfo("상어", true, false);

        response.addParticipantInfoList(List.of(whale, shark));

        return response;
    }

    public static List<MeetingMyPageResponseDto> createMeetingMyPageResponseDto() {

        MeetingMyPageResponseDto responseDto = MeetingMyPageResponseDto.builder()
                .meetingId(1L)
                .meetingName("세븐일레븐")
                .meetingUuid("123ABC")
                .meetingStartDate(LocalDate.of(2024, 8, 27))
                .meetingEndDate(LocalDate.of(2024, 8, 29))
                .dueDateTime(LocalDateTime.of(2024, 8, 26, 23, 59, 59))
                .numberOfPeople(6)
                .isAnonymous(false)
                .leaderName("승조")
                .build();

        responseDto.addCategoryNames(List.of("팀플", "스터디", "회의"));

        return List.of(responseDto);
    }

    public static Meeting createMeeting() {

        Meeting meeting = Meeting.builder()
                .meetingName("세븐일레븐")
                .meetingStartDate(LocalDate.of(2024, 8, 27))
                .meetingEndDate(LocalDate.of(2024, 8, 29))
                .numberOfPeople(6)
                .isAnonymous(false)
                .dueDateTime(LocalDateTime.of(2024, 8, 26, 23, 59, 59))
                .meetingUuid("met123")
                .meetingLeaderId(1L)
                .build();

        ReflectionTestUtils.setField(meeting, "meetingId", 1L);

        return meeting;
    }
}
