package com.dnd.jjakkak.domain.member.jwt.filter;

import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * JWT 검증 필터 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @InjectMocks
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    JwtProvider jwtProvider;

    @Mock
    MemberRepository memberRepository;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("유효한 JWT로 인증 성공 테스트")
    void testDoFilterInternal_ValidJwt() throws ServletException, IOException {
        // given
        String token = "valid_token";
        String kakaoId = "12345";
        Member member = Member.builder().kakaoId(12345L).memberNickname("test").build();

        request.addHeader("Authorization", "Bearer " + token);

        when(jwtProvider.validate(token)).thenReturn(kakaoId);
        when(memberRepository.findByKakaoId(12345L)).thenReturn(Optional.of(member));

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, Mockito.mock(FilterChain.class));

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isInstanceOf(UsernamePasswordAuthenticationToken.class);
    }

    @Test
    @DisplayName("만료된 JWT로 인증 실패 테스트")
    void testDoFilterInternal_ExpiredJwt() throws ServletException, IOException {
        // given
        String token = "expired_token";

        request.addHeader("Authorization", "Bearer " + token);

        when(jwtProvider.validate(token)).thenThrow(new ExpiredJwtException(null, null, "JWT 만료"));

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, Mockito.mock(FilterChain.class));

        // then
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
