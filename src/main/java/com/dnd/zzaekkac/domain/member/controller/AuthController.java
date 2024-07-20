package com.dnd.zzaekkac.domain.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * 카카오 로그인 컨트롤러입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 20.
 */


@Controller
public class AuthController {

    /**
     * 로그인/가입 성공 시 메인으로 리다이렉트 -> 추후 수정 필요
     *
     * <li>OAuth2SuccessHandler를 통해 성공하여 GetMapping의 링크로 리다이렉트</li>
     * <li>해당 링크로 이동 시 메인 페이지로 이동</li>
     * <li>token과 만료시간(지금은 3600으로 잡음)을 사용할 수도 있을 것</li>
     */

    @GetMapping("/auth/oauth-response/{token}/{expiresIn}")
    public void oauthResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 메인 페이지로 리다이렉트
        // 지금은 임시 페이지로 함
        response.sendRedirect("http://localhost:8080/");
    }
}
