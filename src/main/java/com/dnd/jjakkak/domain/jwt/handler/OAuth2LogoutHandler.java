package com.dnd.jjakkak.domain.jwt.handler;

import com.dnd.jjakkak.domain.member.service.BlacklistService;
import com.dnd.jjakkak.domain.member.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 로그아웃 시 토큰에 빈 값을 넣어 전송 하는 방식으로 토큰을 삭제하는 핸들러입니다.
 *
 * @author 류태웅
 * @version 2024. 08. 03.
 */
@Component
@RequiredArgsConstructor
public class OAuth2LogoutHandler implements LogoutHandler {

    private final RefreshTokenService refreshTokenService;
    private final BlacklistService blacklistService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String refreshToken = extractRefreshToken(request.getCookies());

        if (refreshToken == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (!(refreshTokenService.validateRefreshToken(refreshToken))) {
            // FIXME : 해당 메서드가 필요할까요?
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        refreshTokenService.deleteRefreshToken(refreshToken);

        // FIXME : 메서드 명 수정해야 할 것 같아요.
        blacklistService.blacklistToken(refreshToken, LocalDateTime.now().plusWeeks(1));
        response.setStatus(HttpServletResponse.SC_OK);
    }


    /**
     * 쿠키에서 refresh_token을 추출하는 메서드입니다.
     *
     * @param cookies 요청의 쿠키 배열
     * @return Refresh Token
     */
    public String extractRefreshToken(Cookie[] cookies) {

        return Arrays.stream(cookies)
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
