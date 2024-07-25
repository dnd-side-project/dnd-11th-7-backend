package com.dnd.jjakkak.domain.meeting;

import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
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
        ReflectionTestUtils.setField(requestDto, "isOnline", true);
        ReflectionTestUtils.setField(requestDto, "isAnonymous", false);
        ReflectionTestUtils.setField(requestDto, "voteEndDate", LocalDateTime.of(2024, 7, 26, 23, 59, 59));
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
}
