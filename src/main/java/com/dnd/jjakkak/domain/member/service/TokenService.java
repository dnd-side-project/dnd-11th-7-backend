package com.dnd.jjakkak.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RedisTemplate<String, String> redisTemplate;

    // kakaoId로 RT 조회
    public String findRefreshToken(String kakaoId) {
        return redisTemplate.opsForValue().get(kakaoId);
    }

    // RT 저장
    // todo : RT의 유효 기간을 jwtProvider에서 가져오거나 properties로 관리하고 싶다!
    public void saveRefreshToken(String kakaoId, String refreshToken) {
        redisTemplate.opsForValue().set(kakaoId, refreshToken, Duration.ofDays(7));
    }

    // RT 삭제
    public void deleteRefreshToken(String kakaoId) {
        redisTemplate.delete(kakaoId);
    }
}
