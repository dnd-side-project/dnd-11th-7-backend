package com.dnd.jjakkak.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void deleteCookie(HttpServletResponse response, String name){
        Cookie deleteOldRefreshTokenCookie = new Cookie(name, null);
        deleteOldRefreshTokenCookie.setMaxAge(0);
        deleteOldRefreshTokenCookie.setPath("/");
        response.addCookie(deleteOldRefreshTokenCookie);
    }

    public static void createCookie(HttpServletResponse response, String name, String value, int time){
        Cookie newRefreshTokenCookie = new Cookie(name, value);
        newRefreshTokenCookie.setHttpOnly(true);
        newRefreshTokenCookie.setSecure(true);
        newRefreshTokenCookie.setPath("/");
        newRefreshTokenCookie.setMaxAge(time);
        response.addCookie(newRefreshTokenCookie);
    }
}
