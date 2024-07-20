package com.dnd.zzaekkac.domain.member.jwt.filter;

import com.dnd.zzaekkac.domain.member.entity.Member;
import com.dnd.zzaekkac.domain.member.entity.Role;
import com.dnd.zzaekkac.domain.member.jwt.provider.JwtProvider;
import com.dnd.zzaekkac.domain.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
 * @version 2024. 07. 20.
 */

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

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
        try {
            String token = parseBearerToken(request);
            if(token == null){ // Bearer 인증 방식이 아니거나 빈 값일 경우 진행하지 말고 다음 필터로 바로 넘김
                filterChain.doFilter(request, response);
                return;
            }
            String kakaoId = jwtProvider.validate(token);
            if(kakaoId == null){ // 검증 실패 시 다음 필터로 바로 넘김
                filterChain.doFilter(request, response);
                return;
            }
            Member member = memberRepository.findByKakaoId(Long.parseLong(kakaoId));
            Role role = member.getRole(); // ROLE_USER, ROLE_ADMIN

            // 예시 : ROLE_MASTER, ROLE_DEVELOPER
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role.toString()));

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(kakaoId, null, authorities); // pwd는 토큰에 추가X -> null
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            securityContext.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(securityContext);
        } catch (Exception e){
            e.printStackTrace();
        }

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

        return authorization.substring(7);
    }
}