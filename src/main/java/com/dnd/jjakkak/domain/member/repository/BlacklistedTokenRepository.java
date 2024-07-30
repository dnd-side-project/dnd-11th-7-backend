package com.dnd.jjakkak.domain.member.repository;

import com.dnd.jjakkak.domain.member.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 블랙리스트에 추가된 토큰을 관리하는 리포지토리입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    Optional<BlacklistedToken> findByToken(String token);
    void deleteByExpirationDateBefore(LocalDateTime now);
}
