package com.dnd.tikitaka.domain.example.controller;

import com.dnd.tikitaka.domain.example.dto.request.ExampleCreateRequestDto;
import com.dnd.tikitaka.domain.example.dto.response.ExampleResponse;
import com.dnd.tikitaka.domain.example.service.ExampleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Example Controller Class.
 *
 * @author 정승조
 * @version 2024. 07. 17.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/example")
public class ExampleController {

    private final ExampleService exampleService;

    /**
     * Example List 조회 메서드.
     *
     * @return status : 200 (Ok), body : Example List
     */
    @GetMapping
    public ResponseEntity<List<ExampleResponse>> getList() {
        //TODO: Paging 처리에 대해 고민해보기
        List<ExampleResponse> responseList = exampleService.getList();
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    /**
     * Example 저장 메서드.
     *
     * @param request ExampleCreateRequestDto
     * @return status : 201 (CREATED)
     */
    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody ExampleCreateRequestDto request) {
        exampleService.save(request);

        // TODO: 응답 상태 코드 -> 201, 200?
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
