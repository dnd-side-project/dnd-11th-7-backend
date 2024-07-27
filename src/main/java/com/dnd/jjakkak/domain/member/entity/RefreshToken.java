package com.dnd.jjakkak.domain.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리프레쉬 토큰 엔티티입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 24.
 */

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false, name = "member_id")
    private long memberId;

    public RefreshToken(String token, long memberId) {
        this.token = token;
        this.memberId = memberId;
    }
}
