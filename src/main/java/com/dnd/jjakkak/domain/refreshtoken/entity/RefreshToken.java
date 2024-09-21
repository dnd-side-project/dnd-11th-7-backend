package com.dnd.jjakkak.domain.refreshtoken.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Refresh Token 엔티티 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 09. 21.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Builder
    public RefreshToken(Long id, String token) {
        this.id = id;
        this.token = token;
    }
}
