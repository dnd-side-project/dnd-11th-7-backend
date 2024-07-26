package com.dnd.jjakkak.domain.member.jwt.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT 생성 및 검증 테스트 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @Value("${secret.key}")
    private String secretKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtProvider = new JwtProvider(secretKey);
    }

    @Test
    @DisplayName("JWT Access Token 생성 테스트")
    void testCreateAccessToken() {
        // given
        String kakaoId = "12345";

        // when
        String token = jwtProvider.createAccessToken(kakaoId);

        // then
        assertNotNull(token);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
        assertEquals(kakaoId, claims.getSubject());
    }

    @Test
    @DisplayName("JWT Refresh Token 생성 테스트")
    void testCreateRefreshToken() {
        // given
        String kakaoId = "12345";

        // when
        String token = jwtProvider.createRefreshToken(kakaoId);

        // then
        assertNotNull(token);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
        assertEquals(kakaoId, claims.getSubject());
    }

    @Test
    @DisplayName("JWT 유효성 검증 테스트")
    void testValidate() {
        // given
        String kakaoId = "12345";
        String token = jwtProvider.createAccessToken(kakaoId);

        // when
        String subject = jwtProvider.validate(token);

        // then
        assertEquals(kakaoId, subject);
    }

    @Test
    @DisplayName("만료된 JWT 유효성 검증 테스트")
    void testValidateExpiredToken() {
        // given
        String kakaoId = "12345";
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(kakaoId)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().minus(1, ChronoUnit.MINUTES)))
                .compact();

        // when
        String subject = jwtProvider.validate(token);

        // then
        assertNull(subject);
    }

    @Test
    @DisplayName("RefreshToken에서 subject 추출 테스트")
    void testGetSubjectFromRefreshToken() {
        // given
        String kakaoId = "12345";
        String token = jwtProvider.createRefreshToken(kakaoId);

        // when
        String subject = jwtProvider.getSubjectFromRefreshToken(token);

        // then
        assertEquals(kakaoId, subject);
    }
}
