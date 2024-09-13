package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.domain.member.service.AuthService;
import com.dnd.jjakkak.domain.member.service.BlacklistService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
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
    private final BlacklistService blacklistService;

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
     * Refresh Token을 이용하여 Access Token 재발급하는 메서드.
     * 이 때 Refresh Token이 이미 블랙리스트라면 예외처리
     *
     * @param refreshToken Refresh Token (Cookie)
     * @param response     HttpServletResponse
     * @return 200 (OK), Header - Authorization (Bearer Token)
     */
    @GetMapping("/reissue")
    public ResponseEntity<Void> reissueToken(@CookieValue(value = "refresh_token", required = false) String refreshToken,
                                             HttpServletResponse response) {

        if (Strings.isEmpty(refreshToken) || blacklistService.isTokenBlacklisted(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String newAccessToken = authService.reissueToken(refreshToken);
        response.setHeader("Authorization", newAccessToken);
        return ResponseEntity.ok().build();
    }
}
