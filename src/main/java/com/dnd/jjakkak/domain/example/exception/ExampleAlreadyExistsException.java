package com.dnd.jjakkak.domain.example.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

/**
 * 이미 존재하는 예시 예외.
 *
 * @author 정승조
 * @version 2024. 07. 19.
 */
public class ExampleAlreadyExistsException extends GeneralException {

    private static final String MESSAGE = "이미 존재하는 예시입니다.";

    public ExampleAlreadyExistsException() {
        super(MESSAGE);
    }


    @Override
    public int getStatusCode() {
        return 409;
    }
}
