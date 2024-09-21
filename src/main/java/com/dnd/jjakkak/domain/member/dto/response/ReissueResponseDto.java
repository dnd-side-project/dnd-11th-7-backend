package com.dnd.jjakkak.domain.member.dto.response;

import lombok.Getter;
import org.springframework.http.ResponseCookie;

/**
 * Token 재발급 요청에 대한 응답 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 09. 21.
 */
@Getter
public class ReissueResponseDto {

    private String accessToken;
    private ResponseCookie refreshTokenCookie;

    public ReissueResponseDto(String accessToken, ResponseCookie refreshTokenCookie) {
        this.accessToken = accessToken;
        this.refreshTokenCookie = refreshTokenCookie;
    }
}
