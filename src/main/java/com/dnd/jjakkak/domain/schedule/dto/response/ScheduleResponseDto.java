package com.dnd.jjakkak.domain.schedule.dto.response;

import com.dnd.jjakkak.domain.schedule.entity.Schedule;
import lombok.Getter;

/**
 * 일정 응답 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 06.
 */
@Getter
public class ScheduleResponseDto {

    private final Long scheduleId;
    private final Long meetingId;
    private final Long memberId;
    private final String scheduleNickname;
    private final String scheduleUuid;
    private final Boolean isAssigned;

    public ScheduleResponseDto(Schedule schedule) {
        this.scheduleId = schedule.getScheduleId();
        this.meetingId = schedule.getMeeting().getMeetingId();
        this.memberId = schedule.getMember().getMemberId();
        this.scheduleNickname = schedule.getScheduleNickname();
        this.scheduleUuid = schedule.getScheduleUuid();
        this.isAssigned = schedule.getIsAssigned();
    }
}
