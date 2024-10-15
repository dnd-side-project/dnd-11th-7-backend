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
 * Redisson AOP Aspect입니다.
 *
 * @author 류태웅
 * @version 2024. 10. 15.
 */

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedissonLockAspect {
    private final RedissonClient redissonClient;
    private final TransactionAspect transactionAspect;

    @Around("@annotation(RedissonLock)")
    public Object redissonLock(ProceedingJoinPoint joinPoint) throws Throwable{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedissonLock annotation = method.getAnnotation(RedissonLock.class);
        String lockKey = method.getName() + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), annotation.value());

        RLock lock = redissonClient.getLock(lockKey);

        try{
            boolean lockable = lock.tryLock(annotation.waitTime(), annotation.leaseTime(), TimeUnit.MILLISECONDS);
            if(!lockable){
                log.info("{} : 락 획득 실패", lockKey);
                throw new IllegalArgumentException();
            }
            log.info("{} : 로직 수행", lockKey);
            return transactionAspect.proceed(joinPoint);
        } catch (InterruptedException e){
            log.error("{} : 락 에러 발생", lockKey);
            throw e;
        } finally {
            log.info("{} : 락 해제", lockKey);
            // lock 객체가 제대로 생성되었을 경우에만 unlock
            if(lock != null && lock.isHeldByCurrentThread()) lock.unlock();
        }
    }
}
