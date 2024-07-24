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

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getCategoryList() {
        return ResponseEntity.ok(categoryService.getCategoryList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

}
