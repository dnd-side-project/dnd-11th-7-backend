package com.dnd.jjakkak.domain.jwt.provider;

import com.dnd.jjakkak.domain.jwt.exception.MalformedTokenException;
import com.dnd.jjakkak.global.config.proprties.JjakkakProperties;
import com.dnd.jjakkak.global.config.proprties.TokenProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
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

//        Date expiredDate = Date.from(Instant.now().plus(accessTokenExpirationDay, ChronoUnit.DAYS));

        // TODO: 테스트용으로 만료시간 1분으로 설정
        Date expiredDate = Date.from(Instant.now().plus(1, ChronoUnit.MINUTES));

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
//        Date expiredDate = Date.from(Instant.now().plus(refreshTokenExpirationDay, ChronoUnit.DAYS));

        // TODO: 테스트용으로 만료시간 2분으로 설정
        Date expiredDate = Date.from(Instant.now().plus(2, ChronoUnit.MINUTES));


        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(kakaoId)
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                .compact();
    }

    /**
     * JWT를 검증하는 메소드.
     *
     * @param jwt String (JWT)
     * @return subject (kakaoId)
     * @throws ExpiredJwtException 토큰이 만료되었을 경우 발생합니다.
     */
    public String validateToken(String jwt) throws JwtException {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
            return claims.getSubject();
        } catch (MalformedJwtException e) {
            throw new MalformedTokenException();
        }
    }
}