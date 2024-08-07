package com.dnd.jjakkak.domain.member.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

/**
 * 로그인이 필요한 경우 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 08. 07.
 */
public class UnauthorizedException extends GeneralException {

    private static final String MESSAGE = "로그인이 필요합니다.";

    public UnauthorizedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
