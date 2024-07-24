package com.dnd.jjakkak.domain.category.controller;

import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.category.repository.CategoryRepository;
import com.dnd.jjakkak.domain.category.service.CategoryService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 카테고리 컨트롤러 테스트 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 24.
 */

@SpringBootTest
@AutoConfigureRestDocs(uriHost = "43.202.65.170.nip.io", uriPort = 80)
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void clear() {
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("카테고리 전체 목록 조회")
    void testGetCategoryList() throws Exception {

        // given
        Category school = Category.builder()
                .categoryName("학교")
                .build();

        Category friend = Category.builder()
                .categoryName("친구")
                .build();

        Category meeting = Category.builder()
                .categoryName("회의")
                .build();

        categoryRepository.saveAll(List.of(school, friend, meeting));

        // expected
        mockMvc.perform(get("/api/v1/categories"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),

                        jsonPath("$[0].categoryName").value("학교"),
                        jsonPath("$[1].categoryName").value("친구"),
                        jsonPath("$[2].categoryName").value("회의")
                )
                .andDo(document("category/getCategoryList/success",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].categoryId").description("카테고리 아이디"),
                                fieldWithPath("[].categoryName").description("카테고리 이름")
                        )
                ));
    }

    @Test
    @DisplayName("카테고리 단건 조회 - 성공")
    void testGetCategory_Success() throws Exception {

        // given
        Category school = Category.builder()
                .categoryName("학교")
                .build();

        categoryRepository.save(school);

        // expected
        mockMvc.perform(get("/api/v1/categories/{id}", school.getCategoryId()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.categoryId").value(school.getCategoryId()),
                        jsonPath("$.categoryName").value("학교")
                )
                .andDo(document("category/getCategory/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("카테고리 아이디")),
                        responseFields(
                                fieldWithPath("categoryId").description("카테고리 아이디"),
                                fieldWithPath("categoryName").description("카테고리 이름")
                        )
                ));
    }

    @Test
    @DisplayName("카테고리 단건 조회 - 실패 (404)")
    void testGetCategory_Fail() throws Exception {

        // expected
        mockMvc.perform(get("/api/v1/categories/{id}", 100L))
                .andExpect(status().isNotFound())
                .andDo(document("category/getCategory/fail-404",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("카테고리 아이디")),
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation").description("상세 에러 메시지")
                        )
                ));
    }

}