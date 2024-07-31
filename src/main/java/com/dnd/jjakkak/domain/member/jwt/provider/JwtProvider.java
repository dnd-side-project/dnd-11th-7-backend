package com.dnd.jjakkak.domain.member.jwt.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * JWT를 만들고 검증하는 Provider입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */

@Slf4j
@Component
public class JwtProvider {
    private final Key key;

    public JwtProvider(@Value("${secret.key}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT access Token을 만드는 메소드
     *
     * <li>특이 사항으로, JWT를 member의 기본키로 생성하는 것이 아님.</li>
     * <li>kakaoId로 생성함</li>
     * <li>만료시간 30분</li>
     *
     * @param kakaoId String
     * @return JWT
     */

    public String createAccessToken(String kakaoId) {
        Date expiredDate = Date.from(Instant.now().plus(30, ChronoUnit.MINUTES));
        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(kakaoId)
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                .compact();
    }

    /**
     * JWT refresh Token을 만드는 메소드
     *
     * <li>로그아웃에 사용</li>
     * <li>만료시간 1주일</li>
     *
     * @return JWT
     */

    public String createRefreshToken(String kakaoId) {
        Date expiredDate = Date.from(Instant.now().plus(7, ChronoUnit.DAYS));
        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(kakaoId)
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                .compact();
    }

    /**
     * JWT를 검증하는 메소드
     *
     * @param jwt String
     * @return subject
     */

    public String validate(String jwt) {
        String subject;
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
            subject = claims.getSubject();
            log.info("subject, {}", subject);
        } catch (ExpiredJwtException e) {
            log.error("JWT 만료", e);
            return null;
        } catch (MalformedJwtException e) {
            log.error("잘못된 토큰", e);
            return null;
        }
        catch (Exception e) {
            log.error("JWT 검증 실패", e);
            return null;
        }
        return subject;
    }

    /**
     * RefreshToken에서 subject를 추출하는 메소드
     *
     * @param jwt String
     * @return subject
     */
    public String getSubjectFromRefreshToken(String jwt) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        return claims.getSubject();
    }
}