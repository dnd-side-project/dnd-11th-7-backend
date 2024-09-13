package com.dnd.jjakkak.domain.jwt.handler;

import com.dnd.jjakkak.domain.member.service.BlacklistService;
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
 * @author 류태웅
 * @version 2024. 09. 13.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LogoutHandler implements LogoutHandler {
    private final BlacklistService blacklistService;

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

        log.debug("logout 시작");
        // todo : 새로운 refresh_token 검증 방법 필요
        if (refreshToken != null) {
            log.debug("logout refreshToken: {}", refreshToken);
            LocalDateTime expirationDate = LocalDateTime.now().plusWeeks(1);
            blacklistService.blacklistToken(refreshToken, expirationDate);
            log.debug("logout 성공");
        } else {
            log.debug("쿠키 인증 오류");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
