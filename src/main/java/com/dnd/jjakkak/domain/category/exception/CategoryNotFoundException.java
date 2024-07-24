package com.dnd.jjakkak.domain.category.exception;

import com.dnd.zzaekkac.global.exception.GeneralException;

/**
 * 존재하지 않는 카테고리 예외입니다.
 *
 * @author 정승조
 * @version 2024. 07. 24.
 */
public class CategoryNotFoundException extends GeneralException {

    private static final String MESSAGE = "카테고리가 존재하지 않습니다.";

    public CategoryNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
