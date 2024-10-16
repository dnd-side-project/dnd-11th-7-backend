package com.dnd.jjakkak.global.annotation.redisson;

import com.dnd.jjakkak.global.util.CustomSpringELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Redisson Lock Aspect 클래스입니다.
 *
 * @author 류태웅, 정승조
 * @version 2024. 10. 15.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedissonLockAspect {
    private final RedissonClient redissonClient;
    private final TransactionAspect transactionAspect;

    /**
     * '@RedissonLock' 어노테이션을 사용한 메서드에 대한 Advice.
     */
    @Around("@annotation(com.dnd.jjakkak.global.annotation.redisson.RedissonLock)")
    public Object redissonLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedissonLock annotation = method.getAnnotation(RedissonLock.class);

        // todo: prefix 변경 필요 (현재는 모임 관련 로직에서만 사용)
        String lockKey = "meeting-" + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), annotation.key());

        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean lockable = lock.tryLock(annotation.waitTime(), annotation.leaseTime(), TimeUnit.MILLISECONDS);

            if (!lockable) {
                throw new IllegalArgumentException();
            }

            return transactionAspect.proceed(joinPoint);
        } catch (InterruptedException e) {
            // todo : 재시도 로직이 필요한가?
            log.error("Redisson Lock Exception", e);
            throw new RuntimeException();
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
