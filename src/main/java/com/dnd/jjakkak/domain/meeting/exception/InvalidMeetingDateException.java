package com.dnd.jjakkak.domain.meeting.exception;

import com.dnd.jjakkak.global.exception.GeneralException;
import lombok.Getter;

/**
 * 모임 일정이 유효하지 않을 때 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 07. 25.
 */
@Getter
public class InvalidMeetingDateException extends GeneralException {

    private static final String MESSAGE = "모임 일정이 유효하지 않습니다.";

    public InvalidMeetingDateException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
