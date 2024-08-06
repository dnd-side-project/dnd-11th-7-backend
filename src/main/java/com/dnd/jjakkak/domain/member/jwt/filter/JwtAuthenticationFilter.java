package com.dnd.jjakkak.domain.member.jwt.filter;

import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.entity.Role;
import com.dnd.jjakkak.domain.member.exception.MemberNotFoundException;
import com.dnd.jjakkak.domain.member.jwt.exception.AccessTokenExpiredException;
import com.dnd.jjakkak.domain.member.jwt.exception.MalformedTokenException;
import com.dnd.jjakkak.domain.member.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
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
 * 검증을 실행하는 필터입니다.
 *
 * @author 류태웅
 * @version 2024. 08. 03.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = parseBearerToken(request);
        log.info("도착한 토큰: {}", token);
        if (token == null) { // Bearer 인증 방식이 아니거나 빈 값일 경우 진행하지 말고 다음 필터로 바로 넘김
            filterChain.doFilter(request, response);
            return;
        }
        String kakaoId;
        try {
            kakaoId = jwtProvider.validate(token);
            log.info("검증된 카카오 ID: {}", kakaoId);
        } catch (ExpiredJwtException e) {
            log.error("엑세스 토큰이 만료됨", e);
            throw new AccessTokenExpiredException();
        } catch (MalformedJwtException e) {
            log.error("손상된 토큰", e);
            throw new MalformedTokenException();
        }

        if (kakaoId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Member member = memberRepository.findByKakaoId(Long.parseLong(kakaoId))
                .orElseThrow(MemberNotFoundException::new);
        Role role = member.getRole();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.toString()));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member, null, authorities);
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        securityContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(securityContext);

        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
}
