package com.dnd.jjakkak.domain.member.repository;

import com.dnd.jjakkak.domain.member.entity.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

/**
 * 멤버 레포지토리 테스트 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 27.
 */
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("카카오 ID로 멤버 조회 테스트")
    void testFindByKakaoId() {
        // given
        Member member = Member.builder()
                .memberNickname("test_nickname")
                .kakaoId(12345L)
                .build();
        memberRepository.save(member);

        // when
        Optional<Member> actual = memberRepository.findByKakaoId(12345L);

        // then
        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals("test_nickname", actual.get().getMemberNickname());
    }
}
