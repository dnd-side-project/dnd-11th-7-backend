package com.dnd.jjakkak.domain.jwt.handler;

import com.dnd.jjakkak.global.config.proprties.JjakkakProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SignatureException;

/**
 * 검증 실패 시 예외 처리 링크로 이동하는 핸들러입니다.
 *
 * @author 류태웅
 * @version 2024. 09. 15.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final JjakkakProperties jjakkakProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        // 오류 메시지 없이 로그인 실패 페이지로 리다이렉션
        response.sendRedirect(jjakkakProperties.getFrontUrl() + "/login/failure");
    }
}
