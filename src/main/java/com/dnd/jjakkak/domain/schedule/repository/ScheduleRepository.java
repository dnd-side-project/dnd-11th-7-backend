package com.dnd.jjakkak.domain.schedule.repository;

import com.dnd.jjakkak.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 일정 레포지토리 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 29.
 */
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    boolean existsByScheduleUuid(String uuid);

    Optional<Schedule> findByScheduleUuid(String scheduleUuid);
}
