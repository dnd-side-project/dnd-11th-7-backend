package com.dnd.jjakkak.domain.schedule.dto.response;

import com.dnd.jjakkak.domain.dateofschedule.dto.response.DateOfScheduleResponseDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 일정 응답 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 06.
 */
@Getter
public class ScheduleResponseDto {

    private final String scheduleNickname;
    private final String scheduleUuid;
    private final List<DateOfScheduleResponseDto> dateOfScheduleList;

    public ScheduleResponseDto(String scheduleNickname, String scheduleUuid) {
        this.scheduleNickname = scheduleNickname;
        this.scheduleUuid = scheduleUuid;
        this.dateOfScheduleList = new ArrayList<>();
    }

    public void addAllDateOfSchedule(List<DateOfScheduleResponseDto> dateOfScheduleList) {
        this.dateOfScheduleList.addAll(dateOfScheduleList);
    }
}