package com.dnd.jjakkak.domain.schedule.controller;

import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleAssignRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleUpdateRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.response.ScheduleAssignResponseDto;
import com.dnd.jjakkak.domain.schedule.dto.response.ScheduleResponseDto;
import com.dnd.jjakkak.domain.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/meetings/{meetingUuid}/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 회원의 일정을 모임에 할당하는 메서드입니다.
     *
     * @param meetingUuid 모임 UUID
     * @param memberId    로그인한 회원 ID
     * @param requestDto  일정 할당 요청 DTO
     * @return 200 (OK)
     */
    @PatchMapping("/members")
    public ResponseEntity<Void> assignScheduleToMember(@PathVariable("meetingUuid") String meetingUuid,
                                                       @AuthenticationPrincipal Long memberId,
                                                       @Valid @RequestBody ScheduleAssignRequestDto requestDto) {

        scheduleService.assignScheduleToMember(memberId, meetingUuid, requestDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 비회원의 일정을 모임에 할당하는 메서드입니다.
     *
     * @param meetingUuid 모임 UUID
     * @param requestDto  일정 할당 요청 DTO
     * @return 200 (OK), 비회원일 경우, scheduleUuid 반환
     */
    @PatchMapping("/guests")
    public ResponseEntity<ScheduleAssignResponseDto> assignScheduleToGuest(@PathVariable("meetingUuid") String meetingUuid,
                                                                           @Valid @RequestBody ScheduleAssignRequestDto requestDto) {

        ScheduleAssignResponseDto responseDto = scheduleService.assignScheduleToGuest(meetingUuid, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 회원의 일정을 조회하는 메서드입니다.
     *
     * @param meetingUuid 모임 UUID
     * @param memberId    요청 회원 ID
     * @return 200 (OK), body: 일정 응답 DTO
     */
    @GetMapping("/members")
    public ResponseEntity<ScheduleResponseDto> getMemberSchedule(@PathVariable("meetingUuid") String meetingUuid,
                                                                 @AuthenticationPrincipal Long memberId) {

        ScheduleResponseDto responseDto = scheduleService.getMemberSchedule(meetingUuid, memberId);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 비회원의 일정을 조회하는 메서드입니다.
     *
     * @param meetingUuid  모임 UUID
     * @param scheduleUuid 일정 UUID (비회원)
     * @return 200 (OK), body: 일정 응답 DTO
     */
    @GetMapping("/guests")
    public ResponseEntity<ScheduleResponseDto> getGuestSchedule(@PathVariable("meetingUuid") String meetingUuid,
                                                                @RequestParam("scheduleUuid") String scheduleUuid) {

        ScheduleResponseDto responseDto = scheduleService.getGuestSchedule(meetingUuid, scheduleUuid);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 비회원의 일정을 수정하는 메서드입니다.
     *
     * @param scheduleUuid 일정 ID
     * @param requestDto   일정 수정 요청 DTO
     * @return 200 (OK)
     */
    @PatchMapping("/{scheduleUuid}")
    public ResponseEntity<Void> updateGuestSchedule(@PathVariable("meetingUuid") String meetingUuid,
                                                    @PathVariable("scheduleUuid") String scheduleUuid,
                                                    @Valid @RequestBody ScheduleUpdateRequestDto requestDto) {

        scheduleService.updateGuestSchedule(meetingUuid, scheduleUuid, requestDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 회원의 일정을 수정하는 메서드입니다.
     *
     * @param memberId    회원 ID
     * @param meetingUuid 모임 UUID
     * @param requestDto  일정 수정 요청 DTO
     * @return 200 (OK)
     */
    @PatchMapping
    public ResponseEntity<Void> updateMemberSchedule(@AuthenticationPrincipal Long memberId,
                                                     @PathVariable("meetingUuid") String meetingUuid,
                                                     @Valid @RequestBody ScheduleUpdateRequestDto requestDto) {

        scheduleService.updateMemberSchedule(memberId, meetingUuid, requestDto);
        return ResponseEntity.ok().build();
    }
}

