package com.dnd.jjakkak.domain.member.dto.response;

import com.dnd.jjakkak.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

/**
 * 회원 응답 DTO 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 30.
 */
@Getter
public class MemberResponseDto {
    private final long memberId;
    private final String memberNickname;
    private final long kakaoId;
    private final String memberProfile;

    @Builder
    public MemberResponseDto(Member member) {
        this.memberId = member.getMemberId();
        this.memberNickname = member.getMemberNickname();
        this.kakaoId = member.getKakaoId();
        this.memberProfile = member.getMemberProfile();
    }
}
