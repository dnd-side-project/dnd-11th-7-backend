package com.dnd.jjakkak.domain.dateofschedule.entity;

import com.dnd.jjakkak.domain.schedule.entity.Schedule;
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(name = "date_of_schedule_start", nullable = false)
    private LocalDateTime dateOfScheduleStart;

    @Column(name = "date_of_schedule_end", nullable = false)
    private LocalDateTime dateOfScheduleEnd;

    @Column(name = "date_of_schedule_rank", nullable = false)
    private Integer dateOfScheduleRank;

    @Builder
    public DateOfSchedule(Schedule schedule, LocalDateTime dateOfScheduleStart, LocalDateTime dateOfScheduleEnd, Integer dateOfScheduleRank) {
        this.schedule = schedule;
        this.dateOfScheduleStart = dateOfScheduleStart;
        this.dateOfScheduleEnd = dateOfScheduleEnd;
        this.dateOfScheduleRank = dateOfScheduleRank;
    }
}
