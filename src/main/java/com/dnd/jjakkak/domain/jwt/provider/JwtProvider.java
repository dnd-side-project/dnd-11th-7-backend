package com.dnd.jjakkak.domain.jwt.provider;

import com.dnd.jjakkak.domain.jwt.exception.MalformedTokenException;
import com.dnd.jjakkak.domain.jwt.exception.TokenExpiredException;
import com.dnd.jjakkak.global.config.proprties.JjakkakProperties;
import com.dnd.jjakkak.global.config.proprties.TokenProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * @version 2024. 08. 02.
 */

@Slf4j
@Component
public class JwtProvider {

    private final Key key;
    private final int accessTokenExpirationDay;
    private final int refreshTokenExpirationDay;

    public JwtProvider(JjakkakProperties jjakkakProperties, TokenProperties tokenProperties) {
        String jwtSecret = jjakkakProperties.getJwtSecret();
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        accessTokenExpirationDay = tokenProperties.getAccessTokenExpirationDay();
        refreshTokenExpirationDay = tokenProperties.getRefreshTokenExpirationDay();
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

        Date expiredDate = Date.from(Instant.now().plus(accessTokenExpirationDay, ChronoUnit.DAYS));

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
        Date expiredDate = Date.from(Instant.now().plus(refreshTokenExpirationDay, ChronoUnit.DAYS));
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
     * @param jwt String (JWT)
     * @return subject (kakaoId)
     */
    public String validateToken(String jwt) throws JwtException {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (MalformedJwtException e) {
            throw new MalformedTokenException();
        }
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