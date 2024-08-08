package com.dnd.jjakkak.domain.meeting.repository;

import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 모임 Querydsl 인터페이스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 08.
 */
@NoRepositoryBean
public interface MeetingRepositoryCustom {
    /**
     * 모임 ID로 가장 많은 선택을 받은 날짜를 조회합니다.
     *
     * @param meetingId 모임 ID
     * @return 가장 많은 선택을 받은 날짜 응답 DTO
     */
    MeetingResponseDto.BestTime findBestTimeByMeetingId(Long meetingId);
}
