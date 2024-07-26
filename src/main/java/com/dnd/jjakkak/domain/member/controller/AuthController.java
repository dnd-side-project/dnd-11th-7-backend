package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.domain.member.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 로그아웃 시 사용하는 컨트롤러입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 24.
 */

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final RefreshTokenService refreshTokenService;

    /**
     * 로그아웃을 할 때 사용합니다.
     * 헤더에 Authorization : Bearer {refresh_token}을 입력한 다음 호출
     * 그렇게 되면 DB에 refresh_token이 삭제됨
     *
     * @param refreshToken      String
     */

    @PostMapping("/api/v1/logout")
    public void logout(@RequestHeader("Authorization") String refreshToken) {
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
            refreshTokenService.deleteRefreshToken(refreshToken);
        }
    }
}
