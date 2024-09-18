package com.dnd.jjakkak.domain.jwt.handler;

import com.dnd.jjakkak.global.config.proprties.JjakkakProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 검증 실패 시 예외 처리 링크로 이동하는 핸들러입니다.
 *
 * @author 류태웅
 * @version 2024. 09. 15.
 */
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final String QUERY_PARAM = "redirect";
    private final JjakkakProperties jjakkakProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

        request.getSession().removeAttribute(QUERY_PARAM);
        response.sendRedirect(jjakkakProperties.getFrontUrl() + "/login/failure");
    }
}
