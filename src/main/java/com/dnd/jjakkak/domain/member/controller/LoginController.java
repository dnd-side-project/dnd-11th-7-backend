package com.dnd.jjakkak.domain.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 카카오 로그인 테스트 컨트롤러입니다.
 *
 * @author 정승조
 * @version 2024. 08. 14.
 */

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    @GetMapping("/test/login")
    public String login() {
        return "login";
    }
}
