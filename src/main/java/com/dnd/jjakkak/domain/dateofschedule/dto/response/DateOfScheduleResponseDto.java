package com.dnd.jjakkak.domain.dateofschedule.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 일정 날짜 응답 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 09. 20.
 */
@Getter
public class DateOfScheduleResponseDto {

    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public DateOfScheduleResponseDto(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
