package com.dnd.jjakkak.domain.jwt.handler;

import com.dnd.jjakkak.domain.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import com.dnd.jjakkak.domain.member.service.BlacklistService;
import com.dnd.jjakkak.domain.member.service.TokenService;
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
    private final TokenService tokenService;
    private final JwtProvider jwtProvider;
    private static final Long EXPIRATION_WEEK = 1L;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.debug("logout 시작");

        String refreshToken = extractRefreshToken(request.getCookies());

        if (refreshToken == null) {
            log.debug("Refresh Token 쿠키가 없습니다.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Long kakaoId = Long.parseLong(jwtProvider.validateToken(refreshToken));
        if (!memberRepository.existsByKakaoId(kakaoId)) {
            log.debug("로그인한 회원 정보가 없습니다.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        tokenService.deleteRefreshToken(kakaoId.toString());

        log.debug("logout refreshToken: {}", refreshToken);
        // blacklistService.createBlacklistToken(refreshToken, LocalDateTime.now().plusWeeks(EXPIRATION_WEEK));
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
