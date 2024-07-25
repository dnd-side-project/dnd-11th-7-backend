package com.dnd.jjakkak.domain.meeting.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
    @ColumnDefault("false")
    private Boolean isAnonymous;

    @Column(nullable = false, name = "vote_end_date", columnDefinition = "DATETIME DEFAULT schedule_start_date - 1")
    private LocalDateTime voteEndDate;

    @Column(name = "confirmed_schedule")
    private LocalDateTime confirmedSchedule;

    @Builder
    public Meeting(String meetingName, LocalDate meetingStartDate, LocalDate meetingEndDate, Integer numberOfPeople, Boolean isOnline, Boolean isAnonymous, LocalDateTime voteEndDate) {
        this.meetingName = meetingName;
        this.meetingStartDate = meetingStartDate;
        this.meetingEndDate = meetingEndDate;
        this.numberOfPeople = numberOfPeople;
        this.isOnline = isOnline;
        this.isAnonymous = isAnonymous != null ? isAnonymous : false;
        this.voteEndDate = voteEndDate;
    }

    /**
     * 모임 일정이 확정되었을 경우 확정된 일자의 값을 넣어줍니다.
     *
     * @param confirmedSchedule 확정된 일자
     */
    public void updateConfirmedSchedule(LocalDateTime confirmedSchedule) {
        this.confirmedSchedule = confirmedSchedule;
    }
}
