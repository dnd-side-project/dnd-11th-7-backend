package com.dnd.jjakkak.domain.schedule;

import com.dnd.jjakkak.domain.dateofschedule.dto.request.DateOfScheduleCreateRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleAssignRequestDto;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 일정 관련 Dummy 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 09. 06.
 */
public class ScheduleDummy {

    private ScheduleDummy() {
    }


    public static ScheduleAssignRequestDto invalidAssignRequestDto() {
        return new ScheduleAssignRequestDto();
    }

    public static ScheduleAssignRequestDto assignRequestDto() {
        ScheduleAssignRequestDto requestDto = new ScheduleAssignRequestDto();

        DateOfScheduleCreateRequestDto firstDto = new DateOfScheduleCreateRequestDto();
        ReflectionTestUtils.setField(firstDto, "startTime", LocalDateTime.of(2024, 9, 6, 9, 0, 0));
        ReflectionTestUtils.setField(firstDto, "endTime", LocalDateTime.of(2024, 9, 6, 12, 0, 0));
        ReflectionTestUtils.setField(firstDto, "rank", 1);

        DateOfScheduleCreateRequestDto secondDto = new DateOfScheduleCreateRequestDto();
        ReflectionTestUtils.setField(secondDto, "startTime", LocalDateTime.of(2024, 9, 6, 18, 0, 0));
        ReflectionTestUtils.setField(secondDto, "endTime", LocalDateTime.of(2024, 9, 6, 21, 0, 0));
        ReflectionTestUtils.setField(secondDto, "rank", 2);

        List<DateOfScheduleCreateRequestDto> dateOfScheduleList = List.of(firstDto, secondDto);

        ReflectionTestUtils.setField(requestDto, "dateOfScheduleList", dateOfScheduleList);
        ReflectionTestUtils.setField(requestDto, "nickName", "정승조");

        return requestDto;
    }


}
