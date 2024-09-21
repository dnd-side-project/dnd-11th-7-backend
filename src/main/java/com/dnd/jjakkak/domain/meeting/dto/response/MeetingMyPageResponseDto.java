package com.dnd.jjakkak.domain.meeting.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 마이페이지에서 모임 정보를 조회하는 응답 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 20.
 */
@Getter
public class MeetingMyPageResponseDto {

    private final List<String> categoryNames;
    private final Long meetingId;
    private final String meetingUuid;
    private final String meetingName;
    private final LocalDate meetingStartDate;
    private final LocalDate meetingEndDate;
    private final LocalDateTime dueDateTime;
    private final Integer numberOfPeople;
    private final Boolean isAnonymous;
    private final String leaderName;

    @Builder
    public MeetingMyPageResponseDto(Long meetingId, String meetingName, String meetingUuid,
                                    LocalDate meetingStartDate, LocalDate meetingEndDate,
                                    LocalDateTime dueDateTime, Integer numberOfPeople,
                                    Boolean isAnonymous, String leaderName) {
        this.categoryNames = new ArrayList<>();
        this.meetingId = meetingId;
        this.meetingName = meetingName;
        this.meetingUuid = meetingUuid;
        this.meetingStartDate = meetingStartDate;
        this.meetingEndDate = meetingEndDate;
        this.dueDateTime = dueDateTime;
        this.numberOfPeople = numberOfPeople;
        this.isAnonymous = isAnonymous;
        this.leaderName = leaderName;
    }

    public void addCategoryNames(List<String> categoryNames) {
        this.categoryNames.addAll(categoryNames);
    }
}
