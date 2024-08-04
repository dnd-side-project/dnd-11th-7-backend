package com.dnd.jjakkak.domain.schedule.controller;

import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleAssignRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleUpdateRequestDto;
import com.dnd.jjakkak.domain.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 일정 컨트롤러 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 29.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 일정을 할당하는 메서드입니다.
     *
     * @param authorization JWT Token
     * @param uuid          비회원 UUID
     * @param requestDto    일정 할당 요청 DTO
     * @return 200 (OK)
     */
    @PatchMapping("/assign")
    public ResponseEntity<Void> assignSchedule(@RequestHeader(value = "Authorization", required = false) String authorization,
                                               @RequestParam(value = "uuid", required = false) String uuid,
                                               @Valid @RequestBody ScheduleAssignRequestDto requestDto) {

        if (authorization == null) {
            scheduleService.assignScheduleToNonMember(uuid, requestDto);
        } else {
            scheduleService.assignScheduleToMember(authorization, requestDto);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * 일정을 수정하는 메서드입니다.
     *
     * @param scheduleId 일정 ID
     * @param requestDto 일정 수정 요청 DTO
     * @return 200 (OK)
     */
    @PatchMapping("/{scheduleId}")
    public ResponseEntity<Void> updateSchedule(@PathVariable("scheduleId") Long scheduleId,
                                               @Valid @RequestBody ScheduleUpdateRequestDto requestDto) {

        scheduleService.updateSchedule(scheduleId, requestDto);
        return ResponseEntity.ok().build();
    }
}

