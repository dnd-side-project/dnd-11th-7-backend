package com.dnd.zzaekkac.domain.example.exception;

import com.dnd.zzaekkac.global.exception.GeneralException;

/**
 * Example NotFound 예외.
 * <p>status: 404</p>
 *
 * @author 정승조
 * @version 2024. 07. 17.
 */
public class ExampleNotFoundException extends GeneralException {

    private static final String MESSAGE = "존재하지 않는 예시입니다.";

    public ExampleNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
