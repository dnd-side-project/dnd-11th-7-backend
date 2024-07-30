package com.dnd.jjakkak.global.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * 인증 실패 시 사용할 클래스.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */

public class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * <li>code와 message 추가</li>
     *
     * @param request       HttpServletRequest
     * @param response      HttpServletResponse
     * @param authException AuthenticationException
     * @throws IOException      IOException
     * @throws ServletException ServletException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("{\"code\": \"NP\", \"message\": \"No Permission\"}");
    }
}