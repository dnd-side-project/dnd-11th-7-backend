package com.dnd.tikitaka.domain.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

/**
 * Example Create Request Dto.
 *
 * @author 정승조
 * @version 2024. 07. 17.
 */
@Getter
public class ExampleCreateRequestDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private final String name;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private final String content;

    @Builder
    public ExampleCreateRequestDto(String name, String content) {
        this.name = name;
        this.content = content;
    }
}
