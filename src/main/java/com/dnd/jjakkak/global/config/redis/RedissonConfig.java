package com.dnd.jjakkak.global.config.redis;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 설정 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 10. 15.
 */
@Configuration
@RequiredArgsConstructor
public class RedissonConfig {

    private static final String REDISSON_HOST_PREFIX = "redis://";
    private final RedisProperties redisProperties;

    /**
     * Redisson Client 등록 메서드.
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + redisProperties.getHost() + ":" + redisProperties.getPort());
        return Redisson.create(config);
    }
}
