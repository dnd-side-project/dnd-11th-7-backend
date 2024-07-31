package com.dnd.jjakkak.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 회원의 닉네임 업데이트 요청 DTO입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 29.
 */


@Getter
public class MemberUpdateNicknameRequestDto {
    @Size(min = 2, max = 10, message = "닉네임은 최소 2자 이상 10자 이하로 작성해주세요.")
    @NotNull(message = "닉네임은 필수로 작성하셔야 합니다.")
    private String memberNickname;
}
