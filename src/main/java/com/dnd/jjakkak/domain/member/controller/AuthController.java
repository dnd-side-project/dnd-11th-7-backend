package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.domain.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 현재 Member의 로그인 여부를 확인하는 컨트롤러입니다.
 *
 * @author 류태웅
 * @version 2024. 08. 05.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;

    /**
     * Authorization Header 확인 후 로그인 여부를 확인하는 메서드.
     *
     * @param authorization Authorization Header (Bearer Token)
     * @return (로그인 여부)
     */
    @GetMapping("/check-auth")
    public ResponseEntity<Map<String, Boolean>> checkAuth(@RequestHeader(value = "Authorization", required = false) String authorization) {

        Map<String, Boolean> response = new ConcurrentHashMap<>();

        log.debug(authorization);

        String subject = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            subject = jwtProvider.validate(authorization.substring(7));
        }

        if (Strings.isEmpty(subject)) {
            log.debug("로그인이 되어있지 않습니다.");
            response.put("isAuthenticated", false);
            return ResponseEntity.ok(response);
        }

        log.debug("로그인이 되어있습니다.");
        response.put("isAuthenticated", true);
        return ResponseEntity.ok(response);
    }
}
