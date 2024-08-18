package com.dnd.jjakkak.domain.schedule.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

/**
 * 일정을 찾지 못했을 때 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 07. 29.
 */
public class ScheduleNotFoundException extends GeneralException {

    private static final String MESSAGE = "일정을 찾을 수 없습니다.";

    public ScheduleNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
