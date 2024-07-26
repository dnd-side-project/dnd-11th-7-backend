package com.dnd.jjakkak.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 블랙리스트에 추가된 토큰을 저장하는 엔티티입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false, name="expiration_date")
    private LocalDateTime expirationDate;
}
