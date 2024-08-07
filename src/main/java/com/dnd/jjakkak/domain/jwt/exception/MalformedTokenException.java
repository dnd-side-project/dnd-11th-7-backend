package com.dnd.jjakkak.domain.jwt.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

public class MalformedTokenException extends GeneralException {
    private static final String MESSAGE = "손상된 토큰.";

    public MalformedTokenException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
