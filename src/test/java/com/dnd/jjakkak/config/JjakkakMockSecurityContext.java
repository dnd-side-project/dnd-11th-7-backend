package com.dnd.jjakkak.config;

import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

/**
 * 테스트에서 사용할 MockUser 어노테이션에 SecurityContext 값을 설정하는 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 05.
 */
@RequiredArgsConstructor
public class JjakkakMockSecurityContext implements WithSecurityContextFactory<JjakkakMockUser> {

    private final MemberRepository memberRepository;


    @Override
    public SecurityContext createSecurityContext(JjakkakMockUser annotation) {

        Member member = Member.builder()
                .memberNickname(annotation.nickname())
                .kakaoId(annotation.kakaoId())
                .build();

        memberRepository.save(member);

        SimpleGrantedAuthority role = new SimpleGrantedAuthority("ROLE_USER");
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(member.getMemberId(), null, List.of(role));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);

        return context;
    }
}
