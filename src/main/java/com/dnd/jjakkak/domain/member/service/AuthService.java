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
    public String reissueToken(String refreshToken) {

        String kakaoId = jwtProvider.validate(refreshToken);
        String accessToken = jwtProvider.createAccessToken(kakaoId);

        return "Bearer " + accessToken;
    }

    /**
     * AccessToken의 유효성을 검증하는 메소드
     *
     * @param authorization Authorization Header (Bearer Token)
     */
    public void checkAuth(String authorization) {

        String accessToken = authorization.substring(7);

        String validate = jwtProvider.validate(accessToken);

        if (Strings.isBlank(validate)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }
}
