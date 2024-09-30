package com.dnd.jjakkak.global.config.proprties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 째깍 서비스 Properties 토큰 관리 클래스
 *
 * @author 류태웅
 * @version 2024. 09. 23.
 */

@Getter
@Setter
@ConfigurationProperties(prefix = "token")
public class TokenProperties {
    private String refreshTokenName;
    private String queryParam;
    private int accessTokenExpirationDay;
    private int refreshTokenExpirationDay;
}
