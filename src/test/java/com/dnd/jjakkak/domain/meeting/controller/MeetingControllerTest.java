package com.dnd.jjakkak.domain.meeting.controller;

import com.dnd.jjakkak.config.JjakkakMockUser;
import com.dnd.jjakkak.domain.meeting.MeetingDummy;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.service.MeetingService;
import com.dnd.jjakkak.domain.member.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {class name}.
 *
 * @author 정승조
 * @version 2024. 08. 05.
 */
@AutoConfigureRestDocs
@WebMvcTest(MeetingController.class)
class MeetingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MeetingService meetingService;

    @MockBean
    JwtProvider jwtProvider;

    @MockBean
    MemberRepository memberRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(post("/**").with(csrf()))
                .build();

    }

    @Test
    @DisplayName("모임 생성 테스트 - 성공")
    @JjakkakMockUser
    void 모임생성_성공() throws Exception {

        MeetingCreateRequestDto requestDto = MeetingDummy.createRequestDto(List.of(1L, 2L));

        objectMapper.writeValueAsString(requestDto);

        String token = "Bearer access_token";

        mockMvc.perform(post("/api/v1/meeting")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(status().isCreated())
                .andDo(print());
    }

}