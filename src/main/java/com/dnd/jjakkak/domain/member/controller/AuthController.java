package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.domain.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.service.AuthService;
import com.dnd.jjakkak.domain.member.service.BlacklistService;
import com.dnd.jjakkak.domain.member.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    private final TokenService tokenService;
    private final JwtProvider jwtProvider;

    private static final String TEST_REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzNjMwMzM4NjM4IiwiaWF0IjoxNzI2OTA5MjE0LCJleHAiOjE3Mjc1MTQwMTR9.0cL9xh05NJt7WzXgSF85iEDzK3TGXcAXKW-N1qXXkOM";

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

//        if (Strings.isEmpty(refreshToken) || blacklistService.isTokenBlacklisted(refreshToken)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }

        if (Strings.isEmpty(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String kakaoId = jwtProvider.validateToken(refreshToken);

        String storedRefreshToken = tokenService.findRefreshToken(kakaoId);

        // 저장된 토큰과 일치하지 않으면, Redis에서 해당 KakaoId의 RT 삭제 및 재로그인 요청
        if (!refreshToken.equals(storedRefreshToken)) {
            tokenService.deleteRefreshToken(kakaoId);  // 기존 RT 삭제
            deleteCookie(response);
            // todo : 재로그인 리다이렉트
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String newAccessToken = authService.reissueAccessToken(refreshToken);
        response.setHeader("Authorization", newAccessToken);

//        String newRefreshToken = authService.refreshRefreshToken(refreshToken);
        String newRefreshToken = TEST_REFRESH_TOKEN;

        // todo : 여기도 기한을!
        // blacklistService.createBlacklistToken(refreshToken, LocalDateTime.now().plusWeeks(1L));

        createCookie(response, newRefreshToken);
        tokenService.saveRefreshToken(kakaoId, newRefreshToken);

        return ResponseEntity.ok().build();
    }

    // todo : 쿠키 유틸을 만드는 것은 어떨까?

    private void deleteCookie(HttpServletResponse response){
        Cookie deleteOldRefreshTokenCookie = new Cookie("refresh_token", null);
        deleteOldRefreshTokenCookie.setMaxAge(0);
        deleteOldRefreshTokenCookie.setPath("/");
        response.addCookie(deleteOldRefreshTokenCookie);
    }

    private void createCookie(HttpServletResponse response, String newRefreshToken){
        Cookie newRefreshTokenCookie = new Cookie("refresh_token", newRefreshToken);
        newRefreshTokenCookie.setHttpOnly(true);
        newRefreshTokenCookie.setSecure(true);
        newRefreshTokenCookie.setPath("/");
        newRefreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(newRefreshTokenCookie);
    }
}
