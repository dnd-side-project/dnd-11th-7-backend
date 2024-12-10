package com.dnd.jjakkak.global.config.proprties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

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

    private List<String> frontUrl;

    private String jwtSecret;
}
