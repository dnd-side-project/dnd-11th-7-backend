package com.dnd.jjakkak.domain.member.exception;

import com.dnd.jjakkak.global.exception.GeneralException;

/**
 * 존재하지 않는 멤버 예외입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */

public class MemberNotFoundException extends GeneralException {
    private static final String MESSAGE = "ID에 해당하는 유저가 존재하지 않습니다.";

    public MemberNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
