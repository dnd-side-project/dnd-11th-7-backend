package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.config.AbstractRestDocsTest;
import com.dnd.jjakkak.config.JjakkakMockUser;
import com.dnd.jjakkak.domain.member.service.AuthService;
import com.dnd.jjakkak.domain.member.service.BlacklistService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 테스트 클래스입니다.
 *
 * @author 류태웅, 정승조
 * @version 2024. 08. 06
 */
@WebMvcTest(AuthController.class)
@AutoConfigureRestDocs(uriHost = "43.202.65.170.nip.io", uriPort = 80)
class AuthControllerTest extends AbstractRestDocsTest {

    @MockBean
    AuthService authService;

    @MockBean
    BlacklistService blacklistService;

    @Test
    @DisplayName("로그인 상태 확인 - 확인됨")
    @JjakkakMockUser
    void testCheckAuthAuthenticated() throws Exception {

        when(authService.checkAuth(anyString()))
                .thenReturn(true);

        mockMvc.perform(get("/api/v1/auth/check")
                        .header("Authorization", "Bearer valid-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAuthenticated").value(true));
    }

    @Test
    @DisplayName("로그인 상태 확인 - 토큰이 인증되질 않음")
    @JjakkakMockUser
    void testCheckAuthNotAuthenticated() throws Exception {

        when(authService.checkAuth(anyString()))
                .thenReturn(false);

        mockMvc.perform(get("/api/v1/auth/check")
                        .header("Authorization", "Bearer invalid-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAuthenticated").value(false));
    }

    @Test
    @DisplayName("로그인 상태 확인 - 토큰 없음")
    @JjakkakMockUser
    void testCheckAuthNoToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/check")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAuthenticated").value(false));
    }

    @Test
    @DisplayName("토큰 재발급 - 실패 (Refresh Token 없음)")
    @JjakkakMockUser
    void testReissue_NoRefreshToken() throws Exception {

        mockMvc.perform(get("/api/v1/auth/reissue"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("토큰 재발급 - 성공")
    @JjakkakMockUser
    void testReissue_InvalidRefreshToken() throws Exception {

        MockCookie refreshToken = new MockCookie("refresh_token", "1a2s2d3f4g");
        String accessToken = "Bearer mock-access-token";

        when(authService.reissueAccessToken(anyString()))
                .thenReturn(accessToken);

        mockMvc.perform(get("/api/v1/auth/reissue")
                        .cookie(refreshToken))
                .andExpectAll(
                        status().isOk(),
                        header().string("Authorization", accessToken)
                );
    }
}
