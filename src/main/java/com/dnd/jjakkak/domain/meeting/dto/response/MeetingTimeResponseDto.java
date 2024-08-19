package com.dnd.jjakkak.domain.meeting.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 모임 시간 응답 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 19.
 */
@Getter
public class MeetingTimeResponseDto {

    private final List<String> memberNames;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Double rank;


    public MeetingTimeResponseDto(LocalDateTime startTime, LocalDateTime endTime, Double rank) {
        this.memberNames = new ArrayList<>();
        this.startTime = startTime;
        this.endTime = endTime;
        this.rank = rank;
    }

    public void addMemberNames(List<String> memberNames) {
        this.memberNames.addAll(memberNames);
    }
}
