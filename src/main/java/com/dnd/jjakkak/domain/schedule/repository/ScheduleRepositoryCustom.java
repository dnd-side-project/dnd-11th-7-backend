package com.dnd.jjakkak.domain.schedule.repository;

import com.dnd.jjakkak.domain.schedule.dto.response.ScheduleResponseDto;
import com.dnd.jjakkak.domain.schedule.entity.Schedule;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/**
 * 일정 Querydsl 레포지토리 인터페이스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 31.
 */
@NoRepositoryBean
public interface ScheduleRepositoryCustom {

    /**
     * 회원 ID와 모임 UUID로 일정을 조회합니다.
     *
     * @param memberId    회원 ID
     * @param meetingUuid 모임 UUID
     * @return 일정
     */
    Optional<Schedule> findByMemberIdAndMeetingUuid(Long memberId, String meetingUuid);

    /**
     * 모임의 UUID로 할당되지 않은 일정을 조회합니다.
     *
     * @param meetingUuid 모임 UUID
     * @return 일정
     */
    Optional<Schedule> findNotAssignedScheduleByMeetingUuid(String meetingUuid);


    /**
     * 일정 ID로 일정과 일정의 날짜를 조회합니다.
     *
     * @param scheduleId 일정 ID
     * @return 일정 응답 DTO (일정 정보, 일정 날짜)
     */
    ScheduleResponseDto findScheduleWithDateOfSchedule(Long scheduleId);
}
