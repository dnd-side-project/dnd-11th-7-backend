package com.dnd.jjakkak.domain.member.jwt.filter;

import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.entity.Role;
import com.dnd.jjakkak.domain.member.exception.MemberNotFoundException;
import com.dnd.jjakkak.domain.member.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import com.dnd.jjakkak.domain.member.service.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
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
 * @version 2024. 07. 27.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;

    /**
     * 검증을 수행하는 메소드입니다.
     * 여기서 jwtProvider의 validate를 수행합니다.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param filterChain FilterChain
     * @throws ServletException ServletException
     * @throws IOException IOException
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("도착한 요청: {}", request.getPathInfo());
        String token = parseBearerToken(request);
        log.info("도착한 토큰: {}", token);
        if(token == null){ // Bearer 인증 방식이 아니거나 빈 값일 경우 진행하지 말고 다음 필터로 바로 넘김
            filterChain.doFilter(request, response);
            return;
        }
        String kakaoId;
        try {
            kakaoId = jwtProvider.validate(token);
            log.info("검증된 카카오 ID: {}", kakaoId);
        } catch (ExpiredJwtException e) {
            log.error("엑세스 토큰이 만료됨", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Access Token expired\"}");
            response.getWriter().flush();
            return;
        }
        if (kakaoId == null) { // AccessToken 검증 실패 시
            String refreshToken = parseRefreshToken(request);
            log.info("도착한 리프레시 토큰: {}", refreshToken);
            if (refreshToken != null && refreshTokenService.validateRefreshToken(refreshToken)) {
                // RefreshToken이 유효한 경우 새로운 AccessToken 발급
                kakaoId = jwtProvider.getSubjectFromRefreshToken(refreshToken);
                token = jwtProvider.createAccessToken(kakaoId);
                response.setHeader("Authorization", "Bearer " + token);
                log.info("새로 발급된 엑세스 토큰: {}", token);
            } else {
                // RefreshToken도 유효하지 않으면 로그아웃 처리 및 프론트엔드로 메시지 전송
                log.error("엑세스 토큰이 만료되었고 해당되는 리프레시 토큰이 존재하지 않습니다.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid Refresh Token\"}");
                response.getWriter().flush();
                return;
            }
        }
        Member member = memberRepository.findByKakaoId(Long.parseLong(kakaoId))
                .orElseThrow(MemberNotFoundException::new);
        Role role = member.getRole(); // ROLE_USER, ROLE_ADMIN

        // 예시 : ROLE_MASTER, ROLE_DEVELOPER
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.toString()));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(kakaoId, null, authorities); // pwd는 토큰에 추가X -> null
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        securityContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(securityContext);

        filterChain.doFilter(request, response);
    }

    /**
     * 헤더를 검증하는 메소드입니다.
     * @param request HttpServletRequest
     * @return authorization String
     */

    private String parseBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization"); // 응답의 헤더에서 뽑아옴

        boolean hasAuthorization = StringUtils.hasText(authorization);
        if(!hasAuthorization) return null; // null이거나 문자가 아닌 경우 null

        boolean isBearer = authorization.startsWith("Bearer ");
        if(!isBearer) return null; // 아닐 경우 Bearer 인증 방신이 아님 -> null

        String token = authorization.substring(7);
        if ("undefined".equalsIgnoreCase(token)) {
            return null;
        }

        return token;
    }

    /**
     * 헤더에서 RefreshToken을 검증하는 메소드입니다.
     * @param request HttpServletRequest
     * @return refreshToken String
     */
    private String parseRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("RefreshToken");

        boolean hasRefreshToken = StringUtils.hasText(refreshToken);
        if (!hasRefreshToken) return null;

        return refreshToken;
    }
}