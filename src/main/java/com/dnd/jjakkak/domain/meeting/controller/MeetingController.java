package com.dnd.jjakkak.domain.meeting.controller;

import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingCreateResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
import com.dnd.jjakkak.domain.meeting.service.MeetingService;
import com.dnd.jjakkak.domain.member.dto.response.MemberResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
     * @param user       로그인한 회원 정보
     * @param requestDto 모임 생성 요청 DTO
     * @return 201 (CREATED), body: 모임 생성 응답 DTO (UUID)
     */
    @PostMapping
    public ResponseEntity<MeetingCreateResponseDto> createGroup(@AuthenticationPrincipal OAuth2User user,
                                                                @Valid @RequestBody MeetingCreateRequestDto requestDto) {

        MeetingCreateResponseDto response = meetingService.createMeeting(user, requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
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
     * 모임을 삭제하는 메서드입니다.
     *
     * @param user 로그인한 회원 정보
     * @param id   삭제할 모임 ID
     * @return 200 (OK)
     */
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<Void> deleteMeeting(@AuthenticationPrincipal OAuth2User user,
                                              @PathVariable("meetingId") Long id) {

        meetingService.deleteMeeting(user, id);
        return ResponseEntity.ok().build();
    }
}