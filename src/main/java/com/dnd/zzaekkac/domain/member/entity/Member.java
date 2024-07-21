package com.dnd.zzaekkac.domain.member.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * 회원 엔티티입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 21.
 */

@Entity
@Getter
@SQLDelete(sql = "update `Member` set `is_delete` = true where `member_id`=?")
@SQLRestriction("is_delete = FALSE")
@NoArgsConstructor
public class Member implements OAuth2User {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberId;

    @Column(nullable = false, name = "member_nickname", length = 30)
    private String memberNickname;

    @Column(nullable = false, name = "kakao_id")
    private long kakaoId;

    @Column(nullable = false, name = "is_delete")
    private boolean isDelete;

    @Column(name = "member_profile", length = 300)
    private String memberProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name="role")
    private Role role;

    /**
     * <li>MemberService에서 사용하기 위한 빌더</li>
     */

    @Builder
    protected Member(String memberNickname, long kakaoId, String memberProfile) {
        this.memberNickname = memberNickname;
        this.kakaoId = kakaoId;
        this.memberProfile = memberProfile;
        this.isDelete = false;
        this.role = Role.ROLE_USER;
    }

    /**
     * <li>사용하진 않으나 유저 정보 업데이터 시 사용할 메소드</li>
     */

    public void update(String memberNickname, String memberProfile){
        this.memberNickname = memberNickname;
        this.memberProfile = memberProfile;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return this.memberNickname;
    }
}
