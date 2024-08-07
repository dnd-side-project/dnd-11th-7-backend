package com.dnd.jjakkak.domain.category.controller;

import com.dnd.jjakkak.config.AbstractRestDocsTest;
import com.dnd.jjakkak.config.JjakkakMockUser;
import com.dnd.jjakkak.domain.category.CategoryDummy;
import com.dnd.jjakkak.domain.category.exception.CategoryNotFoundException;
import com.dnd.jjakkak.domain.category.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 카테고리 컨트롤러 테스트 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 24.
 */
@WebMvcTest(CategoryController.class)
@AutoConfigureRestDocs(uriHost = "43.202.65.170.nip.io", uriPort = 80)
class CategoryControllerTest extends AbstractRestDocsTest {

    @MockBean
    CategoryService categoryService;

    @Test
    @DisplayName("카테고리 전체 목록 조회")
    @JjakkakMockUser
    void get_list() throws Exception {

        // given
        when(categoryService.getCategoryList()).thenReturn(CategoryDummy.getCategoryResponseDtoList());

        // expected
        mockMvc.perform(get("/api/v1/categories"))
                .andExpectAll(
                        status().isOk(),

                        jsonPath("$[?(@.categoryName == '학교')]").exists(),
                        jsonPath("$[?(@.categoryName == '친구')]").exists(),
                        jsonPath("$[?(@.categoryName == '팀플')]").exists(),
                        jsonPath("$[?(@.categoryName == '회의')]").exists(),
                        jsonPath("$[?(@.categoryName == '스터디')]").exists(),
                        jsonPath("$[?(@.categoryName == '취미')]").exists(),
                        jsonPath("$[?(@.categoryName == '봉사')]").exists(),
                        jsonPath("$[?(@.categoryName == '기타')]").exists()
                )
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("[].categoryId").description("카테고리 아이디"),
                                fieldWithPath("[].categoryName").description("카테고리 이름")
                        ))
                );
    }

    @Test
    @DisplayName("카테고리 단건 조회 - 성공")
    @JjakkakMockUser
    void get_success() throws Exception {

        // given (1L, "학교")
        when(categoryService.getCategory(anyLong())).thenReturn(CategoryDummy.getCategoryResponseDto());


        // expected
        mockMvc.perform(get("/api/v1/categories/{id}", 1L))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.categoryId").value(1),
                        jsonPath("$.categoryName").value("학교")
                )
                .andDo(restDocs.document(
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
    @JjakkakMockUser
    void get_fail() throws Exception {

        // given
        when(categoryService.getCategory(anyLong()))
                .thenThrow(new CategoryNotFoundException());

        // expected
        mockMvc.perform(get("/api/v1/categories/{id}", 100L))
                .andExpect(status().isNotFound())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("id").description("카테고리 아이디")),
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation").description("유효성 검사 오류")
                        )
                ));
    }

}