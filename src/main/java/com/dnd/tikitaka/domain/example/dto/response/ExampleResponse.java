package com.dnd.tikitaka.domain.example.dto.response;

import com.dnd.tikitaka.domain.example.entity.Example;
import lombok.Builder;
import lombok.Getter;

/**
 * Example Response Class.
 *
 * @author 정승조
 * @version 2024. 07. 17.
 */

@Getter
public class ExampleResponse {

    private final Long id;
    private final String name;
    private final String content;

    // 1번 방법
    public ExampleResponse(Example example) {
        this.id = example.getId();
        this.name = example.getName();
        this.content = example.getContent();
    }

    // 2번 방법
    @Builder
    public ExampleResponse(Long id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }
}
