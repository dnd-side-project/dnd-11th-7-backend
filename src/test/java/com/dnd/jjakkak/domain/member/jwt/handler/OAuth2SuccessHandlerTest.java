package com.dnd.jjakkak.domain.member.jwt.handler;

import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.service.RefreshTokenService;
import jakarta.servlet.ServletException;
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
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * 로그인/회원가입 성공 시 테스트 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

    @InjectMocks
    OAuth2SuccessHandler oAuth2SuccessHandler;

    @Mock
    JwtProvider jwtProvider;

    @Mock
    RefreshTokenService refreshTokenService;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        authentication = Mockito.mock(Authentication.class);
    }

    @Test
    @DisplayName("OAuth2 인증 성공 후 토큰 발급 테스트")
    void testOnAuthenticationSuccess() throws IOException, ServletException, ServletException {
        // given
        Member member = Member.builder().kakaoId(12345L).memberNickname("test").build();
        String accessToken = "access_token";
        String refreshToken = "refresh_token";

        when(authentication.getPrincipal()).thenReturn(member);
        when(jwtProvider.createAccessToken("12345")).thenReturn(accessToken);
        when(jwtProvider.createRefreshToken("12345")).thenReturn(refreshToken);

        // when
        oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        assertThat(response.getHeader("Authorization")).isEqualTo("Bearer " + accessToken);
        assertThat(response.getHeader("RefreshToken")).isEqualTo(refreshToken);
    }
}
