package com.dnd.jjakkak.global.annotation.redisson;

import java.lang.annotation.*;

/**
 * Redisson AOP 어노테이션입니다.
 * 적용하려는 메소드에 Transactional을 지우고 RedissonLock을 적용하면 됩니다.
 * ex) @RedissonLock(value = "#meetingUuid")
 *
 *
 * @author 류태웅
 * @version 2024. 10. 15.
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonLock {
    String value();
    long waitTime() default 5000L; // Lock 획득을 시도하는 최대 시간 (ms)
    long leaseTime() default 3000L; // 락을 획득한 후, 점유하는 최대 시간
}
