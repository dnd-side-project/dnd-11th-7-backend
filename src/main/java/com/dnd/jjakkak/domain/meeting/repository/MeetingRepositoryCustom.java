package com.dnd.jjakkak.domain.meeting.repository;

import com.dnd.jjakkak.domain.meeting.dto.response.MeetingInfoResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingParticipantResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingTimeResponseDto;
import com.dnd.jjakkak.global.common.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.LocalDateTime;

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
     * @param meetingUuid 모임 UUID
     * @return 모임의 인원이 꽉 찼는지 여부
     */
    boolean checkMeetingFull(String meetingUuid);

    /**
     * 모임이 익명인지 확인합니다.
     *
     * @param meetingId 모임 ID
     * @return 모임이 익명인지 여부
     */
    boolean isAnonymous(Long meetingId);

    /**
     * 모임의 UUID로 모임 정보를 조회합니다.
     *
     * @param uuid 모임 UUID
     * @return 모임 정보 응답 DTO
     */
    MeetingInfoResponseDto getMeetingInfo(String uuid);


    /**
     * 모임의 UUID로 시간을 조회합니다.
     *
     * @param uuid        모임 UUID
     * @param pageable    페이지 정보
     * @param requestTime 요청 시간
     * @return 정렬된 시간 응답 DTO 리스트
     */
    PagedResponse<MeetingTimeResponseDto> getMeetingTimes(String uuid, Pageable pageable, LocalDateTime requestTime);

    /**
     * 모임의 UUID로 참가자를 조회합니다.
     *
     * @param uuid 모임 UUID
     * @return 참가자 응답 DTO
     */
    MeetingParticipantResponseDto getParticipant(String uuid);

    /**
     * 회원 ID와 모임 UUID로 모임 일정 할당 여부를 파악합니다.
     *
     * @param memberId    회원 ID
     * @param meetingUuid 모임 UUID
     * @return 모임 일정 할당 여부
     */
    boolean existsByMemberIdAndMeetingUuid(Long memberId, String meetingUuid);

    /**
     * 모임 UUID로 최적의 시간을 조회합니다.
     *
     * @param uuid 모임 UUID
     * @return 최적의 시간
     */
    LocalDateTime getBestTime(String uuid);

    /**
     * 모임의 UUID로 전체 일정을 조회합니다.
     *
     * @param uuid 모임 UUId
     * @return 모임 시간 응답 DTO
     */
    MeetingTimeResponseDto getMeetingAllTimes(String uuid);
}
