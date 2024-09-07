package com.dnd.jjakkak.domain.schedule.dto.request;

import com.dnd.jjakkak.domain.dateofschedule.dto.request.DateOfScheduleCreateRequestDto;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 일정 수정 요청 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 31.
 */
@Getter
public class ScheduleUpdateRequestDto {

    @Size(min = 1, message = "일정 날짜를 최소 1개 이상 입력해주세요.")
    private final List<DateOfScheduleCreateRequestDto> dateOfScheduleList = new ArrayList<>();

    private String scheduleNickname;
}
