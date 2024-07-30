package com.dnd.jjakkak.domain.member.repository;

import com.dnd.jjakkak.domain.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RefreshToken의 Repository입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByToken(String token);
    boolean existsByToken(String token);
}
