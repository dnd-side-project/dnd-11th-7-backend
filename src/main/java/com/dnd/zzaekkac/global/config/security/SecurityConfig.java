package com.dnd.zzaekkac.global.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Configuration Class.
 *
 * @author 정승조
 * @version 2024. 07. 17.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Security Bean 등록.
     *
     * <li>CSRF 비활성화</li>
     * <li>CORS 비활성화</li>
     * <li>Form Login 비활성화</li>
     * <li>모든 요청 허용 -> 추후에 변경 필요</li>
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.anyRequest().permitAll());

        return http.build();
    }

    /**
     * Security ignore 설정 빈 등록.
     *
     * <li>static Resource (favicon, ...)</li>
     * <p>추후에 제외해야될 파일들 추가</p>
     */
    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/favicon");
    }
}
