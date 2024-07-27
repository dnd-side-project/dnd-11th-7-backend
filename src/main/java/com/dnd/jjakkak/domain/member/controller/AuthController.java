package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.domain.member.service.BlacklistService;
import com.dnd.jjakkak.domain.member.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 로그아웃 시 사용하는 컨트롤러입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final RefreshTokenService refreshTokenService;
    private final BlacklistService blacklistService;

    /**
     * 로그아웃을 할 때 사용합니다.
     * 헤더에 Authorization : Bearer {refresh_token}을 입력한 다음 호출
     * 그렇게 되면 DB에 refresh_token이 삭제되고 블랙리스트에 추가됨
     *
     * @param refreshToken      String
     * @return ResponseEntity<String>
     */

    @PostMapping("/api/v1/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String refreshToken) {
        log.info("logout 시작");
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
            log.info("logout refreshToken: {}", refreshToken);
            if (refreshTokenService.validateRefreshToken(refreshToken)) {
                try {
                    refreshTokenService.deleteRefreshToken(refreshToken);
                    LocalDateTime expirationDate = LocalDateTime.now().plusWeeks(1); // 토큰의 실제 만료 시간으로 설정
                    blacklistService.blacklistToken(refreshToken, expirationDate);
                    log.info("logout 성공");
                    return ResponseEntity.ok("Logout successful");
                } catch (Exception e) {
                    log.error("서버 에러", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error");
                }
            } else {
                log.error("Refresh Token 인증 오류");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
            }
        }
        log.error("헤더 인증 오류");
        return ResponseEntity.badRequest().body("Invalid Header");
    }
}