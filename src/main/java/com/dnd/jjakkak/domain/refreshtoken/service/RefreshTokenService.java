package com.dnd.jjakkak.domain.refreshtoken.service;

import com.dnd.jjakkak.domain.member.exception.UnauthorizedException;
import com.dnd.jjakkak.domain.refreshtoken.entity.RefreshToken;
import com.dnd.jjakkak.domain.refreshtoken.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Refresh Token 서비스 클래스.
 *
 * @author 정승조
 * @version 2024. 09. 21.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Refresh Token을 DB에 저장합니다.
     *
     * @param kakaoId      회원 ID (카카오 ID)
     * @param refreshToken Refresh Token 값
     */
    @Transactional
    public void saveRefreshToken(Long kakaoId, String refreshToken) {

        if (refreshTokenRepository.existsById(kakaoId)) {
            refreshTokenRepository.deleteById(kakaoId);
        }

        RefreshToken token = RefreshToken.builder()
                .id(kakaoId)
                .token(refreshToken)
                .build();

        refreshTokenRepository.save(token);
    }

    @Transactional(readOnly = true)
    public String findExistingToken(Long kakaoId) {
        RefreshToken refreshToken = refreshTokenRepository.findById(kakaoId)
                .orElseThrow(UnauthorizedException::new);

        return refreshToken.getToken();
    }

    /**
     * Refresh Token을 DB에서 삭제합니다.
     *
     * @param kakaoId 회원 ID (카카오 ID)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteRefreshToken(Long kakaoId) {
        refreshTokenRepository.deleteById(kakaoId);

        log.info("Refresh token exists? = {}", refreshTokenRepository.existsById(kakaoId));
    }
}
