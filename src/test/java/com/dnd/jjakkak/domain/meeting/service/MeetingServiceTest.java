package com.dnd.jjakkak.domain.meeting.service;

import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.category.repository.CategoryRepository;
import com.dnd.jjakkak.domain.meeting.MeetingDummy;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
import com.dnd.jjakkak.domain.meetingcategory.repository.MeetingCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
    MeetingCategoryRepository groupCategoryRepository;

    @Test
    @DisplayName("모임 생성 테스트")
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
        verify(groupCategoryRepository, times(2)).save(any());

    }

}