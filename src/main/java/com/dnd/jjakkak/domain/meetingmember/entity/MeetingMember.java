package com.dnd.jjakkak.domain.meetingmember.entity;

import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * 회원 모임 엔티티 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 30.
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingMember {
    @EmbeddedId
    private Pk pk;

    @MapsId("meetingId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Pk implements Serializable {

        @Column(name = "meeting_id")
        private Long meetingId;

        @Column(name = "member_id")
        private Long memberId;
    }
}
