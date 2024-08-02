package com.dnd.jjakkak.domain.member.jwt.filter;

import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.entity.Role;
import com.dnd.jjakkak.domain.member.exception.MemberNotFoundException;
import com.dnd.jjakkak.domain.member.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import com.dnd.jjakkak.domain.member.service.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 권한과 인증 방식을 검사하는 필터입니다.
 *
 * @author 류태웅
 * @version 2024. 08. 02.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("도착한 요청: {}", request.getRequestURI());
        String token = parseBearerToken(request);
        log.info("도착한 토큰: {}", token);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String kakaoId = null;
        try {
            kakaoId = jwtProvider.validate(token);
            log.info("검증된 카카오 ID: {}", kakaoId);
        } catch (ExpiredJwtException e) {
            log.error("엑세스 토큰이 만료됨", e);
            setErrorResponse(response, "Token expired");
            return;
        } catch (MalformedJwtException e) {
            log.error("손상된 토큰", e);
            setErrorResponse(response, "Malformed Token");
            return;
        } catch (JwtException e) {
            log.error("토큰 검증 오류", e);
            setErrorResponse(response, "Invalid Token");
            return;
        }

        if (kakaoId == null) {
            handleTokenException(response, request);
            return;
        }

        processAuthentication(kakaoId, request);
        filterChain.doFilter(request, response);
    }

    private void processAuthentication(String kakaoId, HttpServletRequest request) {
        Member member = memberRepository.findByKakaoId(Long.parseLong(kakaoId))
                .orElseThrow(MemberNotFoundException::new);
        Role role = member.getRole();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.toString()));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(kakaoId, null, authorities);
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        securityContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(securityContext);
    }

    private void handleTokenException(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String refreshToken = parseRefreshToken(request);
        log.info("도착한 리프레시 토큰: {}", refreshToken);

        if (refreshToken != null && refreshTokenService.validateRefreshToken(refreshToken)) {
            String kakaoId = jwtProvider.getSubjectFromRefreshToken(refreshToken);
            String newToken = jwtProvider.createAccessToken(kakaoId);
            response.setHeader("Authorization", "Bearer " + newToken);
            log.info("새로 발급된 엑세스 토큰: {}", newToken);
            processAuthentication(kakaoId, request);
        } else {
            log.error("엑세스 토큰이 만료되었고 해당되는 리프레시 토큰이 존재하지 않습니다.");
            setErrorResponse(response, "Invalid Refresh Token");
        }
    }

    private void setErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
        response.getWriter().flush();
    }

    private String parseBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            if (!"undefined".equalsIgnoreCase(token)) {
                return token;
            }
        }

        return null;
    }

    private String parseRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("RefreshToken");
        return StringUtils.hasText(refreshToken) ? refreshToken : null;
    }
}
