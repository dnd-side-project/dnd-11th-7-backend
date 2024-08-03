package com.dnd.jjakkak.domain.member.jwt.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

public class AccessTokenExpiredException extends GeneralException {
    private static final String MESSAGE = "엑세스 토큰이 만료됨.";

    public AccessTokenExpiredException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
