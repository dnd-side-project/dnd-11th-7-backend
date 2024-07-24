package com.dnd.zzaekkac.domain.category.controller;

import com.dnd.zzaekkac.domain.category.dto.response.CategoryResponseDto;
import com.dnd.zzaekkac.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 카테고리 컨트롤러 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 24.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 카테고리의 전체 목록을 조회하는 메서드입니다.
     *
     * @return 200 (OK), Body: 카테고리 응답 DTO 리스트
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getCategoryList() {
        return ResponseEntity.ok(categoryService.getCategoryList());
    }

    /**
     * 카테고리의 ID로 단건 조회하는 메서드입니다.
     *
     * @param id 카테고리 ID
     * @return 200 (OK), Body: 카테고리 응답 DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

}
