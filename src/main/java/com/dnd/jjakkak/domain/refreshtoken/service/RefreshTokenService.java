package com.dnd.jjakkak.domain.refreshtoken.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * RefreshToken Service 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 09. 22.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_EXPIRATION = 7; // fixme : properties 로 변경
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Kakao ID로 RT를 조회하는 메서드.
     *
     * @param kakaoId 회원의 카카오 ID
     * @return 저장되어있는 RT 값
     */
    public String findByKakaoId(String kakaoId) {
        return redisTemplate.opsForValue().get(kakaoId);
    }


    /**
     * Kakao ID로 RT를 저장하는 메서드.
     *
     * @param kakaoId      회원의 카카오 ID
     * @param refreshToken 저장할 RT 값
     */
    public void saveRefreshToken(String kakaoId, String refreshToken) {
        redisTemplate.opsForValue().set(kakaoId, refreshToken, Duration.ofDays(REFRESH_TOKEN_EXPIRATION));
    }


    /**
     * Kakao ID로 RT를 삭제하는 메서드.
     *
     * @param kakaoId 회원의 카카오 ID
     */
    public void deleteRefreshToken(String kakaoId) {
        redisTemplate.delete(kakaoId);
    }


}
