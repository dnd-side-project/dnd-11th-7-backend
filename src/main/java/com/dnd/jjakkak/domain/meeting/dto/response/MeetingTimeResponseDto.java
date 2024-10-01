package com.dnd.jjakkak.domain.meeting.dto.response;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 모임 시간 응답 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 09. 15.
 */
@Getter
public class MeetingTimeResponseDto {

    private final List<MeetingTime> meetingTimeList;
    private final Integer numberOfPeople;
    private final Boolean isAnonymous;
    private final LocalDate meetingStartDate;
    private final LocalDate meetingEndDate;
    private LocalDateTime requestTime;


    public MeetingTimeResponseDto(Integer numberOfPeople, Boolean isAnonymous,
                                  LocalDate meetingStartDate, LocalDate meetingEndDate) {
        this.meetingTimeList = new ArrayList<>();
        this.numberOfPeople = numberOfPeople;
        this.isAnonymous = isAnonymous;
        this.meetingStartDate = meetingStartDate;
        this.meetingEndDate = meetingEndDate;
    }

    public void addMeetingTimeList(List<MeetingTime> meetingTime) {
        this.meetingTimeList.addAll(meetingTime);
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }
}
