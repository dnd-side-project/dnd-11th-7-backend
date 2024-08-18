package com.dnd.jjakkak.domain.meeting.repository;

import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
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

    /**
     * 모임의 UUID로 모임을 조회합니다.
     *
     * @param uuid 모임 UUID
     * @return 모임 응답 DTO
     */
    MeetingResponseDto findByMeetingUuidWithBestTime(String uuid);
}
