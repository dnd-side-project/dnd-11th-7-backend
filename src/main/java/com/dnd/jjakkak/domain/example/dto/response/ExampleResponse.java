package com.dnd.jjakkak.domain.example.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Example Response Class.
 *
 * @author 정승조
 * @version 2024. 07. 17.
 */

@Getter
@ToString
public class ExampleResponse {

    private final Long id;
    private final String name;
    private final String content;

    @Builder
    public ExampleResponse(Long id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }
}
