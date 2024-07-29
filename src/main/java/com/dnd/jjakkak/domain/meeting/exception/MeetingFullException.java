package com.dnd.jjakkak.domain.meeting.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

/**
 * 모임의 인원이 꽉 찼을 때 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 07. 29.
 */
public class MeetingFullException extends GeneralException {

    private static final String MESSAGE = "모임의 인원이 꽉 찼습니다.";

    public MeetingFullException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 0;
    }
}
