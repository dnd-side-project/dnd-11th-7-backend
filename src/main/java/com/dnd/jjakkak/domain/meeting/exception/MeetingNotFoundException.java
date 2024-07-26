package com.dnd.jjakkak.domain.meeting.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

/**
 * {class name}.
 *
 * @author 정승조
 * @version 2024. 07. 26.
 */
public class MeetingNotFoundException extends GeneralException {

    private static final String MESSAGE = "모임을 찾을 수 없습니다.";

    public MeetingNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
