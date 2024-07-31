package com.dnd.jjakkak.domain.schedule.controller;

import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleAssignRequestDto;
import com.dnd.jjakkak.domain.schedule.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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
     * @param uuid       비회원 UUID
     * @param requestDto 일정 할당 요청 DTO
     * @param request    HttpServletRequest
     * @return 200 (OK)
     */
    @PatchMapping("/assign")
    public ResponseEntity<Void> assignSchedule(@RequestParam(value = "uuid", required = false) String uuid,
                                               @Valid @RequestBody ScheduleAssignRequestDto requestDto,
                                               HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        // 비회원 - assignNonMember
        if (Objects.isNull(authorization)) {
            scheduleService.assignNonMember(uuid, requestDto);
            return ResponseEntity.ok().build();
        }

        // 회원 - assignMember
        scheduleService.assignMember(authorization, requestDto);
        return ResponseEntity.ok().build();
    }
}

