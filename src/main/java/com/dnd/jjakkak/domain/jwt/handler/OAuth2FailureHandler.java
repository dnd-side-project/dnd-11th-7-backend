package com.dnd.jjakkak.domain.jwt.handler;

import com.dnd.jjakkak.global.config.proprties.JjakkakProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;

/**
 * 검증 실패 시 예외 처리 링크로 이동하는 핸들러입니다.
 *
 * @author 류태웅
 * @version 2024. 09. 15.
 */

@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final JjakkakProperties jjakkakProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        String errorMessage;

        Throwable cause = e.getCause();
        // Refresh Token 및 Access Token 관련 예외 처리
        if (e instanceof OAuth2AuthenticationException) {
            errorMessage = "OAuth2 인증 과정에서 문제가 발생했습니다.";
        } else if (e instanceof InsufficientAuthenticationException) {
            errorMessage = "인증 정보가 충분하지 않습니다.";
        } else if (cause instanceof ExpiredJwtException) {
            errorMessage = "토큰이 만료되었습니다. 다시 로그인해 주세요.";
        } else if (cause instanceof JwtException) {
            errorMessage = "JWT 토큰 처리 중 오류가 발생했습니다.";
        } else if (e instanceof CookieTheftException) {
            errorMessage = "쿠키 탈취가 감지되었습니다. 보안을 위해 다시 로그인해 주세요.";
        } else if (cause instanceof SignatureException) {
            errorMessage = "토큰 서명이 유효하지 않습니다.";
        } else {
            errorMessage = "알 수 없는 이유로 로그인이 안되고 있습니다.";
        }

        errorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);//한글 인코딩 깨지는 문제 방지
        response.sendRedirect(jjakkakProperties.getFrontUrl()+"/login/failure?error=" + errorMessage);
    }
}
