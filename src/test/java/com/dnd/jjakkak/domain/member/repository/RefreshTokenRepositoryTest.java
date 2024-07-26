package com.dnd.jjakkak.domain.member.repository;

import com.dnd.jjakkak.domain.member.entity.RefreshToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * 리프레시 토큰 레포지토리 테스트 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */
@DataJpaTest
class RefreshTokenRepositoryTest {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("리프레시 토큰 저장 및 삭제 테스트")
    void testSaveAndDeleteRefreshToken() {
        // given
        RefreshToken token = new RefreshToken("test_token", 1L);
        refreshTokenRepository.save(token);

        // when
        boolean existsBeforeDelete = refreshTokenRepository.existsByToken("test_token");
        refreshTokenRepository.deleteByToken("test_token");
        boolean existsAfterDelete = refreshTokenRepository.existsByToken("test_token");

        // then
        Assertions.assertTrue(existsBeforeDelete);
        Assertions.assertFalse(existsAfterDelete);
    }
}