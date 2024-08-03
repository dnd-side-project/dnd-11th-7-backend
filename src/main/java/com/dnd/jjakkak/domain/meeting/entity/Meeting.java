package com.dnd.jjakkak.domain.meeting.entity;

import com.dnd.jjakkak.domain.meeting.dto.request.MeetingConfirmRequestDto;
import com.dnd.jjakkak.domain.meeting.exception.InvalidMeetingDateException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 모임 엔티티 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 23.
 */
@Entity
@Table(indexes = {
        @Index(name = "idx_meeting_uuid", columnList = "meeting_uuid", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
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

    @Column(nullable = false, name = "meeting_leader_id")
    private Long meetingLeaderId;

    @Column(nullable = false, name = "meeting_uuid", length = 8)
    private String meetingUuid;

    @Builder
    public Meeting(String meetingName, LocalDate meetingStartDate, LocalDate meetingEndDate,
                   Integer numberOfPeople, Boolean isOnline, Boolean isAnonymous,
                   LocalDateTime voteEndDate, Long meetingLeaderId, String meetingUuid) {
        this.meetingName = meetingName;
        this.meetingStartDate = meetingStartDate;
        this.meetingEndDate = meetingEndDate;
        this.numberOfPeople = numberOfPeople;
        this.isOnline = isOnline;
        this.isAnonymous = isAnonymous;
        this.voteEndDate = voteEndDate;
        this.meetingLeaderId = meetingLeaderId;
        this.meetingUuid = meetingUuid;
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
}
