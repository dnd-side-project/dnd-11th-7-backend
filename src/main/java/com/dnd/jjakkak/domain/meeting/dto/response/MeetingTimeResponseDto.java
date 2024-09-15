package com.dnd.jjakkak.domain.meeting.dto.response;

import lombok.Getter;

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

    private final Integer numberOfPeople;
    private final Boolean isAnonymous;
    private final List<MeetingTime> meetingTimeList;


    public MeetingTimeResponseDto(Integer numberOfPeople, Boolean isAnonymous) {
        this.numberOfPeople = numberOfPeople;
        this.isAnonymous = isAnonymous;
        this.meetingTimeList = new ArrayList<>();
    }

    public void addMeetingTimeList(List<MeetingTime> meetingTime) {
        this.meetingTimeList.addAll(meetingTime);
    }
}
