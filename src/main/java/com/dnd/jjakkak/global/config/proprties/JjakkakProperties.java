package com.dnd.jjakkak.global.config.proprties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 째깍 서비스 Properties 관리 클래스
 *
 * @author 정승조
 * @version 2024. 08. 18.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "jjakkak")
public class JjakkakProperties {

    private String frontUrl;

    private String jwtSecret;
}
