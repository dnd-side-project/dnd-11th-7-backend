package com.dnd.jjakkak.domain.meeting.entity;

import com.dnd.jjakkak.domain.meeting.dto.request.MeetingConfirmRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingUpdateRequestDto;
import com.dnd.jjakkak.domain.meeting.exception.InvalidMeetingDateException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 모임 엔티티 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 23.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long meetingId;

    @Column(nullable = false, name = "meeting_name", length = 30)
    private String meetingName;

    @Column(nullable = false, name = "meeting_start_date")
    private LocalDate meetingStartDate;

    @Column(nullable = false, name = "meeting_end_date")
    private LocalDate meetingEndDate;

    @Column(nullable = false, name = "number_of_people")
    private Integer numberOfPeople;

    @Column(name = "is_online")
    private Boolean isOnline;

    @Column(name = "is_anonymous")
    private Boolean isAnonymous;

    @Column(nullable = false, name = "vote_end_date")
    private LocalDateTime voteEndDate;

    @Column(name = "confirmed_schedule")
    private LocalDateTime confirmedSchedule;

    @Builder
    public Meeting(String meetingName, LocalDate meetingStartDate, LocalDate meetingEndDate,
                   Integer numberOfPeople, Boolean isOnline, Boolean isAnonymous, LocalDateTime voteEndDate) {
        this.meetingName = meetingName;
        this.meetingStartDate = meetingStartDate;
        this.meetingEndDate = meetingEndDate;
        this.numberOfPeople = numberOfPeople;
        this.isOnline = isOnline;
        this.isAnonymous = isAnonymous;
        this.voteEndDate = voteEndDate;
    }


    /**
     * 확정된 모임 일자를 설정합니다.
     *
     * @param requestDto 확정된 모임 일자 수정 요청 DTO
     */
    public void updateConfirmedSchedule(MeetingConfirmRequestDto requestDto) {

        // 확정된 일자가 유효한지 확인합니다.
        if (requestDto.getConfirmedSchedule().isBefore(this.meetingStartDate.atStartOfDay()) ||
                requestDto.getConfirmedSchedule().isAfter(this.meetingEndDate.atStartOfDay())) {
            throw new InvalidMeetingDateException();
        }

        this.confirmedSchedule = requestDto.getConfirmedSchedule();
    }

    /**
     * 모임 정보를 수정합니다.
     *
     * @param requestDto 수정 요청 DTO
     */
    public void updateMeeting(MeetingUpdateRequestDto requestDto) {
        this.meetingName = requestDto.getMeetingName();
        this.meetingStartDate = requestDto.getMeetingStartDate();
        this.meetingEndDate = requestDto.getMeetingEndDate();
        this.numberOfPeople = requestDto.getNumberOfPeople();
        this.isOnline = requestDto.getIsOnline();
        this.isAnonymous = requestDto.getIsAnonymous();
        this.voteEndDate = requestDto.getVoteEndDate();
    }
}