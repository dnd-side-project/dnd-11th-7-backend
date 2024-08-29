package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.config.AbstractRestDocsTest;
import com.dnd.jjakkak.config.JjakkakMockUser;
import com.dnd.jjakkak.domain.meeting.MeetingDummy;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingMyPageResponseDto;
import com.dnd.jjakkak.domain.member.dto.request.MemberUpdateNicknameRequestDto;
import com.dnd.jjakkak.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MemberController 테스트 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 08. 06
 */
@WebMvcTest(MemberController.class)
@AutoConfigureRestDocs(uriHost = "43.202.65.170.nip.io", uriPort = 80)
class MemberControllerTest extends AbstractRestDocsTest {

    @MockBean
    MemberService memberService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 모임 리스트 조회")
    @JjakkakMockUser
    void testGetMemberListByMemberId() throws Exception {

        List<MeetingMyPageResponseDto> response = MeetingDummy.createMeetingMyPageResponseDto();

        when(memberService.getMeetingListByMemberId(anyLong()))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/members/meetingList", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].categoryNames").exists(),
                        jsonPath("$[0].meetingId").value(1),
                        jsonPath("$[0].meetingUuid").value("123ABC"),
                        jsonPath("$[0].meetingName").value("세븐일레븐"),
                        jsonPath("$[0].meetingStartDate").exists(),
                        jsonPath("$[0].meetingEndDate").exists(),
                        jsonPath("$[0].voteEndDate").exists(),
                        jsonPath("$[0].numberOfPeople").value(6),
                        jsonPath("$[0].isAnonymous").value(false),
                        jsonPath("$[0].leaderName").value("승조")
                )
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("[].categoryNames").description("카테고리 이름 리스트"),
                                fieldWithPath("[].meetingId").description("모임 ID"),
                                fieldWithPath("[].meetingUuid").description("모임 UUID"),
                                fieldWithPath("[].meetingName").description("모임 이름"),
                                fieldWithPath("[].meetingStartDate").description("모임 시작일"),
                                fieldWithPath("[].meetingEndDate").description("모임 종료일"),
                                fieldWithPath("[].voteEndDate").description("투표 종료일"),
                                fieldWithPath("[].numberOfPeople").description("모임 인원"),
                                fieldWithPath("[].isAnonymous").description("익명 여부"),
                                fieldWithPath("[].leaderName").description("모임 리더 이름")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("회원 닉네임 업데이트")
    void testUpdateNickname() throws Exception {
        doNothing().when(memberService).updateNickname(anyLong(), any(MemberUpdateNicknameRequestDto.class));

        MemberUpdateNicknameRequestDto requestDto = new MemberUpdateNicknameRequestDto();
        ReflectionTestUtils.setField(requestDto, "memberNickname", "newName");

        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(patch("/api/v1/members/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("memberNickname").description("변경할 닉네임")
                        )
                ));
    }

    @Test
    @WithMockUser
    @DisplayName("회원 삭제")
    void testDeleteMember() throws Exception {
        doNothing().when(memberService).deleteMember(anyLong());

        mockMvc.perform(delete("/api/v1/members", 9L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
