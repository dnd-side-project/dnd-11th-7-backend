package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.domain.member.dto.response.ReissueResponseDto;
import com.dnd.jjakkak.domain.member.exception.UnauthorizedException;
import com.dnd.jjakkak.domain.member.service.AuthService;
import com.dnd.jjakkak.global.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 로그인 검증 및 토큰 재발급 담당 컨트롤러입니다.
 *
 * @author 류태웅, 정승조
 * @version 2024. 08. 05.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authorization Header 확인 후 로그인 여부를 확인하는 메서드.
     *
     * @param authorization Authorization Header (Bearer Token)
     * @return (로그인 여부)
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkAuth(@RequestHeader(value = "Authorization", required = false) String authorization) {

        Map<String, Boolean> response = new ConcurrentHashMap<>();

        boolean isAuthenticated = Strings.isNotBlank(authorization) &&
                authorization.startsWith("Bearer ") &&
                authService.checkAuth(authorization);

        response.put("isAuthenticated", isAuthenticated);
        return ResponseEntity.ok(response);
    }

    /**
     * RT로 AT, RT 재발급, 새로 만들어진 RT는 쿠키로 전달 및 Redis에 새로 저장
     * 이때 RT가 유효하지 않거나 Redis에 맞지 않을 경우 401
     *
     * @param refreshToken Refresh Token (Cookie)
     * @return 200 (OK), Header - Authorization (Bearer Token), Refresh Token (Cookie)
     */

    @GetMapping("/reissue")
    public ResponseEntity<Void> reissueToken(@CookieValue(value = "refresh_token", required = false) String refreshToken) {

        if (Strings.isEmpty(refreshToken)) {
            throw new UnauthorizedException();
        }

        ReissueResponseDto reissuedToken = authService.reissueToken(refreshToken);

        ResponseCookie refreshCookie = CookieUtils.createCookie(
                "refresh_token",
                reissuedToken.getRefreshToken(),
                60 * 60 * 24 * 7);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, reissuedToken.getAccessToken())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .build();
    }
}
