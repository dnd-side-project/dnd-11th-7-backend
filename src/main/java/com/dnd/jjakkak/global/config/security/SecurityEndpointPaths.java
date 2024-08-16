package com.dnd.jjakkak.global.config.security;

/**
 * 권한에 따른 Endpoint 경로를 관리하는 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 16.
 */
public class SecurityEndpointPaths {

    public static final String[] WHITE_LIST = {
            "/api/v1/auth/oauth/**",
            "/api/v1/auth/**",
            "/api/v1/meeting/**",
            "/test/login"
    };
    public static final String[] USER_LIST = {
            "/api/v1/categories",
            "/api/v1/member/**"
    };
    public static final String[] ADMIN_LIST = {

    };

    private SecurityEndpointPaths() {
        // 인스턴스 생성 방지
    }
}
