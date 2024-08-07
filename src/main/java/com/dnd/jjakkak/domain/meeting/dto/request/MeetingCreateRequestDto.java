package com.dnd.jjakkak.domain.meeting.dto.request;

import com.dnd.jjakkak.domain.meeting.exception.InvalidMeetingDateException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 모임 생성 요청 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 25.
 */
@Getter
@ToString
public class MeetingCreateRequestDto {

    @Size(min = 1, max = 3, message = "카테고리는 최소 1개 이상 3개 이하로 선택해주세요.")
    private final List<Long> categoryIds = new ArrayList<>();

    @NotBlank(message = "모임명은 필수 값입니다.")
    @Size(max = 20, message = "모임명을 20자 이내로 입력해주세요.")
    private String meetingName;

    @NotNull(message = "모임 일정 시작일은 필수 값입니다.")
    private LocalDate meetingStartDate;

    @NotNull(message = "모임 일정 종료일은 필수 값입니다.")
    private LocalDate meetingEndDate;

    @NotNull(message = "인원수는 필수 값입니다.")
    @Max(value = 10, message = "인원수는 10명 이하로 입력해주세요.")
    private Integer numberOfPeople;

    @NotNull(message = "익명 여부는 필수 값입니다.")
    private Boolean isAnonymous;

    @NotNull(message = "투표 종료일은 필수 값입니다.")
    private LocalDateTime voteEndDate;

    /**
     * 모임 일정을 검증하는 메서드입니다.
     *
     * @throws InvalidMeetingDateException 모임 일정이 유효하지 않을 경우 발생합니다.
     */
    public void checkMeetingDate() {
        InvalidMeetingDateException invalidMeetingDateException = new InvalidMeetingDateException();

        if (meetingStartDate.isAfter(meetingEndDate)) {
            invalidMeetingDateException.addValidation(
                    "meetingStartDate",
                    "모임 시작일은 종료일 이전으로 설정해주세요.");
        }

        if (voteEndDate.isAfter(meetingStartDate.atStartOfDay())) {
            invalidMeetingDateException.addValidation(
                    "voteEndDate",
                    "투표 종료일은 모임 시작일 이전으로 설정해주세요.");
        }

        if (ChronoUnit.DAYS.between(meetingEndDate, meetingStartDate) >= 14) {
            invalidMeetingDateException.addValidation(
                    "meetingStartDate, meetingEndDate",
                    "모임 시작일과 종료일은 최대 14일까지 설정 가능합니다.");
        }

        if (!invalidMeetingDateException.getValidation().isEmpty()) {
            throw invalidMeetingDateException;
        }
    }
}
