package com.dnd.jjakkak.domain.schedule.controller;

import com.dnd.jjakkak.config.AbstractRestDocsTest;
import com.dnd.jjakkak.config.JjakkakMockUser;
import com.dnd.jjakkak.domain.meeting.exception.MeetingNotFoundException;
import com.dnd.jjakkak.domain.schedule.ScheduleDummy;
import com.dnd.jjakkak.domain.schedule.service.ScheduleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private static final String SCHEDULE_UUID = "sch123";

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
        mockMvc.perform(post(URI + "/members", MEETING_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation").description("유효성 검사"),
                                fieldWithPath("validation.dateOfScheduleList").description("일정 날짜 에러 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("회원의 일정 할당 - 성공")
    @JjakkakMockUser
    void member_schedule_assign_success() throws Exception {

        // given
        String json = objectMapper.writeValueAsString(ScheduleDummy.assignRequestDto());

        // expected
        mockMvc.perform(post(URI + "/members", MEETING_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID")
                        ),
                        requestFields(
                                fieldWithPath("dateOfScheduleList").description("일정 목록"),
                                fieldWithPath("dateOfScheduleList[].startTime").description("일정 시작 시간"),
                                fieldWithPath("dateOfScheduleList[].endTime").description("일정 종료 시간"),
                                fieldWithPath("dateOfScheduleList[].rank").description("일정 우선순위"),
                                fieldWithPath("nickname").description("닉네임")
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
        mockMvc.perform(post(URI + "/guests", MEETING_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation").description("유효성 검사"),
                                fieldWithPath("validation.dateOfScheduleList").description("일정 날짜 에러 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("비회원의 일정 할당 - 성공")
    @WithMockUser(roles = "GUEST")
    void guest_schedule_assign_success() throws Exception {

        // given
        String json = objectMapper.writeValueAsString(ScheduleDummy.assignRequestDto());

        // expected
        mockMvc.perform(post(URI + "/guests", MEETING_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID")
                        ),
                        requestFields(
                                fieldWithPath("dateOfScheduleList").description("일정 목록"),
                                fieldWithPath("dateOfScheduleList[].startTime").description("일정 시작 시간"),
                                fieldWithPath("dateOfScheduleList[].endTime").description("일정 종료 시간"),
                                fieldWithPath("dateOfScheduleList[].rank").description("일정 우선순위"),
                                fieldWithPath("nickname").description("닉네임")
                        )
                ));
    }

    @Test
    @DisplayName("회원 일정 조회 - 실패 (해당 모임에 일정이 없는 경우)")
    @JjakkakMockUser
    void member_schedule_get_fail() throws Exception {

        // given
        Mockito.when(scheduleService.getMemberSchedule(Mockito.anyString(), Mockito.anyLong()))
                .thenThrow(new MeetingNotFoundException());

        // expected
        mockMvc.perform(get(URI + "/members", MEETING_UUID))
                .andExpect(status().isNotFound())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation").description("유효성 검사")
                        )
                ));
    }

    @Test
    @DisplayName("회원의 일정 조회 - 성공")
    @JjakkakMockUser
    void member_schedule_get_success() throws Exception {

        // given
        Mockito.when(scheduleService.getMemberSchedule(Mockito.anyString(), Mockito.anyLong()))
                .thenReturn(ScheduleDummy.scheduleResponseDto());

        // expected
        mockMvc.perform(get(URI + "/members", MEETING_UUID))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("scheduleNickname").value("유저"),
                        jsonPath("scheduleUuid").value("sch123"),
                        jsonPath("meetingStartDate").value("2024-09-06"),
                        jsonPath("meetingEndDate").value("2024-09-07"),
                        jsonPath("dateOfScheduleList.[0].startTime").value("2024-09-06T09:00:00"),
                        jsonPath("dateOfScheduleList.[0].endTime").value("2024-09-06T12:00:00"),
                        jsonPath("dateOfScheduleList.[1].startTime").value("2024-09-07T12:00:00"),
                        jsonPath("dateOfScheduleList.[1].endTime").value("2024-09-07T15:00:00")
                )
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID")
                        ),
                        responseFields(
                                fieldWithPath("scheduleNickname").description("일정을 등록한 유저 닉네임"),
                                fieldWithPath("scheduleUuid").description("일정 UUID"),
                                fieldWithPath("meetingStartDate").description("모임 시작 날짜"),
                                fieldWithPath("meetingEndDate").description("모임 종료 날짜"),
                                fieldWithPath("dateOfScheduleList").description("일정 목록"),
                                fieldWithPath("dateOfScheduleList.[].startTime").description("일정 시작 시간"),
                                fieldWithPath("dateOfScheduleList.[].endTime").description("일정 종료 시간")
                        )
                ));
    }

    @Test
    @DisplayName("비회원 일정 조회 - 실패 (해당 모임에 일정이 없는 경우)")
    @WithMockUser(roles = "GUEST")
    void guest_schedule_get_fail() throws Exception {

        // given
        Mockito.when(scheduleService.getGuestSchedule(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new MeetingNotFoundException());

        // expected
        mockMvc.perform(get(URI + "/guests/{scheduleUuid}", MEETING_UUID, SCHEDULE_UUID))
                .andExpect(status().isNotFound())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID"),
                                parameterWithName("scheduleUuid").description("일정 UUID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation").description("유효성 검사")
                        )
                ));
    }

    @Test
    @DisplayName("비회원 일정 조회 - 성공")
    @WithMockUser(roles = "GUEST")
    void guest_schedule_get_success() throws Exception {

        // given
        Mockito.when(scheduleService.getGuestSchedule(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(ScheduleDummy.scheduleResponseDto());

        // expected
        mockMvc.perform(get(URI + "/guests/{scheduleUuid}", MEETING_UUID, SCHEDULE_UUID))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.scheduleNickname").value("유저"),
                        jsonPath("$.scheduleUuid").value("sch123"),
                        jsonPath("$.meetingStartDate").value("2024-09-06"),
                        jsonPath("$.meetingEndDate").value("2024-09-07"),
                        jsonPath("$.dateOfScheduleList.[0].startTime").value("2024-09-06T09:00:00"),
                        jsonPath("$.dateOfScheduleList.[0].endTime").value("2024-09-06T12:00:00"),
                        jsonPath("$.dateOfScheduleList.[1].startTime").value("2024-09-07T12:00:00"),
                        jsonPath("$.dateOfScheduleList.[1].endTime").value("2024-09-07T15:00:00")
                )
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID"),
                                parameterWithName("scheduleUuid").description("일정 UUID")
                        ),
                        responseFields(
                                fieldWithPath("scheduleNickname").description("일정을 등록한 유저 닉네임"),
                                fieldWithPath("scheduleUuid").description("일정 UUID"),
                                fieldWithPath("meetingStartDate").description("모임 시작 날짜"),
                                fieldWithPath("meetingEndDate").description("모임 종료 날짜"),
                                fieldWithPath("dateOfScheduleList").description("일정 목록"),
                                fieldWithPath("dateOfScheduleList.[].startTime").description("일정 시작 시간"),
                                fieldWithPath("dateOfScheduleList.[].endTime").description("일정 종료 시간")
                        )
                ));
    }

    @Test
    @DisplayName("비회원 일정 수정 - 실패 (일정 없이 요청)")
    @WithMockUser(roles = "GUEST")
    void update_guest_schedule_fail() throws Exception {

        // given
        String json = objectMapper.writeValueAsString(ScheduleDummy.invalidUpdateRequestDto());

        // expected
        mockMvc.perform(patch(URI + "/guests/{scheduleUuid}", MEETING_UUID, SCHEDULE_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID"),
                                parameterWithName("scheduleUuid").description("일정 UUID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation").description("유효성 검사"),
                                fieldWithPath("validation.dateOfScheduleList").description("일정 날짜 에러 메시지")
                        )
                ));
    }


    @Test
    @DisplayName("비회원 일정 수정 - 성공")
    @WithMockUser(roles = "GUEST")
    void update_guest_schedule_success() throws Exception {

        // given
        String json = objectMapper.writeValueAsString(ScheduleDummy.updateRequestDto());

        // expected
        mockMvc.perform(patch(URI + "/guests/{scheduleUuid}", MEETING_UUID, SCHEDULE_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID"),
                                parameterWithName("scheduleUuid").description("일정 UUID")
                        ),
                        requestFields(
                                fieldWithPath("dateOfScheduleList").description("일정 목록"),
                                fieldWithPath("dateOfScheduleList[].startTime").description("일정 시작 시간"),
                                fieldWithPath("dateOfScheduleList[].endTime").description("일정 종료 시간"),
                                fieldWithPath("dateOfScheduleList[].rank").description("일정 우선순위")
                        )
                ));
    }

    @Test
    @DisplayName("회원 일정 수정 - 실패 (일정 없이 요청)")
    @JjakkakMockUser
    void update_member_schedule_fail() throws Exception {

        // given
        String json = objectMapper.writeValueAsString(ScheduleDummy.invalidUpdateRequestDto());

        // expected
        mockMvc.perform(patch(URI + "/members", MEETING_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation").description("유효성 검사"),
                                fieldWithPath("validation.dateOfScheduleList").description("일정 날짜 에러 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("회원 일정 수정 - 성공")
    @JjakkakMockUser
    void update_member_schedule_success() throws Exception {

        // given
        String json = objectMapper.writeValueAsString(ScheduleDummy.updateRequestDto());

        // expected
        mockMvc.perform(patch(URI + "/members", MEETING_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID")
                        ),
                        requestFields(
                                fieldWithPath("dateOfScheduleList").description("일정 목록"),
                                fieldWithPath("dateOfScheduleList[].startTime").description("일정 시작 시간"),
                                fieldWithPath("dateOfScheduleList[].endTime").description("일정 종료 시간"),
                                fieldWithPath("dateOfScheduleList[].rank").description("일정 우선순위")
                        )
                ));
    }
}