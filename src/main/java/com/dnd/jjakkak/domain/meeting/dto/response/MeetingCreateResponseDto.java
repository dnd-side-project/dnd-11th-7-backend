package com.dnd.jjakkak.domain.meeting.dto.response;

import lombok.Getter;

/**
 * 모임이 생성된 후 UUID를 반환하는 응답 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 01.
 */
@Getter
public class MeetingCreateResponseDto {

    private final String meetingUuid;

    public MeetingCreateResponseDto(String meetingUuid) {
        this.meetingUuid = meetingUuid;
    }
}
