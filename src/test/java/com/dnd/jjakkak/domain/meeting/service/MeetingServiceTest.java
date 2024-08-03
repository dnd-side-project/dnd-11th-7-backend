package com.dnd.jjakkak.domain.meeting.service;

import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.category.repository.CategoryRepository;
import com.dnd.jjakkak.domain.meeting.MeetingDummy;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.exception.MeetingNotFoundException;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
import com.dnd.jjakkak.domain.meetingcategory.repository.MeetingCategoryRepository;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 모임 서비스 테스트 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 25.
 */
@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @InjectMocks
    MeetingService meetingService;

    @Mock
    MeetingRepository meetingRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    MeetingCategoryRepository meetingCategoryRepository;

    @Mock
    JwtProvider jwtProvider;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("모임 생성 테스트 - 성공")
    void testCreateMeeting() {

        // given
        MeetingCreateRequestDto actual = MeetingDummy.createRequestDto(List.of(1L, 2L));
        Category teamProject = Category.builder()
                .categoryName("팀플")
                .build();

        Category meeting = Category.builder()
                .categoryName("회의")
                .build();

        Member member = Member.builder()
                .kakaoId(1L)
                .memberNickname("seungjo")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(teamProject));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(meeting));
        when(jwtProvider.validate(anyString())).thenReturn("1");
        when(memberRepository.findByKakaoId(anyLong())).thenReturn(Optional.of(member));

        // when
        meetingService.createMeeting("access_token", actual);

        // then
        verify(meetingRepository, times(1)).save(any());
        verify(categoryRepository, times(2)).findById(anyLong());
        verify(meetingCategoryRepository, times(2)).save(any());

    }

    @Test
    @DisplayName("모임 조회 테스트 - 단건 (성공)")
    void testGetMeeting_Success() {

        // given
        Meeting meeting = Meeting.builder()
                .meetingName("DND 7조 회의")
                .meetingStartDate(LocalDate.of(2024, 7, 27))
                .meetingEndDate(LocalDate.of(2024, 7, 29))
                .numberOfPeople(6)
                .isAnonymous(false)
                .voteEndDate(LocalDateTime.of(2024, 7, 26, 23, 59, 59))
                .meetingLeaderId(1L)
                .meetingUuid("1234abcd")
                .build();

        when(meetingRepository.findByMeetingUuid(anyString())).thenReturn(Optional.of(meeting));

        // when
        MeetingResponseDto actual = meetingService.getMeetingByUuid("1234abcd");

        // then
        assertAll(
                () -> assertEquals(actual.getMeetingId(), meeting.getMeetingId()),
                () -> assertEquals(actual.getMeetingName(), meeting.getMeetingName()),
                () -> assertEquals(actual.getMeetingStartDate(), meeting.getMeetingStartDate()),
                () -> assertEquals(actual.getMeetingEndDate(), meeting.getMeetingEndDate()),
                () -> assertEquals(actual.getNumberOfPeople(), meeting.getNumberOfPeople()),
                () -> assertEquals(actual.getIsAnonymous(), meeting.getIsAnonymous()),
                () -> assertEquals(actual.getVoteEndDate(), meeting.getVoteEndDate()),
                () -> assertEquals(actual.getMeetingLeaderId(), meeting.getMeetingLeaderId()),
                () -> assertEquals(actual.getMeetingUuid(), meeting.getMeetingUuid())
        );

        verify(meetingRepository, times(1)).findByMeetingUuid(anyString());
    }

    @Test
    @DisplayName("모임 조회 테스트 - 단건 (실패)")
    void testGetMeeting_Fail() {
        // given
        when(meetingRepository.findByMeetingUuid(anyString())).thenReturn(Optional.empty());

        // expected
        assertThrows(MeetingNotFoundException.class,
                () -> meetingService.getMeetingByUuid("1234abcd"));

        verify(meetingRepository, times(1)).findByMeetingUuid(anyString());
    }


    @Test
    @DisplayName("모임 삭제 테스트 - 성공")
    void testDeleteMeeting_Success() {
        // given

        Meeting meeting = Meeting.builder()
                .meetingLeaderId(1L)
                .build();

        Member member = Member.builder()
                .kakaoId(1L)
                .memberNickname("seungjo")
                .build();

        ReflectionTestUtils.setField(member, "memberId", 1L);

        when(jwtProvider.validate(anyString())).thenReturn("1");
        when(memberRepository.findByKakaoId(anyLong())).thenReturn(Optional.of(member));
        when(meetingRepository.findById(anyLong())).thenReturn(Optional.of(meeting));

        // when
        meetingService.deleteMeeting("access_token", 1L);

        // then
        verify(meetingRepository, times(1)).deleteById(1L);
        verify(meetingCategoryRepository, times(1)).deleteByMeetingId(1L);
    }

    @Test
    @DisplayName("모임 삭제 테스트 - 실패 (존재하지 않는 모임)")
    void testDeleteMeeting_Fail() {
        // given
        Member member = Member.builder()
                .kakaoId(1L)
                .memberNickname("seungjo")
                .build();


        when(jwtProvider.validate(anyString())).thenReturn("1");
        when(memberRepository.findByKakaoId(anyLong())).thenReturn(Optional.of(member));

        // expected
        assertThrows(MeetingNotFoundException.class,
                () -> meetingService.deleteMeeting("invalid_token", 1L));
    }
}