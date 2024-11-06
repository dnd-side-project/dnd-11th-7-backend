package com.dnd.jjakkak.domain.jwt.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

/**
 * Refresh Token이 만료되었을 때 발생하는 예외입니다.
 *
 * @author 정승조
 * @version 2024. 11. 06.
 */
public class RefreshTokenExpiredException extends GeneralException {

    private static final String MESSAGE = "Refresh Token이 만료되었습니다. 다시 로그인 해주세요.";

    public RefreshTokenExpiredException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 418;
    }
}
