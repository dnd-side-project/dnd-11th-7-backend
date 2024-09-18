package com.dnd.jjakkak.global.config.security;

import com.dnd.jjakkak.domain.jwt.filter.JwtAuthenticationFilter;
import com.dnd.jjakkak.domain.jwt.handler.OAuth2FailureHandler;
import com.dnd.jjakkak.domain.jwt.handler.OAuth2LogoutHandler;
import com.dnd.jjakkak.domain.jwt.handler.OAuth2SuccessHandler;
import com.dnd.jjakkak.global.config.proprties.JjakkakProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.dnd.jjakkak.global.config.security.SecurityEndpointPaths.*;

/**
 * Spring Security Configuration Class.
 *
 * @author 류태웅
 * @version 2024. 08. 03.
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final DefaultOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final OAuth2LogoutHandler oAuth2LogoutHandler;
    private final JjakkakProperties jjakkakProperties;
    private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;
    private final FailedAuthenticationEntryPoint failedAuthenticationEntryPoint;

    /**
     * Security Bean 등록.
     *
     * <li>CSRF 비활성화</li>
     * <li>CORS 비활성화 -> corsConfigurationSource()로 설정</li>
     * <li>Form Login 비활성화</li>
     * <li>비회원도 접근 가능한 whiteList, 회원만 접근 가능한 userList, 관리자만 접근 가능한 adminList</li>
     *
     * <li>httpBasic 비활성화</li>
     * <li>세션 비활성화</li>
     * <li>OAuth2 로그인 설정 : 로그인 url, 리다이렉트 url, 유저 서비스, 성공 시 핸들러</li>
     * <li>로그인 uri : <a href="/api/v1/auth/oauth2/kakao">/api/v1/auth/oauth2/kakao</a></li>
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
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(WHITE_LIST).permitAll()
                        .requestMatchers(USER_LIST).hasRole("USER")
                        .requestMatchers(ADMIN_LIST).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(customAuthorizationRequestResolver))
                        .redirectionEndpoint(endpoint -> endpoint.baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                .logout(logout -> logout
                        .addLogoutHandler(oAuth2LogoutHandler)
                        .logoutUrl("/api/v1/logout")
                        .deleteCookies("refresh_token")
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK))
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling // 실패 시 해당 메시지 반환
                        .authenticationEntryPoint(failedAuthenticationEntryPoint)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 설정 빈 등록.
     *
     * <li>credentials를 허용하기 위해 특정 도메인 추가</li>
     *
     * @return CORS 설정 객체
     */
    @Bean
    protected CorsConfigurationSource corsConfigurationSource() { // 추후 CORS 수정 필요
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(jjakkakProperties.getFrontUrl())); // 허용할 도메인 명시
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Access-Control-Allow-Headers", "Access-Control-Expose-Headers", "_retry"));
        config.addExposedHeader("Authorization"); //프론트에서 해당 헤더를 읽을 수 있게
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Security ignore 설정 빈 등록.
     *
     * <li>static Resource (favicon.ico, index.html)</li>
     */
    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {

        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/index.html");
    }
}
