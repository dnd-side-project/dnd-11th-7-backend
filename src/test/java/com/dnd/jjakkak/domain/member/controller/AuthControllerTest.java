package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.config.AbstractRestDocsTest;
import com.dnd.jjakkak.config.JjakkakMockUser;
import com.dnd.jjakkak.domain.jwt.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthController 테스트 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 08. 06
 */
@WebMvcTest(AuthController.class)
@AutoConfigureRestDocs(uriHost = "43.202.65.170.nip.io", uriPort = 80)
class AuthControllerTest extends AbstractRestDocsTest {

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("로그인 상태 확인 - 확인됨")
    @JjakkakMockUser
    void testCheckAuthAuthenticated() throws Exception {

        // JwtProvider의 validate 메소드가 "user"를 반환하도록 설정
        when(jwtProvider.validate(anyString())).thenReturn("user");

        mockMvc.perform(get("/api/v1/check-auth")
                        .header("Authorization", "Bearer valid-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAuthenticated").value(true));
    }

    @Test
    @DisplayName("로그인 상태 확인 - 토큰이 인증되질 않음")
    @JjakkakMockUser
    void testCheckAuthNotAuthenticated() throws Exception {

        // JwtProvider의 validate 메소드가 null을 반환하도록 설정
        when(jwtProvider.validate(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/v1/check-auth")
                        .header("Authorization", "Bearer invalid-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAuthenticated").value(false));
    }

    @Test
    @DisplayName("로그인 상태 확인 - 토큰 없음")
    @JjakkakMockUser
    void testCheckAuthNoToken() throws Exception {
        mockMvc.perform(get("/api/v1/check-auth")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAuthenticated").value(false));
    }
}
