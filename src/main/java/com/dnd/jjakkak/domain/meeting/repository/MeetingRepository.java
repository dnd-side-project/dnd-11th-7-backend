package com.dnd.jjakkak.domain.meeting.repository;

import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 모임 JPA 레포지토리입니다.
 *
 * @author 정승조
 * @version 2024. 07. 25.
 */
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}
