package com.dnd.jjakkak.domain.refreshtoken.service;

import com.dnd.jjakkak.domain.redis.RedisRepository;
import com.dnd.jjakkak.global.config.proprties.TokenProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * RefreshToken Service 클래스입니다.
 *
 * @author 정승조, 류태웅
 * @version 2024. 09. 22.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final TokenProperties tokenProperties;
    private final RedisRepository redisRepository;

    /**
     * Kakao ID로 RT를 조회하는 메서드.
     *
     * @param kakaoId 회원의 카카오 ID
     * @return 저장되어있는 RT 값
     */
    public String findByKakaoId(String kakaoId) {
        return redisRepository.findByKey(kakaoId);
    }


    /**
     * Kakao ID로 RT를 저장하는 메서드.
     *
     * @param kakaoId      회원의 카카오 ID
     * @param refreshToken 저장할 RT 값
     */
    public void saveRefreshToken(String kakaoId, String refreshToken) {
        Duration expiration = Duration.ofDays(tokenProperties.getRefreshTokenExpirationDay());
        redisRepository.save(kakaoId, refreshToken, expiration);
    }


    /**
     * Kakao ID로 RT를 삭제하는 메서드.
     *
     * @param kakaoId 회원의 카카오 ID
     */
    public void deleteRefreshToken(String kakaoId) {
        redisRepository.delete(kakaoId);
    }
}
