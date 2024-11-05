package com.dnd.jjakkak.domain.member.service;

import com.dnd.jjakkak.domain.jwt.exception.TokenExpiredException;
import com.dnd.jjakkak.domain.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.dto.response.ReissueResponseDto;
import com.dnd.jjakkak.domain.member.exception.UnauthorizedException;
import com.dnd.jjakkak.domain.refreshtoken.service.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
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
    private final RefreshTokenService refreshTokenService;

    /**
     * RT를 통해 모든 토큰을 재발급합니다.
     *
     * @param refreshToken Refresh Token (Cookie)
     * @return 재발급된 토큰 정보를 담은 DTO
     */
    public ReissueResponseDto reissueToken(String refreshToken) {

        String kakaoId;
        try {
            kakaoId = jwtProvider.validateToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("refreshError");
        }

        String existsToken = refreshTokenService.findByKakaoId(kakaoId);

        if (!refreshToken.equals(existsToken)) {
            refreshTokenService.deleteRefreshToken(kakaoId);
            throw new UnauthorizedException();
        }

        String newAccessToken = jwtProvider.createAccessToken(kakaoId);
        String newRefreshToken = jwtProvider.createRefreshToken(kakaoId);

        refreshTokenService.saveRefreshToken(kakaoId, newRefreshToken);

        return new ReissueResponseDto("Bearer " + newAccessToken, newRefreshToken);
    }

    /**
     * AccessToken의 유효성을 검증하는 메소드
     *
     * @param authorization Authorization Header (Bearer Token)
     */
    public boolean checkAuth(String authorization) {

        String accessToken = authorization.substring(7);
        String validate;
        try {
            validate = jwtProvider.validateToken(accessToken);
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("accessError");
        }

        return Strings.isNotBlank(validate);
    }
}
