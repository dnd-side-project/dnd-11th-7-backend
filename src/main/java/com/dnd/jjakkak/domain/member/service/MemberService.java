package com.dnd.jjakkak.domain.member.service;

import com.dnd.jjakkak.domain.meeting.dto.response.MeetingMyPageResponseDto;
import com.dnd.jjakkak.domain.meetingmember.repository.MeetingMemberRepository;
import com.dnd.jjakkak.domain.member.dto.request.MemberUpdateNicknameRequestDto;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.exception.MemberNotFoundException;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Member Service 입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 29.
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MeetingMemberRepository meetingMemberRepository;

    /**
     * 해당 회원이 속한 모임 출력
     *
     * @param memberId 회원 ID
     * @return 모임 정보 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<MeetingMyPageResponseDto> getMeetingListByMemberId(Long memberId) {

        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException();
        }

        return meetingMemberRepository.findMeetingInfoByMemberId(memberId);
    }

    /**
     * 회원의 닉네임을 수정합니다.
     *
     * @param memberId 회원 ID
     * @param requestDto      닉네임 수정 요청 DTO
     */
    @Transactional
    public void updateNickname(Long memberId, MemberUpdateNicknameRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        member.updateNickname(requestDto.getMemberNickname());
    }

    /**
     * 회원 탈퇴를 처리합니다. (Soft Delete)
     *
     * @param memberId 회원 ID
     */
    @Transactional
    public void deleteMember(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    /**
     * 매주 월요일 자정에 탈퇴 처리된 이용자 하드 삭제
     */
    @Transactional
    @Scheduled(cron = "0 0 0 ? * MON")
    public void deletedMemberAllDeleted() {
        memberRepository.deleteAllByIsDeleteTrue();
    }

}
