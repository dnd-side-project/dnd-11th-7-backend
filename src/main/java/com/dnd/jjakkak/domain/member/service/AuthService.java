package com.dnd.jjakkak.domain.member.service;

import com.dnd.jjakkak.domain.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

/**
 * 권한, 인증 관련 Service 클래스 입니다.
 *
 * @author 정승조
 * @version 2024. 08. 16.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;

    /**
     * RefreshToken을 이용하여 AccessToken 재발급
     *
     * @param refreshToken Refresh Token 값
     * @return 재발급된 AccessToken
     */
    public String reissueAccessToken(String refreshToken) {

        String kakaoId = jwtProvider.validateToken(refreshToken);
        String accessToken = jwtProvider.createAccessToken(kakaoId);

        return "Bearer " + accessToken;
    }

    public String refreshRefreshToken(String refreshToken) {
        String kakaoId = jwtProvider.validateToken(refreshToken);
        return jwtProvider.createRefreshToken(kakaoId);
    }

    /**
     * AccessToken의 유효성을 검증하는 메소드
     *
     * @param authorization Authorization Header (Bearer Token)
     */
    public boolean checkAuth(String authorization) {

        String accessToken = authorization.substring(7);
        String validate = jwtProvider.validateToken(accessToken);

        return Strings.isNotBlank(validate);
    }
}
