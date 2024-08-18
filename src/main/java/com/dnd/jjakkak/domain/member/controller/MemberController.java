package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.domain.member.dto.request.MemberUpdateNicknameRequestDto;
import com.dnd.jjakkak.domain.member.dto.request.MemberUpdateProfileRequestDto;
import com.dnd.jjakkak.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Member의 CRUD에 사용하는 컨트롤러입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 29.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;

//    /**
//     * 회원이 속한 모임 조회
//     *
//     * @param id 조회할 멤버 ID
//     * @return 200 (OK), body: 모임 응답 DTO
//     */
//    @GetMapping("/{memberId}/meetingList")
//    public ResponseEntity<List<MeetingResponseDto>> getMemberListByMemberId(@PathVariable("memberId") Long id) {
//        return ResponseEntity.ok(memberService.getMeetingListByMemberId(id));
//    }

    @PatchMapping("/{memberId}/nickname")
    public ResponseEntity<Void> updateNickname(
            @PathVariable("memberId") Long id,
            @Valid @RequestBody MemberUpdateNicknameRequestDto dto){
        memberService.updateNickname(id, dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{memberId}/profile")
    public ResponseEntity<Void> updateProfile(
            @PathVariable("memberId") Long id,
            @Valid @RequestBody MemberUpdateProfileRequestDto dto){
        memberService.updateProfile(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable("memberId") Long id){
        memberService.deleteMember(id);
        return ResponseEntity.ok().build();
    }
}
