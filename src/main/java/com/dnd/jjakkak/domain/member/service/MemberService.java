package com.dnd.jjakkak.domain.member.service;

import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meetingmember.repository.MeetingMemberRepository;
import com.dnd.jjakkak.domain.member.dto.request.MemberUpdateNicknameRequestDto;
import com.dnd.jjakkak.domain.member.dto.request.MemberUpdateProfileRequestDto;
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
     * @param id 회원 ID
     * @return 모임 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<MeetingResponseDto> getMeetingListByMemberId(Long id) {
        List<Meeting> meetingList = meetingMemberRepository.findByMemberId(id);
        return meetingList.stream().map(MeetingResponseDto::new).toList();
    }

    /**
     * 회원의 닉네임을 수정합니다.
     *
     * @param id  회원 ID
     * @param dto 닉네임 수정 요청 DTO
     */
    @Transactional
    public void updateNickname(Long id, MemberUpdateNicknameRequestDto dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        member.updateNickname(dto.getMemberNickname());
    }

    /**
     * 회원의 프로필을 수정합니다.
     *
     * @param id  회원 ID
     * @param dto 프로필 수정 요청 DTO
     */
    @Transactional
    public void updateProfile(Long id, MemberUpdateProfileRequestDto dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        member.updateProfile(dto.getMemberProfile());
    }

    /**
     * 회원 탈퇴를 처리합니다. (Soft Delete)
     *
     * @param id 회원 ID
     */
    @Transactional
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    /**
     * 매주 월요일 자정에 탈퇴 처리된 이용자 하드 삭제
     */
    @Scheduled(cron = "0 0 0 ? * MON")
    public void deletedMemberAllDeleted() {
        memberRepository.deleteAllByIsDeleteTrue();
    }

}
