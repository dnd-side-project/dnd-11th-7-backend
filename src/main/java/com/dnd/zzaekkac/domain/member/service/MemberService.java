package com.dnd.zzaekkac.domain.member.service;

import com.dnd.zzaekkac.domain.member.entity.Member;
import com.dnd.zzaekkac.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Member의 Service입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 20.
 */

@Service
@RequiredArgsConstructor
public class MemberService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    /**
     * 로그인/가입 시 회원 정보를 불러와 저장하는 메소드
     *
     * <li>loadUser릁 통해 멤버를 불러온 뒤</li>
     * <li>json에서 각 정보를 추출하여 builder로 Member 생성 후 save</li>
     *
     * @param userRequest OAuth2UserRequest
     * @throws OAuth2AuthenticationException OAuth2AuthenticationException
     * @return member OAuth2User
     */

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String oauth2ClientName = userRequest.getClientRegistration().getClientName();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        Member member = new Member();

        if(oauth2ClientName.equals("kakao")){
            member = Member.builder()
                    .memberNickname(properties.get("nickname").toString())
                    .kakaoId(Long.parseLong(attributes.get("id").toString()))
                    .memberProfile(properties.get("profile_image").toString())
                    .build();
        }
        memberRepository.save(member);
        return member;
    }
}
