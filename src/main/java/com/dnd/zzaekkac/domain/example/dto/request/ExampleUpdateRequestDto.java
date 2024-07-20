package com.dnd.zzaekkac.domain.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * Example Update Request Dto.
 *
 * @author 정승조
 * @version 2024. 07. 19.
 */

@Getter
public class ExampleUpdateRequestDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;
}
