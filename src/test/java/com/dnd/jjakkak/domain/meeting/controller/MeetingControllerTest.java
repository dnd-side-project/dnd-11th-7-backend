package com.dnd.jjakkak.domain.meeting.controller;

import com.dnd.jjakkak.config.AbstractRestDocsTest;
import com.dnd.jjakkak.config.JjakkakMockUser;
import com.dnd.jjakkak.domain.meeting.MeetingDummy;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "43.203.239.67.nip.io", uriPort = 443)
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

        mockMvc.perform(post("/api/v1/meetings")
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
                                fieldWithPath("dueDateTime").description("일정 입력 마감 시간")
                                        .attributes(key("constraint").value("일정 입력 마감 시간은 모임 시작일 이전이어야 합니다.")),
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

        mockMvc.perform(post("/api/v1/meetings")
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
                                fieldWithPath("validation.dueDateTime").description("일정 입력 종료 시간은 필수 값입니다."),
                                fieldWithPath("validation.categoryIds").description("카테고리는 최소 1개 이상 8개 이하로 선택해주세요.")
                        )));

    }

    @Test
    @DisplayName("모임 정보 조회 - 성공")
    @JjakkakMockUser
    void getMeetingInfo_success() throws Exception {

        String meetingUuid = "123ABC";
        when(meetingService.getMeetingInfo(anyString()))
                .thenReturn(MeetingDummy.createInfoResponse());

        mockMvc.perform(get("/api/v1/meetings/{meetingUuid}/info", meetingUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.categoryNames.[0]").value("팀플"),
                        jsonPath("$.categoryNames.[1]").value("회의"),
                        jsonPath("$.meetingId").value(1),
                        jsonPath("$.meetingName").value("세븐일레븐"),
                        jsonPath("$.meetingStartDate").value("2024-08-27"),
                        jsonPath("$.meetingEndDate").value("2024-08-29"),
                        jsonPath("$.dueDateTime").value("2024-08-26T23:59:59"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID")
                        ),
                        responseFields(
                                fieldWithPath("categoryNames").description("카테고리 리스트"),
                                fieldWithPath("meetingId").description("모임 ID"),
                                fieldWithPath("meetingName").description("모임 이름"),
                                fieldWithPath("meetingStartDate").description("모임 시작 날짜"),
                                fieldWithPath("meetingEndDate").description("모임 종료 날짜"),
                                fieldWithPath("dueDateTime").description("일정 입력 마감 시간")
                        ))
                );
    }

    @Test
    @DisplayName("모임 최적 시간 조회 - 성공")
    @JjakkakMockUser
    void getBestTime_success() throws Exception {

        String meetingUuid = "123ABC";
        when(meetingService.getMeetingTimes(anyString(), any()))
                .thenReturn(MeetingDummy.createMeetingTimeResponseDto());

        mockMvc.perform(get("/api/v1/meetings/{meetingUuid}/times", meetingUuid)
                        .param("sort", "COUNT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.numberOfPeople").value(2),
                        jsonPath("$.isAnonymous").value(false),
                        jsonPath("$.meetingTimeList[0].memberNames.[0]").value("고래"),
                        jsonPath("$.meetingTimeList[0].memberNames.[1]").value("상어"),
                        jsonPath("$.meetingTimeList[0].startTime").value("2024-08-27T10:00:00"),
                        jsonPath("$.meetingTimeList[0].endTime").value("2024-08-27T12:00:00"),
                        jsonPath("$.meetingTimeList[0].rank").value(1.0))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID")
                        ),
                        responseFields(
                                fieldWithPath("numberOfPeople").description("총 인원 수"),
                                fieldWithPath("isAnonymous").description("익명 여부"),
                                fieldWithPath("meetingTimeList[].memberNames").description("멤버 이름 리스트"),
                                fieldWithPath("meetingTimeList[].startTime").description("시작 시간"),
                                fieldWithPath("meetingTimeList[].endTime").description("종료 시간"),
                                fieldWithPath("meetingTimeList[].rank").description("우선순위 (오름차순)")
                        ))
                );
    }

    @Test
    @DisplayName("모임 참석자 조회 - 성공")
    @JjakkakMockUser
    void getParticipants_success() throws Exception {

        String meetingUuid = "123ABC";
        when(meetingService.getParticipants(anyString()))
                .thenReturn(MeetingDummy.createParticipants());

        mockMvc.perform(get("/api/v1/meetings/{meetingUuid}/participants", meetingUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.numberOfPeople").value(2),
                        jsonPath("$.anonymousStatus").value(false),
                        jsonPath("$.participantInfoList.[0].nickname").value("고래"),
                        jsonPath("$.participantInfoList.[0].votedStatus").value(true),
                        jsonPath("$.participantInfoList.[0].leaderStatus").value(true),
                        jsonPath("$.participantInfoList.[1].nickname").value("상어"),
                        jsonPath("$.participantInfoList.[1].votedStatus").value(true),
                        jsonPath("$.participantInfoList.[1].leaderStatus").value(false))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingUuid").description("모임 UUID")
                        ),
                        responseFields(
                                fieldWithPath("numberOfPeople").description("참석자 수"),
                                fieldWithPath("anonymousStatus").description("익명 여부"),
                                fieldWithPath("participantInfoList.[].nickname").description("참석자 닉네임"),
                                fieldWithPath("participantInfoList.[].votedStatus").description("투표 여부"),
                                fieldWithPath("participantInfoList.[].leaderStatus").description("리더 여부")
                        ))
                );
    }


    @Test
    @DisplayName("모임 삭제 - 성공")
    @JjakkakMockUser
    void delete_success() throws Exception {

        // expected
        mockMvc.perform(delete("/api/v1/meetings/{meetingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("meetingId").description("모임 ID"))
                ));
    }
}