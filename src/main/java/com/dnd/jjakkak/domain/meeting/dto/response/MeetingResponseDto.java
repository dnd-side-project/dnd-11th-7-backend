package com.dnd.jjakkak.domain.meeting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 모임 응답 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 25.
 */
@Getter
@AllArgsConstructor
@Builder
public class MeetingResponseDto {

    private final Long meetingId;
    private final String meetingName;
    private final LocalDate meetingStartDate;
    private final LocalDate meetingEndDate;
    private final Integer numberOfPeople;
    private final Boolean isOnline;
    private final Boolean isAnonymous;
    private final LocalDateTime voteEndDate;
}
