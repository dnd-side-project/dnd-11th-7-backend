package com.dnd.tikitaka.domain.example.service;

import com.dnd.tikitaka.domain.example.dto.request.ExampleCreateRequestDto;
import com.dnd.tikitaka.domain.example.dto.response.ExampleResponse;
import com.dnd.tikitaka.domain.example.entity.Example;
import com.dnd.tikitaka.domain.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Example Service Class.
 *
 * @author 정승조
 * @version 2024. 07. 17.
 */
@Service
@RequiredArgsConstructor
public class ExampleService {

    private final ExampleRepository exampleRepository;


    /**
     * Example List 조회 메서드.
     *
     * @return ExampleResponse List
     */
    @Transactional(readOnly = true)
    public List<ExampleResponse> getList() {

        // TODO : DTO 변환을 어떻게 할 것인가?

        // 1번 방법 - 생성자에 Entity를 넣어서 변환하자
        List<ExampleResponse> constructor = exampleRepository.findAll().stream()
                .map(ExampleResponse::new)
                .collect(Collectors.toList());

        // 2번 방법 - Builder를 사용해서 변환하자
        List<ExampleResponse> builder = exampleRepository.findAll().stream()
                .map(example -> ExampleResponse.builder()
                        .id(example.getId())
                        .name(example.getName())
                        .content(example.getContent())
                        .build())
                .collect(Collectors.toList());

        return constructor;
    }

    /**
     * Example 저장 메서드.
     *
     * @param request ExampleCreateRequestDto
     */
    @Transactional
    public void save(ExampleCreateRequestDto request) {

        Example example = Example.builder()
                .name(request.getName())
                .content(request.getContent())
                .build();

        exampleRepository.save(example);

        // TODO: return -> ID 값을 넘겨줄 것인가?
    }
}
