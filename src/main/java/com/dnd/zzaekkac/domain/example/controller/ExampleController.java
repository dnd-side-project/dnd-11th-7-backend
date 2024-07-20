package com.dnd.zzaekkac.domain.example.controller;

import com.dnd.zzaekkac.domain.example.dto.request.ExampleCreateRequestDto;
import com.dnd.zzaekkac.domain.example.dto.request.ExampleUpdateRequestDto;
import com.dnd.zzaekkac.domain.example.dto.response.ExampleResponse;
import com.dnd.zzaekkac.domain.example.service.ExampleService;
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
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Example 수정 메서드.
     *
     * @param id      Example ID
     * @param request ExampleUpdateRequestDto
     * @return status : 200 (OK)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Long id,
                                       @Valid @RequestBody ExampleUpdateRequestDto request) {
        exampleService.update(id, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Example 삭제 메서드.
     *
     * @param id Example ID
     * @return status : 200 (OK)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        exampleService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
