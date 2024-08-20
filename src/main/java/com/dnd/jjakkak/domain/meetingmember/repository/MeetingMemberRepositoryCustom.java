package com.dnd.jjakkak.domain.meetingmember.repository;

import com.dnd.jjakkak.domain.meeting.dto.response.MeetingInfoResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.member.entity.Member;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * 회원 모임 Querydsl 메서드 정의 인터페이스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 30.
 */

@NoRepositoryBean
public interface MeetingMemberRepositoryCustom {
    /**
     * 회원 ID로 회원에 속한 모든 모임 찾기
     *
     * @param memberId 회원 ID
     */
    List<Meeting> findByMemberId(Long memberId);

    /**
     * 모임 ID로 모임에 속한 모든 회원 찾기
     *
     * @param meetingId 모임 ID
     */
    List<Member> findByMeetingId(Long meetingId);

    /**
     * 회원 ID로 회원이 속한 모든 모임 정보 찾기
     *
     * @param memberId 회원 ID
     * @return 모임 정보 응답 DTO 리스트
     */
    List<MeetingInfoResponseDto> findMeetingInfoByMemberId(Long memberId);
}
