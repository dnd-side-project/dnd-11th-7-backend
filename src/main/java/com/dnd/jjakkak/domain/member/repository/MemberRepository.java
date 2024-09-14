package com.dnd.jjakkak.domain.member.repository;

import com.dnd.jjakkak.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

    // 대량의 데이터 일괄 삭제 개선
    // Query-SQL을 지정해주지 않으면 delete를 여러번 수행한다.
    @Modifying
    @Query("delete from Member m where m.isDelete = true")
    void deleteAllByIsDeleteTrue();
}