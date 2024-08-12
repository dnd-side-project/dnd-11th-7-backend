package com.dnd.jjakkak.domain.meeting.repository;

import org.springframework.data.repository.NoRepositoryBean;

/**
 * 모임 Querydsl 메서드를 정의하는 인터페이스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 29.
 */
@NoRepositoryBean
public interface MeetingRepositoryCustom {

    /**
     * 모임의 인원이 꽉 찼는지 확인합니다.
     *
     * @param meetingId 모임 ID
     * @return 모임의 인원이 꽉 찼는지 여부
     */
    boolean checkMeetingFull(Long meetingId);

    /**
     * 모임이 익명인지 확인합니다.
     *
     * @param meetingId 모임 ID
     * @return 모임이 익명인지 여부
     */
    boolean isAnonymous(Long meetingId);
}
