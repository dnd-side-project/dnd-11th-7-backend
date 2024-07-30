package com.dnd.jjakkak.domain.member.service;

import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;

/**
 * MemberService 테스트 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberOAuth2Service memberService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    OAuth2UserRequest oAuth2UserRequest;

    @Mock
    OAuth2User oAuth2User;

    @Test
    @DisplayName("회원 정보 조회 및 저장 테스트")
    void testLoadUser() throws OAuth2AuthenticationException {
        // given
        Map<String, Object> attributes = Map.of(
                "id", "12345",
                "properties", Map.of("nickname", "test_nickname")
        );
        Mockito.when(oAuth2UserRequest.getClientRegistration().getClientName()).thenReturn("kakao");
        Mockito.when(oAuth2User.getAttributes()).thenReturn(attributes);
        Mockito.when(memberRepository.findByKakaoId(12345L)).thenReturn(Optional.empty());

        // when
        OAuth2User actual = memberService.loadUser(oAuth2UserRequest);

        // then
        Mockito.verify(memberRepository, Mockito.times(1)).save(Mockito.any(Member.class));
        Assertions.assertEquals("test_nickname", actual.getName());
    }

    @Test
    @DisplayName("기존 회원 정보 업데이트 테스트")
    void testUpdateExistingMember() throws OAuth2AuthenticationException {
        // given
        Map<String, Object> attributes = Map.of(
                "id", "12345",
                "properties", Map.of("nickname", "new_nickname")
        );
        Member existingMember = Member.builder()
                .memberNickname("old_nickname")
                .kakaoId(12345L)
                .build();
        Mockito.when(oAuth2UserRequest.getClientRegistration().getClientName()).thenReturn("kakao");
        Mockito.when(oAuth2User.getAttributes()).thenReturn(attributes);
        Mockito.when(memberRepository.findByKakaoId(12345L)).thenReturn(Optional.of(existingMember));

        // when
        OAuth2User actual = memberService.loadUser(oAuth2UserRequest);

        // then
        Mockito.verify(memberRepository, Mockito.times(1)).save(existingMember);
        Assertions.assertEquals("new_nickname", existingMember.getMemberNickname());
    }
}