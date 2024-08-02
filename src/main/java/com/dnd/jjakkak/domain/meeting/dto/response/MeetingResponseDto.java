package com.dnd.jjakkak.domain.meeting.dto.response;

import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 모임 응답 DTO 클래스입니다.
 *
 * @author 정승조, 류태웅
 * @version 2024. 07. 30.
 */
@Getter
public class MeetingResponseDto {

    private final Long meetingId;
    private final String meetingName;
    private final LocalDate meetingStartDate;
    private final LocalDate meetingEndDate;
    private final Integer numberOfPeople;
    private final Boolean isOnline;
    private final Boolean isAnonymous;
    private final LocalDateTime voteEndDate;
    private final LocalDateTime confirmedSchedule;
    private final Long meetingLeaderId;
    private final String meetingUuid;

    @Builder
    public MeetingResponseDto(Meeting meeting) {
        this.meetingId = meeting.getMeetingId();
        this.meetingName = meeting.getMeetingName();
        this.meetingStartDate = meeting.getMeetingStartDate();
        this.meetingEndDate = meeting.getMeetingEndDate();
        this.numberOfPeople = meeting.getNumberOfPeople();
        this.isOnline = meeting.getIsOnline();
        this.isAnonymous = meeting.getIsAnonymous();
        this.voteEndDate = meeting.getVoteEndDate();
        this.confirmedSchedule = meeting.getConfirmedSchedule();
        this.meetingLeaderId = meeting.getMeetingLeaderId();
        this.meetingUuid = meeting.getMeetingUuid();
    }
}
