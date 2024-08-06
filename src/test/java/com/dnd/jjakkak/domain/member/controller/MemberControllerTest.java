package com.dnd.jjakkak.domain.member.controller;

import com.dnd.jjakkak.domain.jwt.filter.JwtAuthenticationFilter;
import com.dnd.jjakkak.domain.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.member.dto.request.MemberUpdateNicknameRequestDto;
import com.dnd.jjakkak.domain.member.dto.request.MemberUpdateProfileRequestDto;
import com.dnd.jjakkak.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MemberController 테스트 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 08. 06
 */

@ActiveProfiles("test")
@WebMvcTest(MemberController.class)
@AutoConfigureRestDocs(uriHost = "43.202.65.170.nip.io", uriPort = 80)
@AutoConfigureMockMvc
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(get("/**").with(csrf()))
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("회원 모임 리스트 조회")
    public void testGetMemberListByMemberId() throws Exception {
        Meeting meeting = Meeting.builder()
                .meetingName("Test Meeting")
                .meetingStartDate(LocalDate.now())
                .meetingEndDate(LocalDate.now().plusDays(1))
                .numberOfPeople(10)
                .isAnonymous(false)
                .voteEndDate(LocalDateTime.now().plusDays(1))
                .meetingLeaderId(1L)
                .meetingUuid("uuid")
                .build();

        MeetingResponseDto meetingResponseDto = MeetingResponseDto.builder()
                .meeting(meeting)
                .build();

        when(memberService.getMeetingListByMemberId(anyLong())).thenReturn(Collections.singletonList(meetingResponseDto));

        mockMvc.perform(get("/api/v1/member/{memberId}/meetingList", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].meetingName").value("Test Meeting"))
                .andExpect(jsonPath("$[0].meetingStartDate").exists())
                .andExpect(jsonPath("$[0].meetingEndDate").exists())
                .andExpect(jsonPath("$[0].numberOfPeople").value(10))
                .andExpect(jsonPath("$[0].isAnonymous").value(false))
                .andExpect(jsonPath("$[0].voteEndDate").exists())
                .andExpect(jsonPath("$[0].meetingLeaderId").value(1L))
                .andExpect(jsonPath("$[0].meetingUuid").value("uuid"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("회원 닉네임 업데이트")
    public void testUpdateNickname() throws Exception {
        doNothing().when(memberService).updateNickname(anyLong(), any(MemberUpdateNicknameRequestDto.class));

        mockMvc.perform(patch("/api/v1/member/{memberId}/nickname", 9L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberNickname\": \"newName\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("회원 프로필 업데이트")
    public void testUpdateProfile() throws Exception {
        doNothing().when(memberService).updateProfile(anyLong(), any(MemberUpdateProfileRequestDto.class));

        mockMvc.perform(patch("/api/v1/member/{memberId}/profile", 9L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberProfile\": \"http://newProfile\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("회원 삭제")
    public void testDeleteMember() throws Exception {
        doNothing().when(memberService).deleteMember(anyLong());

        mockMvc.perform(delete("/api/v1/member/{memberId}", 9L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
