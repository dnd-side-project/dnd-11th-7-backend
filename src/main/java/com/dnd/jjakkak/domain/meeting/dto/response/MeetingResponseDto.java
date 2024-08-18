package com.dnd.jjakkak.domain.meeting.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 모임 응답 DTO 클래스입니다.
 *
 * @author 정승조, 류태웅
 * @version 2024. 07. 30.
 */
@ToString
@Getter
public class MeetingResponseDto {

    private final Long meetingId;
    private final String meetingName;
    private final LocalDate meetingStartDate;
    private final LocalDate meetingEndDate;
    private final Integer numberOfPeople;
    private final Boolean isAnonymous;
    private final LocalDateTime voteEndDate;
    private final Long meetingLeaderId;
    private final String meetingUuid;
    private final List<BestTime> bestTime = new ArrayList<>();

    @Builder
    public MeetingResponseDto(Long meetingId, String meetingName,
                              LocalDate meetingStartDate, LocalDate meetingEndDate,
                              Integer numberOfPeople, Boolean isAnonymous,
                              LocalDateTime voteEndDate, Long meetingLeaderId, String meetingUuid) {
        this.meetingId = meetingId;
        this.meetingName = meetingName;
        this.meetingStartDate = meetingStartDate;
        this.meetingEndDate = meetingEndDate;
        this.numberOfPeople = numberOfPeople;
        this.isAnonymous = isAnonymous;
        this.voteEndDate = voteEndDate;
        this.meetingLeaderId = meetingLeaderId;
        this.meetingUuid = meetingUuid;
    }

    public void addBestTime(BestTime bestTime) {
        this.bestTime.add(bestTime);
    }


    @ToString
    @Getter
    public static class BestTime {

        private final LocalDateTime startTime;
        private final LocalDateTime endTIme;
        private final List<String> memberNickname;

        @Builder
        public BestTime(LocalDateTime startTime, LocalDateTime endTIme) {
            this.startTime = startTime;
            this.endTIme = endTIme;
            this.memberNickname = new ArrayList<>();
        }
    }
}
