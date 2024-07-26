package com.dnd.jjakkak.domain.member.service;

import com.dnd.jjakkak.domain.member.entity.RefreshToken;
import com.dnd.jjakkak.domain.member.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RefreshTokenService 테스트 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */
@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @InjectMocks
    RefreshTokenService refreshTokenService;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("리프레시 토큰 생성 테스트")
    void testCreateRefreshToken() {
        // given
        long memberId = 1L;
        String token = "test_token";
        RefreshToken refreshToken = new RefreshToken(token, memberId);

        // when
        refreshTokenService.createRefreshToken(memberId, token);

        // then
        Mockito.verify(refreshTokenRepository, Mockito.times(1)).save(refreshToken);
    }

    @Test
    @DisplayName("리프레시 토큰 삭제 테스트")
    void testDeleteRefreshToken() {
        // given
        String token = "test_token";

        // when
        refreshTokenService.deleteRefreshToken(token);

        // then
        Mockito.verify(refreshTokenRepository, Mockito.times(1)).deleteByToken(token);
    }

    @Test
    @DisplayName("리프레시 토큰 유효성 검증 테스트")
    void testValidateRefreshToken() {
        // given
        String token = "test_token";
        Mockito.when(refreshTokenRepository.existsByToken(token)).thenReturn(true);

        // when
        boolean actual = refreshTokenService.validateRefreshToken(token);

        // then
        Assertions.assertTrue(actual);
    }
}