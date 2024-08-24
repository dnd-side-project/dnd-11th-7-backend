package com.dnd.jjakkak.domain.meeting.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 모임의 정보 응답 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 19.
 */
@ToString
@Getter
public class MeetingInfoResponseDto {

    private final Long meetingId;
    private final List<String> categoryNames;
    private final String meetingName;
    private final LocalDate meetingStartDate;
    private final LocalDate meetingEndDate;
    private final LocalDateTime scheduleInputEndDateTime;

    @Builder
    public MeetingInfoResponseDto(Long meetingId, String meetingName,
                                  LocalDate meetingStartDate, LocalDate meetingEndDate,
                                  LocalDateTime scheduleInputEndDateTime) {

        this.categoryNames = new ArrayList<>();
        this.meetingId = meetingId;
        this.meetingName = meetingName;
        this.meetingStartDate = meetingStartDate;
        this.meetingEndDate = meetingEndDate;
        this.scheduleInputEndDateTime = scheduleInputEndDateTime;
    }

    public void addCategoryNames(List<String> categoryNames) {
        this.categoryNames.addAll(categoryNames);
    }
}
