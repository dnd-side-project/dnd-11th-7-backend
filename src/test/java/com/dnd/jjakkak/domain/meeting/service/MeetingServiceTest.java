package com.dnd.jjakkak.domain.meeting.service;

import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.category.repository.CategoryRepository;
import com.dnd.jjakkak.domain.meeting.MeetingDummy;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingInfoResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingParticipantResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingTimeResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.enums.MeetingSort;
import com.dnd.jjakkak.domain.meeting.exception.MeetingNotFoundException;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
import com.dnd.jjakkak.domain.meetingcategory.repository.MeetingCategoryRepository;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import com.dnd.jjakkak.domain.schedule.service.ScheduleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
    ScheduleService scheduleService;

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


        when(categoryRepository.findById(1L)).thenReturn(Optional.of(teamProject));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(meeting));

        // when
        meetingService.createMeeting(1L, actual);

        // then
        verify(meetingRepository, times(1)).save(any());
        verify(categoryRepository, times(2)).findById(anyLong());
        verify(meetingCategoryRepository, times(2)).save(any());
        verify(scheduleService, times(6)).createDefaultSchedule(any()); // 인원 수 = 6

    }


    @Test
    @DisplayName("모임 삭제 테스트 - 성공")
    void testDeleteMeeting_Success() {
        // given

        Meeting meeting = Meeting.builder()
                .meetingLeaderId(1L)
                .build();

        Member member = Member.builder()
                .memberNickname("seungjo")
                .build();
        ReflectionTestUtils.setField(member, "memberId", 1L);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(meetingRepository.findById(anyLong())).thenReturn(Optional.of(meeting));

        // when
        meetingService.deleteMeeting(1L, 1L);

        // then
        verify(meetingRepository, times(1)).deleteById(1L);
        verify(meetingCategoryRepository, times(1)).deleteByMeetingId(1L);
    }

    @Test
    @DisplayName("모임 삭제 테스트 - 실패 (존재하지 않는 모임)")
    void testDeleteMeeting_Fail() {

        Member member = Member.builder()
                .memberNickname("seungjo")
                .build();

        ReflectionTestUtils.setField(member, "memberId", 1L);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(meetingRepository.findById(anyLong())).thenReturn(Optional.empty());

        // expected
        assertThrows(MeetingNotFoundException.class,
                () -> meetingService.deleteMeeting(1L, 1L));
    }

    @Test
    @DisplayName("모임 정보 조회 - 성공")
    void testGetMeetingInfo_Success() {

        String uuid = "1234abcd";
        MeetingInfoResponseDto expected = MeetingDummy.createInfoResponse();

        when(meetingRepository.getMeetingInfo(anyString()))
                .thenReturn(expected);

        when(meetingRepository.existsByMeetingUuid(anyString()))
                .thenReturn(true);


        MeetingInfoResponseDto actual = meetingService.getMeetingInfo(uuid);

        assertAll(
                () -> assertEquals(expected.getMeetingId(), actual.getMeetingId()),
                () -> assertEquals(expected.getMeetingName(), actual.getMeetingName()),
                () -> assertEquals(expected.getMeetingStartDate(), actual.getMeetingStartDate()),
                () -> assertEquals(expected.getMeetingEndDate(), actual.getMeetingEndDate()),
                () -> assertEquals(expected.getCategoryNames(), actual.getCategoryNames())
        );

        verify(meetingRepository, times(1)).getMeetingInfo(uuid);
        verify(meetingRepository, times(1)).existsByMeetingUuid(uuid);

    }

    @Test
    @DisplayName("모임 정보 조회 - 실패 (존재하지 않는 모임)")
    void testGetMeetingInfo_Fail() {

        String uuid = "1234abcd";

        when(meetingRepository.existsByMeetingUuid(anyString()))
                .thenReturn(false);

        // expected
        assertThrows(MeetingNotFoundException.class,
                () -> meetingService.getMeetingInfo(uuid));
    }

    @Test
    @DisplayName("모임 시간 조회 - 성공")
    void testGetMeetingBestTime_Success() {

        String uuid = "1234abcd";
        List<MeetingTimeResponseDto> expected = MeetingDummy.createBestTimeResponse();

        when(meetingRepository.getMeetingTimes(anyString(), any()))
                .thenReturn(expected);

        when(meetingRepository.existsByMeetingUuid(anyString()))
                .thenReturn(true);

        List<MeetingTimeResponseDto> actual = meetingService.getMeetingTimes(uuid, MeetingSort.COUNT);

        assertEquals(expected.size(), actual.size());

        MeetingTimeResponseDto expectedTime = expected.get(0);
        MeetingTimeResponseDto actualTime = actual.get(0);

        assertAll(
                () -> assertEquals(expectedTime.getMemberNames(), actualTime.getMemberNames()),
                () -> assertEquals(expectedTime.getStartTime(), actualTime.getStartTime()),
                () -> assertEquals(expectedTime.getEndTime(), actualTime.getEndTime()),
                () -> assertEquals(expectedTime.getRank(), actualTime.getRank())
        );

        verify(meetingRepository, times(1)).getMeetingTimes(uuid, MeetingSort.COUNT);
        verify(meetingRepository, times(1)).existsByMeetingUuid(uuid);
    }

    @Test
    @DisplayName("모임 시간 조회 - 실패 (존재하지 않는 모임)")
    void testGetMeetingBestTime_Fail() {

        String uuid = "1234abcd";

        when(meetingRepository.existsByMeetingUuid(anyString()))
                .thenReturn(false);

        // expected
        assertThrows(MeetingNotFoundException.class,
                () -> meetingService.getMeetingTimes(uuid, MeetingSort.COUNT));
    }

    @Test
    @DisplayName("모임 참여자 조회 - 성공")
    void testGetMeetingParticipants_Success() {

        String uuid = "1234abcd";
        MeetingParticipantResponseDto expected = MeetingDummy.createParticipants();

        when(meetingRepository.getParticipant(anyString()))
                .thenReturn(expected);

        when(meetingRepository.existsByMeetingUuid(anyString()))
                .thenReturn(true);

        MeetingParticipantResponseDto actual = meetingService.getParticipants(uuid);

        assertAll(
                () -> assertEquals(expected.getNumberOfPeople(), actual.getNumberOfPeople()),
                () -> assertEquals(expected.isAnonymous(), actual.isAnonymous())
        );

        verify(meetingRepository, times(1)).getParticipant(uuid);
        verify(meetingRepository, times(1)).existsByMeetingUuid(uuid);
    }

    @Test
    @DisplayName("모임 참여자 조회 - 실패 (존재하지 않는 모임)")
    void testGetMeetingParticipants_Fail() {

        String uuid = "1234abcd";

        when(meetingRepository.existsByMeetingUuid(anyString()))
                .thenReturn(false);

        // expected
        assertThrows(MeetingNotFoundException.class,
                () -> meetingService.getParticipants(uuid));
    }
}