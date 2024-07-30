package com.dnd.jjakkak.domain.dateofschedule.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 일정 날짜 엔티티 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 30.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DateOfSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "date_of_schedule_id")
    private Long dateOfScheduleId;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "date_of_schedule_start", nullable = false)
    private LocalDateTime dateOfScheduleStart;

    @Column(name = "date_of_schedule_end", nullable = false)
    private LocalDateTime dateOfScheduleEnd;

    @Column(name = "date_of_schedule_rank", nullable = false)
    private Integer dateOfScheduleRank;

    @Builder
    public DateOfSchedule(Long scheduleId, LocalDateTime dateOfScheduleStart, LocalDateTime dateOfScheduleEnd, Integer dateOfScheduleRank) {
        this.scheduleId = scheduleId;
        this.dateOfScheduleStart = dateOfScheduleStart;
        this.dateOfScheduleEnd = dateOfScheduleEnd;
        this.dateOfScheduleRank = dateOfScheduleRank;
    }
}
