package com.dnd.jjakkak.domain.member.service;

import com.dnd.jjakkak.domain.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.jwt.provider.JwtUtils;
import com.dnd.jjakkak.domain.member.dto.response.ReissueResponseDto;
import com.dnd.jjakkak.domain.member.exception.UnauthorizedException;
import com.dnd.jjakkak.domain.refreshtoken.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 권한, 인증 관련 Service 클래스 입니다.
 *
 * @author 정승조
 * @version 2024. 08. 16.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private static final String TEST_REFRESH_TOKEN_VALUE = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzNjU0ODMxNTk4IiwiaWF0IjoxNzI2OTgwNzEwLCJleHAiOjE3Mjc1ODU1MTB9.KfELuCbYwPGUxVEUfrEQpFN2Z_7ND_nUGYMwojPtSzo";


    /**
     * RefreshToken을 이용하여 AccessToken 재발급
     *
     * @param refreshToken Refresh Token 값
     * @return 재발급된 AccessToken
     */
    @Transactional
    public ReissueResponseDto reissueToken(String refreshToken) {

        refreshToken = TEST_REFRESH_TOKEN_VALUE;

        log.info("Refresh Token: {}", refreshToken);
        String kakaoId = jwtProvider.validateToken(refreshToken);
        String existingToken = refreshTokenService.findExistingToken(Long.parseLong(kakaoId));
        log.info("Existing Token: {}", existingToken);

        log.info("is equal ? = {}", existingToken.equals(refreshToken));

        // 저장되어있는 토근과 요청받은 토큰이 일치하지 않으면 예외처리
        if (!existingToken.equals(refreshToken)) {
            refreshTokenService.deleteRefreshToken(Long.parseLong(kakaoId));
            throw new UnauthorizedException();
        }

        // AccessToken을 재발급한다.
        String accessToken = jwtProvider.createAccessToken(kakaoId);

        // 새로운 RefreshToken을 발급한다.
        String newRefreshToken = jwtProvider.createRefreshToken(kakaoId);

        // 새로운 RefreshToken을 DB에 저장한다.
        refreshTokenService.saveRefreshToken(Long.parseLong(kakaoId), TEST_REFRESH_TOKEN_VALUE);

        ResponseCookie refreshCookie = JwtUtils.createCookie("refresh_token", TEST_REFRESH_TOKEN_VALUE, 60 * 60 * 24 * 7);

        return new ReissueResponseDto(accessToken, refreshCookie);
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
