package com.dnd.jjakkak.domain.meeting.repository;

/**
 * 모임 Querydsl 메서드를 정의하는 인터페이스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 29.
 */
public interface MeetingRepositoryCustom {

    boolean checkMeetingFull(Long meetingId);
}
