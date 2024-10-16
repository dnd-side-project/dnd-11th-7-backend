package com.dnd.jjakkak.global.annotation.redisson;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 트랜잭션 분리를 위한 TransactionAspect 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 10. 15.
 */

@Component
public class TransactionAspect {

    /**
     * 트랜잭션을 분리하기 위한 메서드 (다른 트랜잭션과 분리)
     *
     * <li> REQUIRES_NEW 를 통해 별도의 트랜잭션으로 관리한다.</li>
     * <li> timeout 시간 내에 수행이 되어야 한다. (아니면 롤백) </li>
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 2)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
