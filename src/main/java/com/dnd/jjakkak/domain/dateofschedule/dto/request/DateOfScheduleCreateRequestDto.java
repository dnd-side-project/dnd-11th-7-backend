package com.dnd.jjakkak.domain.dateofschedule.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 일정 날짜 생성 요청 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 30.
 */
@Getter
public class DateOfScheduleCreateRequestDto {

    private LocalDateTime dateOfScheduleStart;
    private LocalDateTime dateOfScheduleEnd;
    private Integer dateOfScheduleRank;
}
