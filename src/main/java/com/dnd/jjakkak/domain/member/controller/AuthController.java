package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.domain.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.service.AuthService;
import com.dnd.jjakkak.domain.member.service.TokenService;
import com.dnd.jjakkak.global.util.CookieUtil;
import jakarta.servlet.http.Cookie;
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
    private final TokenService tokenService;
    private final JwtProvider jwtProvider;

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
     * @param response     HttpServletResponse
     * @return 200 (OK), Header - Authorization (Bearer Token), Refresh Token (Cookie)
     */

    @GetMapping("/reissue")
    public ResponseEntity<Void> reissueToken(@CookieValue(value = "refresh_token", required = false) String refreshToken,
                                             HttpServletResponse response) {

        if (Strings.isEmpty(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String kakaoId = jwtProvider.validateToken(refreshToken);

        String storedRefreshToken = tokenService.findRefreshToken(kakaoId);

        // 저장된 토큰과 일치하지 않으면, Redis에서 해당 KakaoId의 RT 삭제 및 재로그인 요청
        if (!refreshToken.equals(storedRefreshToken)) {
            tokenService.deleteRefreshToken(kakaoId);  // 기존 RT 삭제
            CookieUtil.deleteCookie(response, "refresh_token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String newAccessToken = authService.reissueAccessToken(refreshToken);
        response.setHeader("Authorization", newAccessToken);

        String newRefreshToken = authService.refreshRefreshToken(refreshToken);

        CookieUtil.createCookie(response, "refresh_token", newRefreshToken, 7 * 24 * 60 * 60);
        tokenService.saveRefreshToken(kakaoId, newRefreshToken);

        return ResponseEntity.ok().build();
    }
}
