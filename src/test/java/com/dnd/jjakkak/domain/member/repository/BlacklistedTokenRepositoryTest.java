package com.dnd.jjakkak.domain.member.repository;

import com.dnd.jjakkak.domain.member.entity.BlacklistedToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 블랙리스트 토큰 레포지토리 테스트 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */
@DataJpaTest
class BlacklistedTokenRepositoryTest {

    @Autowired
    BlacklistedTokenRepository blacklistedTokenRepository;

    @Test
    @DisplayName("블랙리스트 토큰 저장 및 조회 테스트")
    void testSaveAndFindBlacklistedToken() {
        // given
        BlacklistedToken token = BlacklistedToken.builder()
                .token("test_token")
                .expirationDate(LocalDateTime.now().plusDays(1))
                .build();
        blacklistedTokenRepository.save(token);

        // when
        Optional<BlacklistedToken> actual = blacklistedTokenRepository.findByToken("test_token");

        // then
        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals("test_token", actual.get().getToken());
    }

    @Test
    @DisplayName("만료된 블랙리스트 토큰 삭제 테스트")
    void testDeleteExpiredTokens() {
        // given
        BlacklistedToken token = BlacklistedToken.builder()
                .token("expired_token")
                .expirationDate(LocalDateTime.now().minusDays(1))
                .build();
        blacklistedTokenRepository.save(token);

        // when
        blacklistedTokenRepository.deleteByExpirationDateBefore(LocalDateTime.now());

        // then
        Optional<BlacklistedToken> actual = blacklistedTokenRepository.findByToken("expired_token");
        Assertions.assertFalse(actual.isPresent());
    }
}