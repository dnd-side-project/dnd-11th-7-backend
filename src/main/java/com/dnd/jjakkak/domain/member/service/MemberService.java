package com.dnd.jjakkak.domain.member.service;

import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Member의 CRUD Service입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 29.
 */

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final MeetingRepository meetingRepository;

    /**
     * 해당 회원이 속한 모임 출력
     *
     * @param id 회원 ID
     * @return 모임 응답 DTO (모임 정보와 해당 모임의 최적 시간)
     */
    @Transactional(readOnly = true)
    public List<MeetingResponseDto> getMeetingListByMemberId(Long id) {
        List<MeetingResponseDto> responseList = new ArrayList<>();
        List<Meeting> meetingList = meetingMemberRepository.findByMemberId(id);

        for (Meeting meeting : meetingList) {
            MeetingResponseDto.BestTime bestTime = meetingRepository.findBestTimeByMeetingId(meeting.getMeetingId());

            MeetingResponseDto meetingResponse = MeetingResponseDto.builder()
                    .meeting(meeting)
                    .bestTime(bestTime)
                    .build();

            responseList.add(meetingResponse);
        }

        return responseList;
    }

    /**
     * 닉네임 수정
     *
     * @param id  Long
     * @param dto MemberUpdateNicknameRequestDto
     */
    @Transactional
    public void updateNickname(Long id, MemberUpdateNicknameRequestDto dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        member.updateNickname(dto.getMemberNickname());
    }

    /**
     * 프로필 수정
     *
     * @param id  Long
     * @param dto MemberUpdateProfileRequestDto
     */
    @Transactional
    public void updateProfile(Long id, MemberUpdateProfileRequestDto dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        member.updateProfile(dto.getMemberProfile());
    }

    /**
     * 회원 탈퇴
     *
     * @param id Long
     */
    @Transactional
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    /**
     * 매주 월요일 자정에 탈퇴 처리된 이용자 하드 삭제
     */
    @Scheduled(cron = "0 0 0 ? * MON") //매주 월요일 자정
    public void deletedMemberAllDeleted() {
        memberRepository.deleteAllByIsDeleteTrue();
    }


}
