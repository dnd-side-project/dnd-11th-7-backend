package com.dnd.jjakkak.domain.member.service;

import com.dnd.jjakkak.domain.member.entity.RefreshToken;
import com.dnd.jjakkak.domain.member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RefreshToken의 Service입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Refresh Token CRUD
     *
     * @param memberId long
     * @param token    String
     */

    @Transactional
    public void createRefreshToken(long memberId, String token) {
        RefreshToken refreshToken = new RefreshToken(token, memberId);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    /**
     * Refresh Token의 유효성을 검증하는 메소드
     *
     * @param token String
     * @return 유효하면 true, 그렇지 않으면 false
     */
    @Transactional(readOnly = true)
    public boolean validateRefreshToken(String token) {
        return refreshTokenRepository.existsByToken(token);
    }
}
