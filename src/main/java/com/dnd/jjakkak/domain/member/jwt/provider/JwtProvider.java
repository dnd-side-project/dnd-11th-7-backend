package com.dnd.jjakkak.domain.member.jwt.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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
 * @version 2024. 07. 20.
 */

@Component
public class JwtProvider {
    @Value("${secret.key}")
    private String secretKey;

    /**
     * JWT를 만드는 메소드
     *
     * <li>특이 사항으로, JWT를 member의 기본키로 생성하는 것이 아님.</li>
     * <li>kakaoId로 생성함</li>
     *
     * @param kakaoId String
     * @return JWT
     */

    public String create(String kakaoId){
        // 만료 시간
        Date expiredDate = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));

        // 비밀 키 만들기
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // JWT 만들기
        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(kakaoId).setIssuedAt(new Date()).setExpiration(expiredDate)
                .compact();
    }

    /**
     * JWT를 검증하는 메소드
     *
     * @param jwt String
     * @return subject
     */

    public String validate(String jwt){
        String subject = null;
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
            subject = claims.getSubject();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return subject;
    }
}
