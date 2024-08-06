package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.domain.jwt.provider.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * 현재 Member의 로그인 여부를 확인하는 컨트롤러입니다.
 *
 * @author 류태웅
 * @version 2024. 08. 02.
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;

    /**
     * 프론트 측에서 보내는 access_token을 확인 후
     * 프론트엔드에게 로그인 또는 비로그인 상태의 메시지를 보냄
     *
     * @param request        HttpServletRequest
     * @return message ResponseEntity
     */

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        log.info(authorizationHeader);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String subject = jwtProvider.validate(token);

            if (subject != null && !subject.isEmpty()) {
                log.info("isAuthenticated");
                return ResponseEntity.ok().body(Collections.singletonMap("isAuthenticated", true));
            }
        }
        log.info("isNotAuthenticated");
        return ResponseEntity.ok().body(Collections.singletonMap("isAuthenticated", false));
    }
}
