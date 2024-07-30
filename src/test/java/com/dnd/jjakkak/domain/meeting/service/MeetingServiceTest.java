package com.dnd.jjakkak.domain.meeting.service;

import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.category.exception.CategoryNotFoundException;
import com.dnd.jjakkak.domain.category.repository.CategoryRepository;
import com.dnd.jjakkak.domain.meeting.MeetingDummy;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingUpdateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.exception.MeetingNotFoundException;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
import com.dnd.jjakkak.domain.meetingcategory.repository.MeetingCategoryRepository;
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

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(teamProject));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(meeting));

        // when
        meetingService.createMeeting(actual);

        // then
        verify(meetingRepository, times(1)).save(any());
        verify(categoryRepository, times(2)).findById(anyLong());
        verify(meetingCategoryRepository, times(2)).save(any());

    }

    @Test
    @DisplayName("모임 생성 테스트 - 실패 (카테고리 없음)")
    void testCreateMeeting_Fail() {

        // given
        MeetingCreateRequestDto actual = MeetingDummy.createRequestDto(List.of(1L, 2L));
        Category teamProject = Category.builder()
                .categoryName("팀플")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(teamProject));
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        // expected
        assertThrows(CategoryNotFoundException.class,
                () -> meetingService.createMeeting(actual));
    }

    @Test
    @DisplayName("모임 조회 테스트 - 전체")
    void testGetMeetingList() {

        // given
        List<Meeting> meetingList = MeetingDummy.createMeetingList();

        when(meetingRepository.findAll()).thenReturn(meetingList);

        // when
        List<MeetingResponseDto> actual = meetingService.getMeetingList();

        // then
        assertAll(
                () -> assertEquals(actual.size(), meetingList.size()),
                () -> assertEquals(actual.get(0).getMeetingId(), meetingList.get(0).getMeetingId()),
                () -> assertEquals(actual.get(0).getMeetingName(), meetingList.get(0).getMeetingName()),
                () -> assertEquals(actual.get(0).getMeetingStartDate(), meetingList.get(0).getMeetingStartDate()),
                () -> assertEquals(actual.get(0).getMeetingEndDate(), meetingList.get(0).getMeetingEndDate()),
                () -> assertEquals(actual.get(0).getNumberOfPeople(), meetingList.get(0).getNumberOfPeople()),
                () -> assertEquals(actual.get(0).getIsOnline(), meetingList.get(0).getIsOnline()),
                () -> assertEquals(actual.get(0).getIsAnonymous(), meetingList.get(0).getIsAnonymous()),
                () -> assertEquals(actual.get(0).getVoteEndDate(), meetingList.get(0).getVoteEndDate())
        );

        verify(meetingRepository, times(1)).findAll();
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
                .isOnline(true)
                .isAnonymous(false)
                .voteEndDate(LocalDateTime.of(2024, 7, 26, 23, 59, 59))
                .build();

        when(meetingRepository.findById(anyLong())).thenReturn(Optional.of(meeting));

        // when
        MeetingResponseDto actual = meetingService.getMeeting(1L);

        // then
        assertAll(
                () -> assertEquals(actual.getMeetingId(), meeting.getMeetingId()),
                () -> assertEquals(actual.getMeetingName(), meeting.getMeetingName()),
                () -> assertEquals(actual.getMeetingStartDate(), meeting.getMeetingStartDate()),
                () -> assertEquals(actual.getMeetingEndDate(), meeting.getMeetingEndDate()),
                () -> assertEquals(actual.getNumberOfPeople(), meeting.getNumberOfPeople()),
                () -> assertEquals(actual.getIsOnline(), meeting.getIsOnline()),
                () -> assertEquals(actual.getIsAnonymous(), meeting.getIsAnonymous()),
                () -> assertEquals(actual.getVoteEndDate(), meeting.getVoteEndDate())
        );

        verify(meetingRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("모임 조회 테스트 - 단건 (실패)")
    void testGetMeeting_Fail() {
        // given
        when(meetingRepository.findById(anyLong())).thenReturn(Optional.empty());

        // expected
        assertThrows(MeetingNotFoundException.class,
                () -> meetingService.getMeeting(1L));

        verify(meetingRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("모임 수정 테스트 - 성공")
    void testUpdateMeeting_Success() {

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

        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));

        Category hobby = Category.builder()
                .categoryName("취미")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(hobby));

        // when
        meetingService.updateMeeting(1L, MeetingDummy.updateRequestDto(1L));

        // then
        verify(meetingRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(meetingCategoryRepository, times(1)).deleteByMeetingId(1L);
        verify(meetingCategoryRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("모임 수정 테스트 - 실패 (모임 없음)")
    void testUpdateMeeting_Fail() {

        // given
        MeetingUpdateRequestDto meetingUpdateRequestDto = MeetingDummy.updateRequestDto(1L);
        when(meetingRepository.findById(anyLong())).thenReturn(Optional.empty());

        // expected
        assertThrows(MeetingNotFoundException.class,
                () -> meetingService.updateMeeting(1L, meetingUpdateRequestDto));
    }

    @Test
    @DisplayName("모임 삭제 테스트 - 성공")
    void testDeleteMeeting_Success() {
        // given
        when(meetingRepository.existsById(anyLong())).thenReturn(true);

        // when
        meetingService.deleteMeeting(1L);

        // then
        verify(meetingRepository, times(1)).existsById(1L);
        verify(meetingRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("모임 삭제 테스트 - 실패 (존재하지 않는 모임)")
    void testDeleteMeeting_Fail() {
        // given
        when(meetingRepository.existsById(anyLong())).thenReturn(false);

        // expected
        assertThrows(MeetingNotFoundException.class,
                () -> meetingService.deleteMeeting(1L));
    }
}