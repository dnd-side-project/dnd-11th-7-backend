package com.dnd.jjakkak.domain.jwt.handler;

import com.dnd.jjakkak.domain.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.global.config.proprties.JjakkakProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth 로그인 성공시 JWT 토큰(AT, RT)을 생성하고 쿠키에 저장합니다.
 *
 * @author 류태웅, 정승조
 * @version 2024. 09. 13.
 */
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JjakkakProperties jjakkakProperties;
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        Member oauth2User = (Member) authentication.getPrincipal();

        String kakaoId = Long.toString(oauth2User.getKakaoId());
        String refreshToken = jwtProvider.createRefreshToken(kakaoId);

        ResponseCookie refreshCookie = createCookie("refresh_token", refreshToken, 60 * 60 * 24 * 7);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        String redirectUrl = getRedirectUrl(request);
        response.sendRedirect(redirectUrl);
    }

    /**
     * 리다이렉트 URL 을 반환하는 메서드입니다.
     *
     * @param request HttpServletRequest
     * @return 리다이렉트 URL
     */
    public String getRedirectUrl(HttpServletRequest request) {
        String baseUrl = jjakkakProperties.getFrontUrl() + "/login/success";

        HttpSession session = request.getSession();
        String redirectParam = (String) session.getAttribute("redirect");
        session.removeAttribute("redirect");

        return (redirectParam != null)
                ? baseUrl + "?redirect=" + redirectParam
                : baseUrl;
    }

    /**
     * 쿠키를 생성하는 메서드입니다.
     *
     * @param name   쿠키 이름
     * @param value  쿠키 값
     * @param maxAge 쿠키 만료 시간
     * @return 생성된 쿠키
     */
    private ResponseCookie createCookie(String name, String value, int maxAge) {

        return ResponseCookie.from(name, value)
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
