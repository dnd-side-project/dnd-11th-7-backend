package com.dnd.jjakkak.domain.meeting.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

/**
 * 모임에 대한 권한이 없을 때 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 08. 01.
 */
public class MeetingUnauthorizedException extends GeneralException {

    private static final String MESSAGE = "모임에 대한 권한이 없습니다.";

    public MeetingUnauthorizedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
