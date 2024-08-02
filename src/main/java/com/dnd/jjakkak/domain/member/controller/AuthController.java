package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.domain.member.jwt.provider.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * 현재 Member의 로그인 여부를 확인하는 컨트롤러입니다.
 *
 * @author 류태웅
 * @version 2024. 08. 02.
 */

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;

    /**
     * 로그아웃 시 프론트 측에서 보내는 access_token과 일치한 지 확인 후
     * 프론트엔드에게 로그인 또는 비로그인 상태의 메세지롤 보냄
     *
     * @param request        HttpServletRequest
     * @return message ResponseEntity
     */

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access_token")) {
                    String token = cookie.getValue();
                    if (!jwtProvider.validate(token).isEmpty()) {
                        return ResponseEntity.ok().body(Collections.singletonMap("isAuthenticated", true));
                    }
                }
            }
        }
        return ResponseEntity.ok().body(Collections.singletonMap("isAuthenticated", false));
    }
}

