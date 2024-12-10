package com.dnd.jjakkak.domain.dateofschedule.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

/**
 * 일정 날짜가 모임 날짜를 벗어난 경우 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 08. 20.
 */
public class ScheduleDateOutOfMeetingDateException extends GeneralException {

    private static final String MESSAGE = "모임 기간을 벗어나는 일정 날짜는 생성할 수 없습니다.";

    public ScheduleDateOutOfMeetingDateException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
