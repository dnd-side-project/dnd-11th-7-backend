package com.dnd.jjakkak.domain.member.jwt.handler;

import com.dnd.jjakkak.domain.member.service.BlacklistService;
import com.dnd.jjakkak.domain.member.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 로그아웃 시 토큰에 빈 값을 넣어 전송 하는 방식으로 토큰을 삭제하는 핸들러입니다.
 *
 * @author 류태웅
 * @version 2024. 08. 02.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LogoutHandler implements LogoutHandler {
    private final RefreshTokenService refreshTokenService;
    private final BlacklistService blacklistService;

    /**
     * 로그아웃 시 프론트 측에서 보내는 refresh_token과 일치한 지 확인 후
     * 프론트엔드에게 빈 값의 쿠키를 보냄으로써 쿠키 삭제 (프론트에서도 쿠키 삭제 필요)
     *
     * @param request        HttpServletRequest
     * @param response       HttpServletResponse
     * @param authentication Authentication
     */

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        log.info("logout 시작");

        if (refreshToken != null) {
            log.info("logout refreshToken: {}", refreshToken);

            if (refreshTokenService.validateRefreshToken(refreshToken)) {
                try {
                    refreshTokenService.deleteRefreshToken(refreshToken);
                    LocalDateTime expirationDate = LocalDateTime.now().plusWeeks(1);
                    blacklistService.blacklistToken(refreshToken, expirationDate);
                    log.info("logout 성공");

                    // 쿠키 삭제
                    Cookie accessTokenCookie = new Cookie("access_token", null);
                    accessTokenCookie.setPath("/");
                    accessTokenCookie.setHttpOnly(true);
                    accessTokenCookie.setSecure(true);
                    accessTokenCookie.setMaxAge(0);

                    Cookie refreshTokenCookie = new Cookie("refresh_token", null);
                    refreshTokenCookie.setPath("/");
                    refreshTokenCookie.setHttpOnly(true);
                    refreshTokenCookie.setSecure(true);
                    refreshTokenCookie.setMaxAge(0);

                    response.addCookie(accessTokenCookie);
                    response.addCookie(refreshTokenCookie);

                    response.setStatus(HttpServletResponse.SC_OK);
                } catch (Exception e) {
                    log.error("서버 에러", e);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                log.error("Refresh Token 인증 오류");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            log.error("헤더 인증 오류");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
