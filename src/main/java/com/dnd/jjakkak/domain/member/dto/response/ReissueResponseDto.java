package com.dnd.jjakkak.domain.member.dto.response;

import lombok.Getter;

/**
 * Token 재발급 응답 DTO 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 09. 22.
 */
@Getter
public class ReissueResponseDto {

    private String accessToken;
    private String refreshToken;

    public ReissueResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
