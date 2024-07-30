package com.dnd.jjakkak.domain.member.service;

import com.dnd.jjakkak.domain.member.entity.BlacklistedToken;
import com.dnd.jjakkak.domain.member.repository.BlacklistedTokenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 블랙리스트 서비스 테스트 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */
@ExtendWith(MockitoExtension.class)
class BlacklistServiceTest {

    @InjectMocks
    BlacklistService blacklistService;

    @Mock
    BlacklistedTokenRepository blacklistedTokenRepository;

    @Test
    @DisplayName("블랙리스트 토큰 조회 테스트")
    void testIsTokenBlacklisted() {
        // given
        String token = "test_token";
        BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                        .token(token).build();
        Mockito.when(blacklistedTokenRepository.findByToken(token))
                .thenReturn(Optional.of(blacklistedToken));

        // when
        boolean actual = blacklistService.isTokenBlacklisted(token);

        // then
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("블랙리스트 토큰 추가 테스트")
    void testBlacklistToken() {
        // given
        String token = "test_token";
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);
        BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                .token(token)
                .expirationDate(expirationDate)
                .build();

        // when
        blacklistService.blacklistToken(token, expirationDate);

        // then
        Mockito.verify(blacklistedTokenRepository, Mockito.times(1)).save(blacklistedToken);
    }

    @Test
    @DisplayName("만료된 블랙리스트 토큰 삭제 테스트")
    void testRemoveExpiredTokens() {
        // when
        blacklistService.removeExpiredTokens();

        // then
        Mockito.verify(blacklistedTokenRepository, Mockito.times(1))
                .deleteByExpirationDateBefore(LocalDateTime.now());
    }
}