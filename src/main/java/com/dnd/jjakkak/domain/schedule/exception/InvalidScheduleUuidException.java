package com.dnd.jjakkak.domain.schedule.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

/**
 * 일정 식별자가 올바르지 않을 때 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 09. 20.
 */
public class InvalidScheduleUuidException extends GeneralException {

    private static final String MESSAGE = "식별자가 올바르지 않습니다.";

    public InvalidScheduleUuidException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
