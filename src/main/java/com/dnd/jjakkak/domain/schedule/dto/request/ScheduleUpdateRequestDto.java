package com.dnd.jjakkak.domain.schedule.dto.request;

import com.dnd.jjakkak.domain.dateofschedule.dto.request.DateOfScheduleCreateRequestDto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * {class name}.
 *
 * @author 정승조
 * @version 2024. 07. 31.
 */
@Getter
public class ScheduleUpdateRequestDto {

    @NotNull(message = "일정 날짜를 입력해주세요.")
    private final List<DateOfScheduleCreateRequestDto> dateOfScheduleList = new ArrayList<>();

    private String scheduleNickname;
}
