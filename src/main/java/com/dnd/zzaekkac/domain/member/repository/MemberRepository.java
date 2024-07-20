package com.dnd.zzaekkac.domain.member.repository;

import com.dnd.zzaekkac.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Member의 Repository입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 20.
 */

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByKakaoId(long kakaoId);
}
