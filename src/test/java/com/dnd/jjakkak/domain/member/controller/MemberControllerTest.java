package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.config.AbstractRestDocsTest;
import com.dnd.jjakkak.config.JjakkakMockUser;
import com.dnd.jjakkak.domain.meeting.MeetingDummy;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingMyPageResponseDto;
import com.dnd.jjakkak.domain.member.dto.request.MemberUpdateNicknameRequestDto;
import com.dnd.jjakkak.domain.member.dto.request.MemberUpdateProfileRequestDto;
import com.dnd.jjakkak.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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

    @Test
    @DisplayName("회원 모임 리스트 조회")
    @JjakkakMockUser
    void testGetMemberListByMemberId() throws Exception {

        List<MeetingMyPageResponseDto> response = MeetingDummy.createMeetingMyPageResponseDto();

        Mockito.when(memberService.getMeetingListByMemberId(anyLong()))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/member/{memberId}/meetingList", 1L)
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
                        pathParameters(
                                parameterWithName("memberId").description("조회할 멤버 ID")),
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

        mockMvc.perform(patch("/api/v1/member/{memberId}/nickname", 9L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberNickname\": \"newName\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("회원 프로필 업데이트")
    void testUpdateProfile() throws Exception {
        doNothing().when(memberService).updateProfile(anyLong(), any(MemberUpdateProfileRequestDto.class));

        mockMvc.perform(patch("/api/v1/member/{memberId}/profile", 9L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberProfile\": \"http://newProfile\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("회원 삭제")
    void testDeleteMember() throws Exception {
        doNothing().when(memberService).deleteMember(anyLong());

        mockMvc.perform(delete("/api/v1/member/{memberId}", 9L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
