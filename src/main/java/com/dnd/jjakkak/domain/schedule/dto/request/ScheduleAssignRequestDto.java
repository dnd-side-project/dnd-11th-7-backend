package com.dnd.jjakkak.domain.schedule.dto.request;

import lombok.Getter;

/**
 * 일정 할당 요청 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 29.
 */
@Getter
public class ScheduleAssignRequestDto {

//    @NotNull(message = "일정 날짜를 입력해주세요.")
//    private final List<DateOfSchedule> dateOfScheduleList = new ArrayList<>();

    private String nickName;

    private Long memberId;
}
