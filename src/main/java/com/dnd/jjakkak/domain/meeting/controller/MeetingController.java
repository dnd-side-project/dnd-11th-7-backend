package com.dnd.jjakkak.domain.meeting.controller;

import com.dnd.jjakkak.domain.meeting.dto.request.MeetingConfirmRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingCreateResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
import com.dnd.jjakkak.domain.meeting.service.MeetingService;
import com.dnd.jjakkak.domain.member.dto.response.MemberResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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
     * @param accessToken JWT Token (Access Token)
     * @param requestDto  모임 생성 요청 DTO
     * @return 201 (CREATED), body: 모임 생성 응답 DTO (UUID)
     */
    @PostMapping
    public ResponseEntity<MeetingCreateResponseDto> createGroup(@RequestHeader("Authorization") String accessToken,
                                                                @Valid @RequestBody MeetingCreateRequestDto requestDto) {

        if (Objects.isNull(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(meetingService.createMeeting(accessToken, requestDto));
    }

    /**
     * 모임의 UUID로 모임을 조회하는 메서드입니다.
     *
     * @param uuid 조회할 모임 UUID
     * @return 200 (OK), body: 모임 응답 DTO
     */
    @GetMapping("/{meetingUuid}")
    public ResponseEntity<MeetingResponseDto> getMeetingByUuid(@PathVariable("meetingUuid") String uuid) {
        return ResponseEntity.ok(meetingService.getMeetingByUuid(uuid));
    }

    /**
     * 모임에 속한 회원 조회
     *
     * @param id 조회할 모임 ID
     * @return 200 (OK), body: 회원 응답 DTO
     */
    @GetMapping("/{meetingId}/memberList")
    public ResponseEntity<List<MemberResponseDto>> getMemberListByMemberId(@PathVariable("meetingId") Long id) {
        return ResponseEntity.ok(meetingService.getMeetingListByMeetingId(id));
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
     * @param accessToken JWT Token (Access Token)
     * @param id          삭제할 모임 ID
     * @return 200 (OK)
     */
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<Void> deleteMeeting(@RequestHeader("Authorization") String accessToken,
                                              @PathVariable("meetingId") Long id) {

        if (Objects.isNull(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        meetingService.deleteMeeting(accessToken, id);
        return ResponseEntity.ok().build();
    }
}
