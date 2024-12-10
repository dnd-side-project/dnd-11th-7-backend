package com.dnd.jjakkak.domain.member.repository;

import com.dnd.jjakkak.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Member의 Repository입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 29.
 */

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByKakaoId(long kakaoId);

    boolean existsByKakaoId(Long kakaoId);
}