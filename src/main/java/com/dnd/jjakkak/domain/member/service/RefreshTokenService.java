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
 * @version 2024. 07. 24.
 */

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Refresh Token CRUD
     *
     * @param memberId long
     * @param token String
     * @return RefreshToken
     */

    @Transactional
    public RefreshToken createRefreshToken(long memberId, String token) {
        RefreshToken refreshToken = new RefreshToken(token, memberId);
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
