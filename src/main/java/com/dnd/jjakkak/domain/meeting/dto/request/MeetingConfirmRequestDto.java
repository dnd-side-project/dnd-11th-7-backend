package com.dnd.jjakkak.domain.meeting.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 모임의 확정된 일자 설정 요청 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 28.
 */
@Getter
public class MeetingConfirmRequestDto {

    @NotNull(message = "확정 일정은 필수 값입니다.")
    private LocalDateTime confirmedSchedule;
}
