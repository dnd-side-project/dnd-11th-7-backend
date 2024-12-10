package com.dnd.jjakkak.domain.member.dto.response;

import lombok.Getter;

/**
 * {class name}.
 *
 * @author 정승조
 * @version 2024. 11. 26.
 */
@Getter
public class AccessTokenResponseDto {

    private final String accessToken;

    public AccessTokenResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
