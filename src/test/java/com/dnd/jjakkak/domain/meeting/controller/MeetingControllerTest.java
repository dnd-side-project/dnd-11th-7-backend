package com.dnd.jjakkak.domain.meeting.controller;

import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.category.repository.CategoryRepository;
import com.dnd.jjakkak.domain.meeting.MeetingDummy;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingUpdateRequestDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
import com.dnd.jjakkak.domain.meeting.service.MeetingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 모임 컨트롤러 테스트 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 25.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestDocs(uriHost = "43.202.65.170.nip.io", uriPort = 80)
@AutoConfigureMockMvc
class MeetingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MeetingService groupService;

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ObjectMapper objectMapper;

    Category school, friend, teamProject, session, study, hobby, volunteer, etc;


    @BeforeEach
    void setUp() {

        // 1.학교, 2.친구, 3.팀플, 4.회의, 5.스터디, 6.취미, 7.봉사, 8.기타
        school = Category.builder()
                .categoryName("학교")
                .build();

        friend = Category.builder()
                .categoryName("친구")
                .build();

        teamProject = Category.builder()
                .categoryName("팀플")
                .build();

        session = Category.builder()
                .categoryName("회의")
                .build();

        study = Category.builder()
                .categoryName("스터디")
                .build();

        hobby = Category.builder()
                .categoryName("취미")
                .build();

        volunteer = Category.builder()
                .categoryName("봉사")
                .build();

        etc = Category.builder()
                .categoryName("기타")
                .build();

        categoryRepository.saveAll(List.of(school, friend, teamProject, session, study, hobby, volunteer, etc));
    }


    @Test
    @DisplayName("모임 생성 테스트 - 성공")
    void testCreateMeeting_Success() throws Exception {

        // given
        MeetingCreateRequestDto requestDto = MeetingDummy.createRequestDto(
                List.of(teamProject.getCategoryId(), study.getCategoryId(), session.getCategoryId()));

        String json = objectMapper.writeValueAsString(requestDto);

        // expected
        mockMvc.perform(post("/api/v1/meeting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isCreated())
                .andDo(document("meeting/create/success",
                        preprocessRequest(prettyPrint()),
                        requestFields(
                                fieldWithPath("meetingName").description("모임 이름")
                                        .attributes(key("constraint").value("모임 이름은 1자 이상 10자 이하로 입력해주세요.")),
                                fieldWithPath("meetingStartDate").description("모임 시작 날짜")
                                        .attributes(key("constraint").value("모임 시작일은 종료일 이전이어야 합니다.")),
                                fieldWithPath("meetingEndDate").description("모임 종료 날짜")
                                        .attributes(key("constraint").value("모임 종료일은 시작일 이후이어야 합니다.")),
                                fieldWithPath("numberOfPeople").description("모임 인원")
                                        .attributes(key("constraint").value("모임 인원은 2명 이상 10명 이하로 설정해주세요.")),
                                fieldWithPath("isOnline").description("온라인 여부")
                                        .optional()
                                        .attributes(key("constraint").value("default = null")),
                                fieldWithPath("isAnonymous").description("익명 여부")
                                        .attributes(key("constraint").value("default = false (실명)")),
                                fieldWithPath("voteEndDate").description("투표 종료 날짜")
                                        .attributes(key("constraint").value("투표 종료일은 모임 시작일 이전이어야 합니다.")),
                                fieldWithPath("categoryIds").description("카테고리 아이디 목록")
                                        .attributes(key("constraint").value("1개 이상의 카테고리를 선택해주세요."))
                        )));
    }

    @Test
    @DisplayName("모임 생성 테스트 - 실패")
    void testCreateGroup_Fail() throws Exception {

        // given
        MeetingCreateRequestDto requestDto = MeetingDummy.createInvalidRequestDto();
        String json = objectMapper.writeValueAsString(requestDto);

        // expected
        mockMvc.perform(post("/api/v1/meeting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(document("meeting/create/fail",
                        preprocessResponse(prettyPrint()),
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
                        ))
                );
    }

    @Test
    @DisplayName("모임 조회 테스트 - 전체 조회")
    void testGetMeetingList() throws Exception {
        // given
        List<Meeting> meetingList = MeetingDummy.createMeetingList();
        meetingRepository.saveAll(meetingList);

        // expected
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/meeting")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("meeting/getList/success",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].meetingId").description("모임 아이디"),
                                fieldWithPath("[].meetingName").description("모임 이름"),
                                fieldWithPath("[].meetingStartDate").description("모임 시작 날짜"),
                                fieldWithPath("[].meetingEndDate").description("모임 종료 날짜"),
                                fieldWithPath("[].numberOfPeople").description("모임 인원"),
                                fieldWithPath("[].isOnline").description("온라인 여부"),
                                fieldWithPath("[].isAnonymous").description("익명 여부"),
                                fieldWithPath("[].voteEndDate").description("투표 종료 날짜")
                        )));
    }

    @Test
    @DisplayName("모임 조회 테스트 - 단건 조회 (성공)")
    void testGetMeeting_Success() throws Exception {
        // given
        Meeting meeting = Meeting.builder()
                .meetingName("DND 7조 회의")
                .meetingStartDate(LocalDate.of(2024, 7, 27))
                .meetingEndDate(LocalDate.of(2024, 7, 29))
                .numberOfPeople(6)
                .isOnline(true)
                .isAnonymous(false)
                .voteEndDate(LocalDateTime.of(2024, 7, 26, 23, 59, 59))
                .build();

        meetingRepository.save(meeting);

        // expected
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/meeting/{meetingId}", meeting.getMeetingId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.meetingId").value(meeting.getMeetingId()),
                        jsonPath("$.meetingName").value(meeting.getMeetingName()),
                        jsonPath("$.meetingStartDate").value(meeting.getMeetingStartDate().toString()),
                        jsonPath("$.meetingEndDate").value(meeting.getMeetingEndDate().toString()),
                        jsonPath("$.numberOfPeople").value(meeting.getNumberOfPeople()),
                        jsonPath("$.isOnline").value(meeting.getIsOnline()),
                        jsonPath("$.isAnonymous").value(meeting.getIsAnonymous()),
                        jsonPath("$.voteEndDate").value(meeting.getVoteEndDate().toString())
                )
                .andDo(document("meeting/get/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("meetingId").description("모임 아이디")),
                        responseFields(
                                fieldWithPath("meetingId").description("모임 아이디"),
                                fieldWithPath("meetingName").description("모임 이름"),
                                fieldWithPath("meetingStartDate").description("모임 시작 날짜"),
                                fieldWithPath("meetingEndDate").description("모임 종료 날짜"),
                                fieldWithPath("numberOfPeople").description("모임 인원"),
                                fieldWithPath("isOnline").description("온라인 여부"),
                                fieldWithPath("isAnonymous").description("익명 여부"),
                                fieldWithPath("voteEndDate").description("투표 종료 날짜")
                        )));
    }

    @Test
    @DisplayName("모임 조회 테스트 - 단건 조회 (실패)")
    void testGetMeeting_Fail() throws Exception {

        // expected
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/meeting/{meetingId}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("meeting/get/fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("meetingId").description("모임 아이디")),
                        responseFields(
                                fieldWithPath("code").description("상태 코드"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation").description("유효성 검사 오류 목록")
                        )));
    }

    @Test
    @DisplayName("모임 수정 테스트 - 성공")
    void testUpdateMeeting_Success() throws Exception {
        // given
        Meeting meeting = Meeting.builder()
                .meetingName("DND 7조 회의")
                .meetingStartDate(LocalDate.of(2024, 7, 27))
                .meetingEndDate(LocalDate.of(2024, 7, 29))
                .numberOfPeople(6)
                .isOnline(true)
                .isAnonymous(false)
                .voteEndDate(LocalDateTime.of(2024, 7, 26, 23, 59, 59))
                .build();

        Meeting saved = meetingRepository.save(meeting);

        MeetingUpdateRequestDto requestDto = MeetingDummy.updateRequestDto(session.getCategoryId());
        String json = objectMapper.writeValueAsString(requestDto);

        // expected
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/meeting/{meetingId}", saved.getMeetingId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(document("meeting/update/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("meetingId").description("모임 아이디")),
                        requestFields(
                                fieldWithPath("meetingName").description("모임 이름")
                                        .attributes(key("constraint").value("모임 이름은 1자 이상 10자 이하로 입력해주세요.")),
                                fieldWithPath("meetingStartDate").description("모임 시작 날짜")
                                        .attributes(key("constraint").value("모임 시작일은 종료일 이전이어야 합니다.")),
                                fieldWithPath("meetingEndDate").description("모임 종료 날짜")
                                        .attributes(key("constraint").value("모임 종료일은 시작일 이후이어야 합니다.")),
                                fieldWithPath("numberOfPeople").description("모임 인원")
                                        .attributes(key("constraint").value("모임 인원은 2명 이상 10명 이하로 설정해주세요.")),
                                fieldWithPath("isOnline").description("온라인 여부")
                                        .optional()
                                        .attributes(key("constraint").value("default = null")),
                                fieldWithPath("isAnonymous").description("익명 여부")
                                        .attributes(key("constraint").value("default = false (실명)")),
                                fieldWithPath("voteEndDate").description("투표 종료 날짜")
                                        .attributes(key("constraint").value("투표 종료일은 모임 시작일 이전이어야 합니다.")),
                                fieldWithPath("categoryIds").description("카테고리 아이디 목록")
                                        .attributes(key("constraint").value("1개 이상의 카테고리를 선택해주세요."))
                        )));
    }

    @Test
    @DisplayName("모임 수정 테스트 - 실패 (존재하지 않는 모임)")
    void testUpdateMeeting_Fail() throws Exception {

        // given
        MeetingUpdateRequestDto requestDto = MeetingDummy.updateRequestDto(1L, 2L);
        String json = objectMapper.writeValueAsString(requestDto);

        // expected
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/meeting/{meetingId}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("meeting/update/fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").description("상태 코드"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation").description("유효성 검사 오류 목록")
                        )));
    }

    @Test
    @DisplayName("모임 삭제 테스트 - 성공")
    void testDeleteMeeting_Success() throws Exception {
        // given
        Meeting meeting = Meeting.builder()
                .meetingName("DND 7조 회의")
                .meetingStartDate(LocalDate.of(2024, 7, 27))
                .meetingEndDate(LocalDate.of(2024, 7, 29))
                .numberOfPeople(6)
                .isOnline(true)
                .isAnonymous(false)
                .voteEndDate(LocalDateTime.of(2024, 7, 26, 23, 59, 59))
                .build();

        ReflectionTestUtils.setField(meeting, "meetingId", 1L);
        meetingRepository.save(meeting);

        // expected
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/meeting/{meetingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("meeting/delete/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("meetingId").description("모임 아이디"))));
    }

    @Test
    @DisplayName("모임 삭제 테스트 - 실패 (존재하지 않는 모임)")
    void testDeleteMeeting_Fail() throws Exception {

        // expected
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/meeting/{meetingId}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(document("meeting/delete/fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("meetingId").description("모임 아이디")),
                        responseFields(
                                fieldWithPath("code").description("상태 코드"),
                                fieldWithPath("message").description("에러 메시지"),
                                fieldWithPath("validation").description("유효성 검사 오류 목록"))
                ));
    }
}