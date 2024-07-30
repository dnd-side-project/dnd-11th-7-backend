package com.dnd.jjakkak.domain.meetingmember.repository;

import com.dnd.jjakkak.domain.meetingmember.entity.MeetingMember;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 회원 모임 JPA 레포지토리입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 30.
 */

public interface MeetingMemberRepository
        extends JpaRepository<MeetingMember, MeetingMember.Pk>, MeetingMemberRepositoryCustom {
}
