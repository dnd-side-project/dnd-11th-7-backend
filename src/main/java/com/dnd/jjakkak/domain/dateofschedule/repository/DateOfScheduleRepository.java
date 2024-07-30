 package com.dnd.jjakkak.domain.dateofschedule.repository;

import com.dnd.jjakkak.domain.dateofschedule.entity.DateOfSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 일정 날짜 레포지토리 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 30.
 */
public interface DateOfScheduleRepository extends JpaRepository<DateOfSchedule, Long> {
}
