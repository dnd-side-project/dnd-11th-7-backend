package com.dnd.jjakkak.domain.jwt.provider;

import org.springframework.http.ResponseCookie;

/**
 * JWT 관련 유틸 클래스.
 *
 * @author 정승조
 * @version 2024. 09. 21.
 */
public class JwtUtils {


    private JwtUtils() {
        throw new IllegalArgumentException("Utility class");
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

    /**
     * 쿠키를 삭제하는 메서드입니다.
     *
     * @param name 삭제할 쿠키 이름
     * @return 삭제된 쿠키
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
}
