package com.dnd.jjakkak.global.annotation.redisson;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * AOP에서 트랜잭션 분리를 위한 TransactionAspect 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 10. 15.
 */

@Component
public class TransactionAspect {
    // leaseTime 보다 트랜잭션 타임아웃을 작게 설정
    // leaseTimeOut 발생 전에 rollback 시키기 위함
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 2)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
