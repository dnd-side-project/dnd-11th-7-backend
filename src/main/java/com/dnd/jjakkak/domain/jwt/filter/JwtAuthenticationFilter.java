package com.dnd.jjakkak.domain.jwt.filter;

import com.dnd.jjakkak.domain.jwt.exception.TokenExpiredException;
import com.dnd.jjakkak.domain.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.exception.MemberNotFoundException;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import com.dnd.jjakkak.global.config.security.SecurityEndpointPaths;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT 인증 필터입니다. (모든 요청에 1회 실행)
 *
 * @author 류태웅
 * @version 2024. 08. 03.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    /**
     * 화이트 리스트에 포함된 요청은 필터링하지 않습니다.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        return Arrays.stream(SecurityEndpointPaths.WHITE_LIST)
                .anyMatch(path ->
                        PatternMatchUtils.simpleMatch(path, request.getRequestURI()));
    }

    /**
     * 요청에 대해 JWT 인증을 수행합니다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = parseBearerToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String kakaoId;

        try {
            kakaoId = jwtProvider.validateToken(token);
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("accessError");
        }

        if (Strings.isEmpty(kakaoId)) {
            filterChain.doFilter(request, response);
            return;
        }

        authenticateUser(kakaoId, request);
        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 Bearer Token을 추출합니다.
     *
     * @param request HTTP 요청 객체 (HttpServletRequest)
     * @return Bearer Token
     */
    private String parseBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (Strings.isNotBlank(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        return null;
    }

    /**
     * 사용자 인증을 수행합니다. (SecurityContext - 사용자 정보 저장)
     *
     * @param kakaoId 사용자의 카카오 ID
     * @param request HTTP 요청 객체 (HttpServletRequest)
     */
    private void authenticateUser(String kakaoId, HttpServletRequest request) {

        Member member = memberRepository.findByKakaoId(Long.parseLong(kakaoId))
                .orElseThrow(MemberNotFoundException::new);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().name()));

        AbstractAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(member.getMemberId(), null, authorities);
        WebAuthenticationDetails authDetails = new WebAuthenticationDetailsSource().buildDetails(request);
        authToken.setDetails(authDetails);

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
