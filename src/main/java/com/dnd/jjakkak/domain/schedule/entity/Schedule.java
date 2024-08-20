package com.dnd.jjakkak.domain.schedule.entity;

import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 일정 엔티티 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 29.
 */

@Entity
@Getter
@Table(
        indexes = {
                @Index(name = "IDX_SCHEDULE_MEETING_ID", columnList = "meeting_id"),
                @Index(name = "IDX_SCHEDULE_MEMBER_ID", columnList = "member_id"),
                @Index(name = "IDX_SCHEDULE_UUID", columnList = "schedule_uuid")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "schedule_nickname", nullable = false, length = 30)
    private String scheduleNickname;

    @Column(name = "schedule_uuid", nullable = false)
    private String scheduleUuid;

    @Column(name = "is_assigned", nullable = false)
    private Boolean isAssigned;

    @Builder
    public Schedule(Meeting meeting, Member member, String scheduleNickname, String scheduleUuid) {
        this.meeting = meeting;
        this.member = member;
        this.scheduleNickname = scheduleNickname;
        this.scheduleUuid = scheduleUuid;
        this.isAssigned = Boolean.FALSE;
    }

    /**
     * 일정의 닉네임을 수정하는 메서드입니다.
     *
     * @param scheduleNickname 새로운 닉네임
     */
    public void updateScheduleNickname(String scheduleNickname) {
        this.scheduleNickname = scheduleNickname;
    }

    /**
     * 일정의 멤버를 할당하는 메서드입니다.
     *
     * @param member 할당할 멤버
     */
    public void assignMember(Member member) {
        this.member = member;
    }


    /**
     * 일정을 할당하여 isAssigned 를 true 로 변경하는 메서드입니다.
     */
    public void scheduleAssign() {
        this.isAssigned = Boolean.TRUE;
    }
}
