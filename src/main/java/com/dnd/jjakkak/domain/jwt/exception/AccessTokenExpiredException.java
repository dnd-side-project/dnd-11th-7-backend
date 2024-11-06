package com.dnd.jjakkak.domain.jwt.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

public class AccessTokenExpiredException extends GeneralException {

    private static final String MESSAGE = "Access Token이 만료되었습니다.";

    public AccessTokenExpiredException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
