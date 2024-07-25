package com.dnd.jjakkak.group.entity;

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
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(nullable = false, name = "group_name", length = 30)
    private String groupName;

    @Column(nullable = false, name = "group_start_date")
    private LocalDate groupStartDate;

    @Column(nullable = false, name = "group_end_date")
    private LocalDate groupEndDate;

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
    public Group(String groupName, LocalDate groupStartDate, LocalDate groupEndDate, Integer numberOfPeople, Boolean isOnline, Boolean isAnonymous, LocalDateTime voteEndDate, LocalDateTime confirmedSchedule) {
        this.groupName = groupName;
        this.groupStartDate = groupStartDate;
        this.groupEndDate = groupEndDate;
        this.numberOfPeople = numberOfPeople;
        this.isOnline = isOnline;
        this.isAnonymous = isAnonymous != null ? isAnonymous : false;
        this.voteEndDate = voteEndDate;
        this.confirmedSchedule = confirmedSchedule;
    }
}
