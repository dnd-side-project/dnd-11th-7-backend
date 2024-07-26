package com.dnd.jjakkak.domain.member.service;

import com.dnd.jjakkak.domain.member.entity.BlacklistedToken;
import com.dnd.jjakkak.domain.member.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 블랙리스트에 관련된 로직을 처리하는 서비스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    /**
     * 주어진 토큰이 블랙리스트에 있는지 확인합니다.
     * 아직은 사용하지 않아서 향후 계속 사용하지 않는다면 지울 계획입니다.
     *
     * @param token 블랙리스트에 추가된 토큰
     * @return 토큰이 블랙리스트에 있으면 true, 그렇지 않으면 false
     */
    @Transactional(readOnly = true)
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.findByToken(token).isPresent();
    }

    /**
     * 주어진 토큰을 블랙리스트에 추가합니다.
     *
     * @param token 블랙리스트에 추가할 토큰
     * @param expirationDate 토큰의 만료 시간
     */
    @Transactional
    public void blacklistToken(String token, LocalDateTime expirationDate) {
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedToken.setExpirationDate(expirationDate);
        blacklistedTokenRepository.save(blacklistedToken);
    }

    /**
     * 만료된 블랙리스트 토큰을 삭제합니다.
     */
    @Transactional
    public void removeExpiredTokens() {
        blacklistedTokenRepository.deleteByExpirationDateBefore(LocalDateTime.now());
    }

    /**
     * 주기적으로 만료된 블랙리스트 토큰을 삭제하는 스케줄링 작업입니다.
     */
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void removeExpiredTokensScheduler() {
        removeExpiredTokens();
    }
}
