package com.dnd.jjakkak.domain.meeting.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

/**
 * {class name}.
 *
 * @author 정승조
 * @version 2024. 10. 03.
 */
public class MeetingAlreadyEndedException extends GeneralException {

    private static final String MESSAGE = "모임 일정 입력 시간이 종료되었습니다.";

    public MeetingAlreadyEndedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
