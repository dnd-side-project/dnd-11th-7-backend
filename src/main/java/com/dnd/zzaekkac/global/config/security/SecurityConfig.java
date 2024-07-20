package com.dnd.zzaekkac.global.config.security;

import com.dnd.zzaekkac.domain.member.jwt.filter.JwtAuthenticationFilter;
import com.dnd.zzaekkac.domain.member.jwt.handler.OAuth2SuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;

/**
 * Spring Security Configuration Class.
 *
 * @author 류태웅
 * @version 2024. 07. 20.
 */

@Configurable
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final DefaultOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    /**
     * Security Bean 등록.
     *
     * <li>CSRF 비활성화</li>
     * <li>CORS 비활성화 -> corsConfigurationSource()로 설정</li>
     * <li>Form Login 비활성화</li>
     * <li>모든 요청 허용 -> 추후에 변경 필요 -> USER와 ADMIN에 따라 결정해야 할 듯? (류태웅)</li>
     *
     * <li>httpBasic 비활성화</li>
     * <li>세션 비활성화</li>
     * <li>OAuth2 로그인 설정 : 로그인 url, 리다이렉트 url, 유저 서비스, 성공 시 핸들러</li>
     * <li>인증 실패 시 메시지 임시 설정 -> 추후 변경 필요</li>
     * <li>필터 체인에 JWT Authentication Filter 추가</li>
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.anyRequest().permitAll())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint.baseUri("/api/v1/auth/oauth2"))
                        .redirectionEndpoint(endpoint -> endpoint.baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling // 실패 시 해당 메시지 반환
                        .authenticationEntryPoint(new FailedAuthenticationEntryPoint())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 설정 용 메소드 추가
     *
     * <li>추후 수정 필요</li>
     *
     * @return CorsConfigurationSource
     */

    @Bean
    protected CorsConfigurationSource corsConfigurationSource(){ // 추후 CORS 수정 필요
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
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

/**
 * 인증 실패 시 사용할 클래스
 *
 * <li>code와 message 추가</li>
 */
class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override

    /**
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param authException AuthenticationException
     * @throws IOException IOException
     * @throws ServletException ServletException
     *
     * <li>code와 message 추가</li>
     */
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("{\"code\": \"NP\", \"message\": \"No Permission\"}");
    }
}
