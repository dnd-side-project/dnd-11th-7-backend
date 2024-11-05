package com.dnd.jjakkak.domain.jwt.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

public class TokenExpiredException extends GeneralException {

    private static final String MESSAGE = "JWT 토큰이 만료되었습니다.";

    public TokenExpiredException() {
        super(MESSAGE);
    }

    public TokenExpiredException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
