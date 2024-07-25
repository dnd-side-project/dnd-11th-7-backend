package com.dnd.jjakkak.domain.member.jwt.handler;

import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.jwt.provider.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 검증 성공 시 토큰과 만료시간이 저장된 링크로 이동하는 핸들러입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 20.
 */

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    /**
     * 해당 메소드로 성공 시 링크로 이동합니다.
     * 여기서 jwtProvider의 create를 수행합니다.
     *
     * @param request        HttpServletRequest
     * @param response       HttpServletResponse
     * @param authentication Authentication
     * @throws IOException      IOException
     * @throws ServletException ServletException
     */

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Member oauth2User = (Member) authentication.getPrincipal();
        String kakaoId = Long.toString(oauth2User.getKakaoId());
        String token = jwtProvider.create(kakaoId);
        response.sendRedirect("http://localhost:8080/auth/oauth-response/" + token + "/3600");
    }
}