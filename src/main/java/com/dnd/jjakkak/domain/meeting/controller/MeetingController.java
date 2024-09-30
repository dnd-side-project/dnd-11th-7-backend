package com.dnd.jjakkak.domain.meeting.controller;

import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingCreateResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingInfoResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingParticipantResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingTimeResponseDto;
import com.dnd.jjakkak.domain.meeting.service.MeetingService;
import com.dnd.jjakkak.domain.member.dto.response.MemberResponseDto;
import com.dnd.jjakkak.global.common.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 모임 컨트롤러 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 25.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    /**
     * 모임을 생성하는 메서드입니다.
     *
     * @param memberId   인증된 회원 ID
     * @param requestDto 모임 생성 요청 DTO
     * @return 201 (CREATED), body: 모임 생성 응답 DTO (UUID)
     */
    @PostMapping
    public ResponseEntity<MeetingCreateResponseDto> createMeeting(@AuthenticationPrincipal Long memberId,
                                                                  @Valid @RequestBody MeetingCreateRequestDto requestDto) {

        MeetingCreateResponseDto response = meetingService.createMeeting(memberId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 모임의 정보를 조회하는 메서드입니다.
     *
     * @param uuid 조회할 모임 UUID
     * @return 200 (OK), body: 모임 정보 응답 DTO
     */
    @GetMapping("/{meetingUuid}/info")
    public ResponseEntity<MeetingInfoResponseDto> getMeetingInfo(@PathVariable("meetingUuid") String uuid) {
        return ResponseEntity.ok(meetingService.getMeetingInfo(uuid));
    }

    /**
     * 모임 시간을 조회하는 메서드입니다.
     *
     * @param uuid 조회할 모임 UUID
     * @param pageable 페이징 정보 (default: page = 0, size = 10, sort = count)
     * @return 200 (OK), body: 모임 시간 응답 DTO
     */
    @GetMapping("/{meetingUuid}/times")
    public ResponseEntity<PagedResponse<MeetingTimeResponseDto>> getMeetingTimes(
            @PathVariable("meetingUuid") String uuid,
            @PageableDefault(sort = "count") Pageable pageable,
            @RequestParam(value = "request_time", required = false) String requestTime) {

        LocalDateTime time = LocalDateTime.now();

        if (requestTime != null) {
            time = LocalDateTime.parse(requestTime);
        }

        PagedResponse<MeetingTimeResponseDto> responseDto = meetingService.getMeetingTimes(uuid, pageable, time);
        responseDto.getData().setRequestTime(time);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 모임의 참여자를 조회하는 메서드입니다.
     *
     * @param uuid 조회할 모임 UUID
     * @return 200 (OK), body: 참여자 응답 DTO
     */
    @GetMapping("/{meetingUuid}/participants")
    public ResponseEntity<MeetingParticipantResponseDto> getParticipant(@PathVariable("meetingUuid") String uuid) {
        return ResponseEntity.ok(meetingService.getParticipants(uuid));
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
     * 모임을 삭제하는 메서드입니다.
     *
     * @param memberId 인증된 회원 ID
     * @param id       삭제할 모임 ID
     * @return 200 (OK)
     */
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<Void> deleteMeeting(@AuthenticationPrincipal Long memberId,
                                              @PathVariable("meetingId") Long id) {

        meetingService.deleteMeeting(memberId, id);
        return ResponseEntity.ok().build();
    }
}