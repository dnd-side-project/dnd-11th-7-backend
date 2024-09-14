package com.dnd.jjakkak.domain.jwt.handler;

import com.dnd.jjakkak.domain.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
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
import java.util.Arrays;

/**
 * 로그아웃 시 토큰에 빈 값을 넣어 전송 하는 방식으로 토큰을 삭제하는 핸들러입니다.
 *
 * @author 류태웅, 정승조
 * @version 2024. 09. 13.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LogoutHandler implements LogoutHandler {

    private final MemberRepository memberRepository;
    private final BlacklistService blacklistService;
    private final JwtProvider jwtProvider;
    private static final Long EXPIRATION_WEEK = 1L;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.debug("logout 시작");

        String refreshToken = extractRefreshToken(request.getCookies());
        Long kakaoId = Long.parseLong(jwtProvider.validateToken(refreshToken));

        if (refreshToken == null || memberRepository.existsByKakaoId(kakaoId)) {
            log.debug("쿠키 인증 오류");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        log.debug("logout refreshToken: {}", refreshToken);
        blacklistService.blacklistToken(refreshToken, LocalDateTime.now().plusWeeks(EXPIRATION_WEEK));
        log.debug("logout 성공");
    }

    /**
     * 쿠키에서 Refresh Token을 추출합니다.
     *
     * @param cookies 모든 쿠키
     * @return Refresh Token
     */
    public String extractRefreshToken(Cookie[] cookies) {
        return Arrays.stream(cookies)
                .filter(cookie -> "refresh_tokne".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
