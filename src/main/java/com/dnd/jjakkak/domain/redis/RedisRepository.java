package com.dnd.jjakkak.domain.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * Redis 접근하는 Repository 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 10. 11.
 */
@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public String findByKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void save(String key, String value, Duration expiration) {
        redisTemplate.opsForValue().set(key, value, expiration);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public Boolean lock(String key) {
        return redisTemplate.opsForValue()
                .setIfAbsent(key, "lock", Duration.ofSeconds(5));
    }
}
