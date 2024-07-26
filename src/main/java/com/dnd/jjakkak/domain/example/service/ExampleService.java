package com.dnd.jjakkak.domain.example.service;

import com.dnd.jjakkak.domain.example.dto.request.ExampleCreateRequestDto;
import com.dnd.jjakkak.domain.example.dto.request.ExampleUpdateRequestDto;
import com.dnd.jjakkak.domain.example.dto.response.ExampleResponse;
import com.dnd.jjakkak.domain.example.entity.Example;
import com.dnd.jjakkak.domain.example.exception.ExampleNotFoundException;
import com.dnd.jjakkak.domain.example.repository.ExampleRepository;
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

        return exampleRepository.findAll().stream()
                .map(example -> ExampleResponse.builder()
                        .id(example.getId())
                        .name(example.getName())
                        .content(example.getContent())
                        .build())
                .collect(Collectors.toList());
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
    }

    /**
     * Example 수정 메서드.
     *
     * @param id      Example ID
     * @param request ExampleUpdateRequestDto
     */
    @Transactional
    public void update(Long id, ExampleUpdateRequestDto request) {

        Example example = exampleRepository.findById(id)
                .orElseThrow(ExampleNotFoundException::new);

        // dirty checking 사용하여 update
        example.update(request.getName(), request.getContent());
    }

    /**
     * Example 삭제 메서드.
     * @param id Example ID
     */
    @Transactional
    public void delete(Long id) {

        if (!exampleRepository.existsById(id)) {
            throw new ExampleNotFoundException();
        }

        exampleRepository.deleteById(id);
    }
}
