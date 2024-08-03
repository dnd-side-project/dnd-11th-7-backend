package com.dnd.jjakkak.domain.member.jwt.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 검증 실패 시 예외 처리 링크로 이동하는 핸들러입니다.
 *
 * @author 류태웅
 * @version 2024. 08. 02.
 */

@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        String errorMessage;
        if(e instanceof UsernameNotFoundException){
            errorMessage="존재하지 않는 아이디 입니다.";
        }
        else{
            errorMessage="알 수 없는 이유로 로그인이 안되고 있습니다.";
        }

        errorMessage= URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);//한글 인코딩 깨지는 문제 방지
        response.sendRedirect("http://localhost:8080/auth/oauth-response/error="+errorMessage);
    }
}
