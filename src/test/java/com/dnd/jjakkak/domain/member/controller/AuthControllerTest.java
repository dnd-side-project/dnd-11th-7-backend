package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.domain.member.entity.RefreshToken;
import com.dnd.jjakkak.domain.member.repository.BlacklistedTokenRepository;
import com.dnd.jjakkak.domain.member.repository.RefreshTokenRepository;
import com.dnd.jjakkak.domain.member.service.BlacklistService;
import com.dnd.jjakkak.domain.member.service.RefreshTokenService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 테스트 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureRestDocs(uriHost = "43.202.65.170.nip.io", uriPort = 80)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    BlacklistService blacklistService;

    @Autowired
    EntityManager entityManager;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    BlacklistedTokenRepository blacklistedTokenRepository;

    @AfterEach
    void clear() {
        refreshTokenRepository.deleteAll();
        blacklistedTokenRepository.deleteAll();
    }

    @Test
    @Disabled
    @DisplayName("로그아웃 테스트 - 성공")
    void testLogoutSuccess() throws Exception {

        // given
        RefreshToken token = new RefreshToken("valid_refresh_token", 1L);
        refreshTokenRepository.save(token);

        String refreshToken = "Bearer valid_refresh_token";

        // expected
        mockMvc.perform(post("/api/v1/logout")
                        .header("Authorization", refreshToken))
                .andExpectAll(
                        status().isOk(),
                        content().string("Logout successful")
                )
                .andDo(document("auth/logout/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @Disabled
    @DisplayName("로그아웃 테스트 - 실패 (유효하지 않은 토큰)")
    void testLogoutFailInvalidToken() throws Exception {

        // given
        String refreshToken = "Bearer invalid_refresh_token";

        // expected
        mockMvc.perform(post("/api/v1/logout")
                        .header("Authorization", refreshToken))
                .andExpectAll(
                        status().isUnauthorized(),
                        jsonPath("$.code").value(401),
                        jsonPath("$.message").value("Invalid Refresh Token")
                )
                .andDo(document("auth/logout/fail-invalid-token",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").description("상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("validation").description("유효성 검사")
                        )
                ));
    }

    @Test
    @Disabled
    @DisplayName("로그아웃 테스트 - 실패 (헤더 없음)")
    void testLogoutFailNoHeader() throws Exception {

        // expected
        mockMvc.perform(post("/api/v1/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.code").value(400),
                        jsonPath("$.message").value("Invalid Header Error")
                )
                .andDo(document("auth/logout/fail-no-header",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").description("상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("validation").description("유효성 검사")
                        )
                ));
    }
}
