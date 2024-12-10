package com.dnd.jjakkak.domain.dateofschedule.repository;

import com.dnd.jjakkak.domain.dateofschedule.entity.DateOfSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 일정 날짜 레포지토리 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 30.
 */
public interface DateOfScheduleRepository extends JpaRepository<DateOfSchedule, Long> {

    @Modifying
    @Query("DELETE FROM DateOfSchedule d WHERE d.schedule.scheduleId = :scheduleId")
    void deleteAllByScheduleId(Long scheduleId);
}
