package com.dnd.jjakkak.domain.schedule.repository;

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
     * 회원 ID와 모임 ID로 일정을 조회합니다.
     *
     * @param memberId  회원 ID
     * @param meetingId 모임 ID
     * @return 일정
     */
    Optional<Schedule> findByMemberIdAndMeetingId(Long memberId, Long meetingId);
}
