package com.dnd.jjakkak.domain.meeting.repository;

import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 모임 JPA 레포지토리입니다.
 *
 * @author 정승조
 * @version 2024. 07. 25.
 */
public interface MeetingRepository extends JpaRepository<Meeting, Long>,
        MeetingRepositoryCustom {

    /**
     * 모임 uuid로 모임이 존재하는지 확인합니다.
     *
     * @param uuid 모임 uuid
     * @return 모임이 존재하는지 여부
     */
    boolean existsByMeetingUuid(String uuid);

    /**
     * 모임 uuid로 모임을 조회합니다.
     *
     * @param uuid 모임 uuid
     * @return 모임
     */
    Optional<Meeting> findByMeetingUuid(String uuid);
}
