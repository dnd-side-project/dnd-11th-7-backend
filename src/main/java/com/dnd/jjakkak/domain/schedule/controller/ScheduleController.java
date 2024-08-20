package com.dnd.jjakkak.domain.schedule.controller;

import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleAssignRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleUpdateRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.response.ScheduleAssignResponseDto;
import com.dnd.jjakkak.domain.schedule.dto.response.ScheduleResponseDto;
import com.dnd.jjakkak.domain.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 일정 컨트롤러 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 29.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;


    /**
     * 회원의 일정을 할당하는 메서드입니다.
     *
     * @param memberId   인증된 회원 ID
     * @param requestDto 일정 할당 요청 DTO
     * @return 200 (OK)
     */
    @PatchMapping("/members/assign")
    public ResponseEntity<Void> assignScheduleToMember(@AuthenticationPrincipal Long memberId,
                                                       @Valid @RequestBody ScheduleAssignRequestDto requestDto) {

        scheduleService.assignScheduleToMember(memberId, requestDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 비회원의 일정을 할당하는 메서드입니다.
     *
     * @param requestDto 일정 할당 요청 DTO
     * @return 200 (OK)
     */
    @PatchMapping("/guests/assign")
    public ResponseEntity<ScheduleAssignResponseDto> assignScheduleToGuest(@Valid @RequestBody ScheduleAssignRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(scheduleService.assignScheduleToGuest(requestDto));
    }


    /**
     * 회원의 일정을 조회하는 메서드입니다.
     *
     * @param meetingId 모임 ID
     * @param memberId  인증된 회원 ID
     * @return 200 (OK), body: 일정 응답 DTO
     */
    @GetMapping("/members/{meetingId}")
    public ResponseEntity<ScheduleResponseDto> getMemberSchedule(@PathVariable("meetingId") Long meetingId,
                                                                 @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(scheduleService.getMemberSchedule(meetingId, memberId));
    }

    /**
     * 비회원의 일정을 조회하는 메서드입니다.
     *
     * @param meetingId 모임 ID
     * @param uuid      일정 UUID (비회원)
     * @return 200 (OK), body: 일정 응답 DTO
     */
    @GetMapping("/guests/{meetingId}")
    public ResponseEntity<ScheduleResponseDto> getGuestSchedule(@PathVariable("meetingId") Long meetingId,
                                                                @RequestParam("uuid") String uuid) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(scheduleService.getGuestSchedule(meetingId, uuid));
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

