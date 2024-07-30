package com.dnd.jjakkak.domain.member.service;

import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * Member의 OAuth2 Service입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 29.
 */

@Service
@RequiredArgsConstructor
public class MemberOAuth2Service extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    /**
     * 로그인/가입 시 회원 정보를 불러와 저장하는 메소드
     *
     * <li>loadUser릁 통해 멤버를 불러온 뒤</li>
     * <li>json에서 각 정보를 추출하여 builder로 Member 생성 후 save</li>
     * <li>지금은 profile을 null로 하였는데 나중엔 기본 프사로 값을 채워야 할 듯?</li>
     *
     * @param userRequest OAuth2UserRequest
     * @return member OAuth2User
     * @throws OAuth2AuthenticationException OAuth2AuthenticationException
     */

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String oauth2ClientName = userRequest.getClientRegistration().getClientName();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        long kakaoId = Long.parseLong(attributes.get("id").toString());
        String nickname = properties.get("nickname").toString();
        Optional<Member> member = memberRepository.findByKakaoId(kakaoId);
        if (member.isPresent()) {
            return member.get();
        }

        Member newMember = new Member();
        if ("kakao".equals(oauth2ClientName)) {
            newMember = createMember(nickname, kakaoId);
        }
        memberRepository.save(newMember);
        return newMember;
    }

    private Member createMember(String nickname, long kakaoId) {
        return Member.builder()
                .memberNickname(nickname)
                .kakaoId(kakaoId)
                .build();
    }


}