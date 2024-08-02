package com.dnd.jjakkak.domain.member.jwt.handler;

import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 검증 성공 시 토큰이 저장된 링크로 이동하는 핸들러입니다.
 * @author 류태웅
 * @version 2024. 08. 02.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Member oauth2User = (Member) authentication.getPrincipal();
        String kakaoId = Long.toString(oauth2User.getKakaoId());

        // Access Token 생성
        String accessToken = jwtProvider.createAccessToken(kakaoId);

        // Refresh Token 생성 및 저장
        String refreshToken = jwtProvider.createRefreshToken(kakaoId);
        refreshTokenService.createRefreshToken(oauth2User.getMemberId(), refreshToken);

        log.info("access token: " + accessToken);
        log.info("refresh token: " + refreshToken);

        // Refresh Token 쿠키 설정
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/"); // 모든 경로에서 접근 가능하도록 설정
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 1주일

        // 쿠키 추가
        response.addCookie(refreshTokenCookie);
        // Access Token을 헤더에 추가
        response.setHeader("Authorization", "Bearer " + accessToken);
        // 리다이렉트할 URL 설정 (프론트엔드 페이지로 리다이렉트)
        response.sendRedirect("http://localhost:3000/");
    }
}
