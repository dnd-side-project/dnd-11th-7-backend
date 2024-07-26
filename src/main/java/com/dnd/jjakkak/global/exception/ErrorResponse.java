package com.dnd.jjakkak.global.exception;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Error Response Class.
 *
 * @author 정승조
 * @version 2024. 07. 19.
 */

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;
    private final Map<String, String> validation;

    @Builder
    public ErrorResponse(String code, String message, Map<String, String> validation) {
        this.code = code;
        this.message = message;
        this.validation = Objects.nonNull(validation) ? validation : new HashMap<>();
    }

    public void addValidation(String field, String errorMessage) {
        validation.put(field, errorMessage);
    }

}
