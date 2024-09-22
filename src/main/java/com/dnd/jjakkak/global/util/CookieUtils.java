package com.dnd.jjakkak.global.util;

import org.springframework.http.ResponseCookie;

public class CookieUtils {

    private CookieUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 쿠키를 지우는 메서드입니다.
     *
     * @param name 쿠키 이름
     * @return 삭제할 쿠키
     */
    public static ResponseCookie deleteCookie(String name) {

        return ResponseCookie.from(name, "")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
    }

    /**
     * 쿠키를 생성하는 메서드입니다.
     *
     * @param name   쿠키 이름
     * @param value  쿠키 값
     * @param maxAge 쿠키 만료 시간
     * @return 생성된 쿠키
     */
    public static ResponseCookie createCookie(String name, String value, int maxAge) {

        return ResponseCookie.from(name, value)
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
