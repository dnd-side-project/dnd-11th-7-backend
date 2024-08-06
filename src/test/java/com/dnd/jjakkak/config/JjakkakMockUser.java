package com.dnd.jjakkak.config;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * MockUser 어노테이션입니다.
 *
 * @author 정승조
 * @version 2024. 08. 05.
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = JjakkakMockSecurityContext.class)
public @interface JjakkakMockUser {

    String nickname() default "seungjo";

    long kakaoId() default 1234567890;
}
