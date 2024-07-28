package com.dnd.jjakkak.domain.meeting.controller;

import com.dnd.jjakkak.domain.meeting.dto.request.MeetingConfirmRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingUpdateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
import com.dnd.jjakkak.domain.meeting.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 모임 컨트롤러 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 25.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meeting")
public class MeetingController {

    private final MeetingService meetingService;

    /**
     * 모임을 생성하는 메서드입니다.
     *
     * @param requestDto 모임 생성 요청 DTO
     * @return 201 (CREATED)
     */
    @PostMapping
    public ResponseEntity<Void> createGroup(@Valid @RequestBody MeetingCreateRequestDto requestDto) {
        meetingService.createMeeting(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 전체 모임을 조회하는 메서드입니다.
     *
     * @return 200 (OK), body: 모임 응답 DTO 리스트
     */
    @GetMapping
    public ResponseEntity<List<MeetingResponseDto>> getMeetingList() {
        return ResponseEntity.ok(meetingService.getMeetingList());
    }

    /**
     * 특정 모임을 조회하는 메서드입니다.
     *
     * @param id 조회할 모임 ID
     * @return 200 (OK), body: 모임 응답 DTO
     */
    @GetMapping("/{meetingId}")
    public ResponseEntity<MeetingResponseDto> getMeeting(@PathVariable("meetingId") Long id) {
        return ResponseEntity.ok(meetingService.getMeeting(id));
    }

    /**
     * 모임을 수정하는 메서드입니다.
     *
     * @param id         모임 ID
     * @param requestDto 수정된 모임 정보 DTO
     * @return 200 (OK)
     */
    @PatchMapping("/{meetingId}")
    public ResponseEntity<Void> updateMeeting(@PathVariable("meetingId") Long id,
                                              @Valid @RequestBody MeetingUpdateRequestDto requestDto) {

        meetingService.updateMeeting(id, requestDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 모임의 확정된 일자를 수정하는 메서드입니다.
     *
     * @param id         모임 ID
     * @param requestDto 확정된 일자 수정 요청 DTO
     * @return 200 (OK)
     */
    @PatchMapping("/{meetingId}/confirm")
    public ResponseEntity<Void> confirmMeeting(@PathVariable("meetingId") Long id,
                                               @Valid @RequestBody MeetingConfirmRequestDto requestDto) {
        meetingService.confirmMeeting(id, requestDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 모임을 삭제하는 메서드입니다.
     *
     * @param id 삭제할 모임 ID
     * @return 200 (OK)
     */
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable("meetingId") Long id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.ok().build();
    }
}
