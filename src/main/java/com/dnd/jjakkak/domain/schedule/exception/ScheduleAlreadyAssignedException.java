package com.dnd.jjakkak.domain.schedule.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

/**
 * 이미 할당된 일정일 때 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 07. 29.
 */
public class ScheduleAlreadyAssignedException extends GeneralException {

    private static final String MESSAGE = "이미 할당된 일정입니다.";

    public ScheduleAlreadyAssignedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 409;
    }
}
