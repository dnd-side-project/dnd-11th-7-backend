package com.dnd.jjakkak.domain.jwt.filter;

import com.dnd.jjakkak.domain.jwt.exception.AccessTokenExpiredException;
import com.dnd.jjakkak.domain.jwt.exception.MalformedTokenException;
import com.dnd.jjakkak.domain.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.exception.MemberNotFoundException;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import com.dnd.jjakkak.global.config.security.SecurityEndpointPaths;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 인증 필터입니다. (모든 요청에 1회 실행)
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

        String path = request.getRequestURI();

        if (isPathInWhiteList(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = parseBearerToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }


        String kakaoId = validateToken(token);
        if (Strings.isEmpty(kakaoId)) {
            filterChain.doFilter(request, response);
            return;
        }

        authenticateUser(kakaoId, request);
        filterChain.doFilter(request, response);
    }

    /**
     * 요청 URI가 화이트 리스트에 포함되어 있는지 확인합니다.
     *
     * @param path 요청 URI
     * @return 화이트 리스트에 포함되어 있으면 true, 아니면 false
     */
    private boolean isPathInWhiteList(String path) {
        return PatternMatchUtils.simpleMatch(SecurityEndpointPaths.WHITE_LIST, path);
    }

    /**
     * 토큰을 통해 사용자 정보(kakaoId) 조회
     *
     * @param token Access Token
     * @return kakaoId
     */
    private String validateToken(String token) {
        try {
            return jwtProvider.validate(token);
        } catch (ExpiredJwtException e) {
            throw new AccessTokenExpiredException();
        } catch (MalformedJwtException e) {
            throw new MalformedTokenException();
        }
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

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authToken);
        SecurityContextHolder.setContext(securityContext);
    }
}
