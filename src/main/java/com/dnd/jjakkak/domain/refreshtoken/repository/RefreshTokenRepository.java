package com.dnd.jjakkak.domain.refreshtoken.repository;

import com.dnd.jjakkak.domain.refreshtoken.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Refresh Token 레포지토리 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 09. 21.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {


}
