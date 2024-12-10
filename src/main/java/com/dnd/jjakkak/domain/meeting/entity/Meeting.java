package com.dnd.jjakkak.domain.meeting.entity;

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

    @Column(name = "is_anonymous")
    private Boolean isAnonymous;

    @Column(nullable = false, name = "due_date_time")
    private LocalDateTime dueDateTime;

    @Column(nullable = false, name = "meeting_leader_id")
    private Long meetingLeaderId;

    @Column(nullable = false, name = "meeting_uuid", length = 8)
    private String meetingUuid;

    @Builder
    public Meeting(String meetingName, LocalDate meetingStartDate, LocalDate meetingEndDate,
                   Integer numberOfPeople, Boolean isAnonymous,
                   LocalDateTime dueDateTime, Long meetingLeaderId, String meetingUuid) {
        this.meetingName = meetingName;
        this.meetingStartDate = meetingStartDate;
        this.meetingEndDate = meetingEndDate;
        this.numberOfPeople = numberOfPeople;
        this.isAnonymous = isAnonymous;
        this.dueDateTime = dueDateTime;
        this.meetingLeaderId = meetingLeaderId;
        this.meetingUuid = meetingUuid;
    }
}
