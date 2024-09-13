package com.dnd.jjakkak.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 컨트롤러.
 *
 * @author 정승조
 * @version 2024. 07. 19.
 */
@Slf4j
@RestControllerAdvice
public class ExceptionController {

    /**
     * BindingResult 에러 처리 (invalid request)
     *
     * @param e MethodArgumentNotValidException
     * @return ErrorResponse (에러 응답 - code, message, validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(MethodArgumentNotValidException e) {
        ErrorResponse body = ErrorResponse.builder()
                .code("400")
                .message("잘못된 요청입니다.")
                .build();

        e.getFieldErrors()
                .forEach(fieldError
                        -> body.addValidation(fieldError.getField(), fieldError.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * 전역 예외 처리
     *
     * @param e GeneralException (모든 예외의 최상위 클래스)
     * @return ErrorResponse (에러 응답 - code, message, validation)
     */
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(GeneralException e) {
        ErrorResponse body = ErrorResponse.builder()
                .code(String.valueOf(e.getStatusCode()))
                .message(e.getMessage())
                .validation(e.getValidation())
                .build();

        return ResponseEntity.status(e.getStatusCode()).body(body);
    }

    /**
     * Exception 예외 처리 (Server Error)
     *
     * @param e Exception
     * @return ErrorResponse (에러 응답 - code, message)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        log.error("Server Error = {}", e.getMessage());

        ErrorResponse body = ErrorResponse.builder()
                .code("500")
                .message("서버에서 에러가 발생하였습니다. 조금 뒤에 다시 시도해주세요.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
