package com.dnd.jjakkak.domain.schedule.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 비회원의 일정 할당 후 응답 DTO 클래스입니다. (일정 UUID)
 *
 * @author 정승조
 * @version 2024. 08. 06.
 */
@Getter
public class ScheduleAssignResponseDto {

    private final String scheduleUuid;

    @Builder
    public ScheduleAssignResponseDto(String scheduleUuid) {
        this.scheduleUuid = scheduleUuid;
    }
}
