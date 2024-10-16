package com.dnd.jjakkak.global.annotation.redisson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Redisson AOP 어노테이션입니다.
 *
 * <li>적용하려는 메소드에 Transactional을 지우고 RedissonLock을 적용하면 됩니다.</li>
 * <li>ex) @RedissonLock(value = "#meetingUuid")</li>
 *
 * @author 류태웅
 * @version 2024. 10. 15.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {

    /**
     * Lock Key
     */
    String key();

    /***
     * Lock 획득을 시도하는 최대 시간 (ms)
     */
    long waitTime() default 5000L;

    /**
     * 락을 획득한 후, 점유하는 최대 시간
     */
    long leaseTime() default 3000L;
}
