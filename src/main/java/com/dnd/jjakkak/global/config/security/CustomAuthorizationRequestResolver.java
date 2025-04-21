package com.dnd.jjakkak.global.config.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.UUID;

/**
 * OAuth 로그인 요청 리졸버 커스터마이징 클래스.
 *
 * @author 정승조
 * @version 2024. 09. 18.
 */
@Component
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private static final String AUTHORIZATION_REQUEST_BASE_URI = "/api/v1/auth/oauth2";
    private static final String REDIRECT_PARAM = "redirect";
    private final OAuth2AuthorizationRequestResolver defaultAuthorizationRequestResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultAuthorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, AUTHORIZATION_REQUEST_BASE_URI);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = defaultAuthorizationRequestResolver.resolve(request);
        return customizeAuthorizationRequest(request, authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultAuthorizationRequestResolver.resolve(request, clientRegistrationId);
        return customizeAuthorizationRequest(request, authorizationRequest);
    }

    /**
     * 쿼리 파라미터에서 redirect 값을 추출하여 state 파라미터에 인코딩하여 전달합니다.
     *
     * @param request              HttpServletRequest
     * @param authorizationRequest OAuth2AuthorizationRequest
     * @return OAuth2AuthorizationRequest
     */
    private OAuth2AuthorizationRequest customizeAuthorizationRequest(HttpServletRequest request, OAuth2AuthorizationRequest authorizationRequest) {

        if (authorizationRequest == null) {
            return null;
        }

        String redirectUri = request.getParameter(REDIRECT_PARAM);

        if (redirectUri == null || redirectUri.isEmpty()) {
            return authorizationRequest;
        }


        String stateValue = redirectUri + ":" + UUID.randomUUID();
        String encodedState = Base64.getEncoder().encodeToString(stateValue.getBytes());

        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .state(encodedState)
                .build();
    }
}