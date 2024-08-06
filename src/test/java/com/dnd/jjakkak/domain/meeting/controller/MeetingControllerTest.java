package com.dnd.jjakkak.domain.meeting.controller;

import com.dnd.jjakkak.config.AbstractRestDocsTest;
import com.dnd.jjakkak.config.JjakkakMockUser;
import com.dnd.jjakkak.domain.meeting.MeetingDummy;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingConfirmRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.service.MeetingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 모임 컨트롤러 테스트입니다.
 *
 * @author 정승조
 * @version 2024. 08. 05.
 */
@WebMvcTest(MeetingController.class)
@AutoConfigureRestDocs(uriHost = "43.202.65.170.nip.io", uriPort = 80)
class MeetingControllerTest extends AbstractRestDocsTest {

    @MockBean
    MeetingService meetingService;

    @Autowired
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @Test
    @DisplayName("모임 생성 테스트 - 성공")
    @JjakkakMockUser
    void create_success() throws Exception {

        MeetingCreateRequestDto requestDto = MeetingDummy.createRequestDto(List.of(1L, 2L));
        String json = objectMapper.writeValueAsString(requestDto);

        String token = "Bearer access_token";

        mockMvc.perform(post("/api/v1/meeting")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("meetingName").description("모임 이름")
                                        .attributes(key("constraint").value("모임 이름은 1자 이상 10자 이하로 입력해주세요.")),
                                fieldWithPath("meetingStartDate").description("모임 시작 날짜")
                                        .attributes(key("constraint").value("모임 시작일은 종료일 이전이어야 합니다.")),
                                fieldWithPath("meetingEndDate").description("모임 종료 날짜")
                                        .attributes(key("constraint").value("모임 종료일은 시작일 이후이어야 합니다.")),
                                fieldWithPath("numberOfPeople").description("모임 인원")
                                        .attributes(key("constraint").value("모임 인원은 2명 이상 10명 이하로 설정해주세요.")),
                                fieldWithPath("isAnonymous").description("익명 여부")
                                        .attributes(key("constraint").value("default = false (실명)")),
                                fieldWithPath("voteEndDate").description("투표 종료 날짜")
                                        .attributes(key("constraint").value("투표 종료일은 모임 시작일 이전이어야 합니다.")),
                                fieldWithPath("categoryIds").description("카테고리 아이디 목록")
                                        .attributes(key("constraint").value("1개 이상의 카테고리를 선택해주세요."))
                        )));
    }

    @Test
    @DisplayName("모임 생성 테스트 - 실패 (Invalid)")
    @JjakkakMockUser
    void create_fail_invalid() throws Exception {
        MeetingCreateRequestDto requestDto = MeetingDummy.createInvalidRequestDto();
        String json = objectMapper.writeValueAsString(requestDto);

        String token = "Bearer access_token";

        mockMvc.perform(post("/api/v1/meeting")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("code").description("상태 코드"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation").description("유효성 검사 오류 목록"),
                                fieldWithPath("validation.meetingName").description("모임명은 필수 값입니다."),
                                fieldWithPath("validation.meetingStartDate").description("모임 일정 시작일은 필수 값입니다."),
                                fieldWithPath("validation.meetingEndDate").description("모임 일정 종료일은 필수 값입니다."),
                                fieldWithPath("validation.numberOfPeople").description("인원수는 필수 값입니다."),
                                fieldWithPath("validation.isAnonymous").description("익명 여부는 필수 값입니다."),
                                fieldWithPath("validation.voteEndDate").description("투표 종료일은 필수 값입니다."),
                                fieldWithPath("validation.categoryIds").description("카테고리는 최소 1개 이상 8개 이하로 선택해주세요.")
                        )));

    }

    @Test
    @DisplayName("모임 조회 테스트")
    @JjakkakMockUser
    void get_byUuid() throws Exception {

        // given
        String uuid = "1234ABCD";
        when(meetingService.getMeetingByUuid(anyString())).thenReturn(MeetingDummy.createResponseDto());

        // expected

        mockMvc.perform(get("/api/v1/meeting/{meetingUuid}", uuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.meetingId").value(1L),
                        jsonPath("$.meetingName").value("세븐일레븐"),
                        jsonPath("$.meetingStartDate").value("2024-07-27"),
                        jsonPath("$.meetingEndDate").value("2024-07-29"),
                        jsonPath("$.numberOfPeople").value(6),
                        jsonPath("$.isAnonymous").value(false),
                        jsonPath("$.voteEndDate").value("2024-07-26T23:59:59"),
                        jsonPath("$.confirmedSchedule").doesNotExist(), // null
                        jsonPath("$.meetingLeaderId").value(1L),
                        jsonPath("$.meetingUuid").value("1234ABCD")
                )
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID")),
                        responseFields(
                                fieldWithPath("meetingId").description("모임 ID"),
                                fieldWithPath("meetingName").description("모임 이름"),
                                fieldWithPath("meetingStartDate").description("모임 시작 날짜"),
                                fieldWithPath("meetingEndDate").description("모임 종료 날짜"),
                                fieldWithPath("numberOfPeople").description("모임 인원"),
                                fieldWithPath("isAnonymous").description("익명 여부"),
                                fieldWithPath("voteEndDate").description("투표 종료 날짜"),
                                fieldWithPath("confirmedSchedule").description("확정된 일정"),
                                fieldWithPath("meetingLeaderId").description("모임 리더 ID"),
                                fieldWithPath("meetingUuid").description("모임 UUID")
                        )));
    }

    @Test
    @JjakkakMockUser
    @DisplayName("모임 확정 일정 수정 테스트")
    void update_confirmedSchedule() throws Exception {

        // given
        MeetingConfirmRequestDto requestDto = new MeetingConfirmRequestDto();
        ReflectionTestUtils.setField(requestDto, "confirmedSchedule", LocalDateTime.of(2024, 7, 28, 12, 30));

        String json = objectMapper.writeValueAsString(requestDto);

        // expected
        mockMvc.perform(patch("/api/v1/meeting/{meetingId}/confirm", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingId").description("모임 ID")),
                        requestFields(
                                fieldWithPath("confirmedSchedule").description("확정된 일정")
                                        .attributes(key("constraint").value("확정된 일정은 시작일과 종료일 사이의 날짜여야 합니다.")))
                ));
    }

    @Test
    @DisplayName("모임 삭제 테스트")
    @JjakkakMockUser
    void delete_success() throws Exception {

        // expected
        mockMvc.perform(delete("/api/v1/meeting/{meetingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingId").description("모임 ID"))
                ));
    }
}