package com.dnd.jjakkak.domain.member.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

/**
 * 회원의 프로필 업데이트 요청 DTO입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 29.
 */

@Getter
public class MemberUpdateProfileRequestDto {
    @Pattern(regexp = "^(http|https)://.*$", message = "유효한 URL 형식이어야 합니다.")
    private String memberProfile;
}
