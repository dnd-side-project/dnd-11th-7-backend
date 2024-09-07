package com.dnd.jjakkak.domain.schedule.controller;

import com.dnd.jjakkak.config.AbstractRestDocsTest;
import com.dnd.jjakkak.config.JjakkakMockUser;
import com.dnd.jjakkak.domain.schedule.ScheduleDummy;
import com.dnd.jjakkak.domain.schedule.service.ScheduleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 일정 컨트롤러 테스트입니다.
 *
 * @author 정승조
 * @version 2024. 09. 06.
 */
@WebMvcTest(ScheduleController.class)
@AutoConfigureRestDocs(uriHost = "43.202.65.170.nip.io", uriPort = 80)
class ScheduleControllerTest extends AbstractRestDocsTest {

    private static final String URI = "/api/v1/meetings/{meetingUuid}/schedules";
    private static final String MEETING_UUID = "met123";

    @MockBean
    ScheduleService scheduleService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 일정 할당 - 실패 (일정 없이 요청)")
    @JjakkakMockUser
    void member_schedule_assign_fail() throws Exception {

        // given
        String json = objectMapper.writeValueAsString(ScheduleDummy.invalidAssignRequestDto());

        // expected
        mockMvc.perform(patch(URI + "/members", MEETING_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원의 일정 할당 - 성공")
    @JjakkakMockUser
    void member_schedule_assign_success() throws Exception {

        // given
        String json = objectMapper.writeValueAsString(ScheduleDummy.assignRequestDto());

        // expected
        mockMvc.perform(patch(URI + "/members", MEETING_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("dateOfScheduleList").description("일정 목록"),
                                fieldWithPath("dateOfScheduleList[].startTime").description("일정 시작 시간"),
                                fieldWithPath("dateOfScheduleList[].endTime").description("일정 종료 시간"),
                                fieldWithPath("dateOfScheduleList[].rank").description("일정 우선순위"),
                                fieldWithPath("nickName").description("닉네임")
                        )
                ));
    }

    @Test
    @DisplayName("비회원 일정 할당 - 실패 (일정 없이 요청)")
    @WithMockUser(roles = "GUEST")
    void guest_schedule_assign_fail() throws Exception {

        // given
        String json = objectMapper.writeValueAsString(ScheduleDummy.invalidAssignRequestDto());

        // expected
        mockMvc.perform(patch(URI + "/guests", MEETING_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비회원의 일정 할당 - 성공")
    @WithMockUser(roles = "GUEST")
    void guest_schedule_assign_success() throws Exception {

        // given
        String json = objectMapper.writeValueAsString(ScheduleDummy.assignRequestDto());

        // expected
        mockMvc.perform(patch(URI + "/guests", MEETING_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("dateOfScheduleList").description("일정 목록"),
                                fieldWithPath("dateOfScheduleList[].startTime").description("일정 시작 시간"),
                                fieldWithPath("dateOfScheduleList[].endTime").description("일정 종료 시간"),
                                fieldWithPath("dateOfScheduleList[].rank").description("일정 우선순위"),
                                fieldWithPath("nickName").description("닉네임")
                        )
                ));
    }

}