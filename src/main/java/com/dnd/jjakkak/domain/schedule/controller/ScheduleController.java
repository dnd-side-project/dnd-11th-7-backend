package com.dnd.jjakkak.domain.schedule.controller;

import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleAssignRequestDto;
import com.dnd.jjakkak.domain.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 일정 컨트롤러 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 29.
 */
@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 일정을 할당하는 메서드입니다.
     *
     * @param scheduleUuid 일정 UUID
     * @param requestDto   일정 할당 요청 DTO
     */
    @PatchMapping("/schedule/{scheduleUuid}")
    public void assignSchedule(@PathVariable("scheduleUuid") String scheduleUuid,
                               @Valid @RequestBody ScheduleAssignRequestDto requestDto) {
        scheduleService.assignSchedule(scheduleUuid, requestDto);
    }
}
