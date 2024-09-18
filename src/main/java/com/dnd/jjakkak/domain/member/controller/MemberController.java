package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.domain.meeting.dto.response.MeetingMyPageResponseDto;
import com.dnd.jjakkak.domain.member.dto.request.MemberUpdateNicknameRequestDto;
import com.dnd.jjakkak.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 회원 컨트롤러 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 29.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {
    private final MemberService memberService;

    /**
     * 회원이 속한 모임을 조회합니다.
     *
     * @param memberId 조회할 회원 ID
     * @return 200 (OK), body: 모임 응답 DTO 리스트
     */
    @GetMapping("/meetings")
    public ResponseEntity<List<MeetingMyPageResponseDto>> getMeetingList(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(memberService.getMeetingListByMemberId(memberId));
    }

    /**
     * 회원의 닉네임을 변경합니다.
     *
     * @param memberId   회원 ID
     * @param requestDto 닉네임 변경 요청 DTO
     * @return 200 (OK)
     */
    @PatchMapping("/nickname")
    public ResponseEntity<Void> updateNickname(@AuthenticationPrincipal Long memberId,
                                               @Valid @RequestBody MemberUpdateNicknameRequestDto requestDto) {

        memberService.updateNickname(memberId, requestDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 회원 탈퇴를 진행합니다.
     *
     * @param memberId 회원 ID
     * @return 200 (OK)
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.ok().build();
    }
}
