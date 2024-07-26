package com.dnd.jjakkak.domain.meeting.service;

import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.category.exception.CategoryNotFoundException;
import com.dnd.jjakkak.domain.category.repository.CategoryRepository;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.exception.MeetingNotFoundException;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
import com.dnd.jjakkak.domain.meetingcategory.entity.MeetingCategory;
import com.dnd.jjakkak.domain.meetingcategory.repository.MeetingCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 모임 서비스 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 25.
 */
@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingCategoryRepository meetingCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void createMeeting(MeetingCreateRequestDto requestDto) {

        // requestDto 객체의 checkmeetingDate 메서드를 호출하여 유효성 검사를 진행합니다.
        requestDto.checkMeetingDate();

        // 모임 생성 로직
        Meeting meeting = Meeting.builder()
                .meetingName(requestDto.getMeetingName())
                .meetingStartDate(requestDto.getMeetingStartDate())
                .meetingEndDate(requestDto.getMeetingEndDate())
                .numberOfPeople(requestDto.getNumberOfPeople())
                .isOnline(requestDto.getIsOnline())
                .isAnonymous(requestDto.getIsAnonymous())
                .voteEndDate(requestDto.getVoteEndDate())
                .build();

        meetingRepository.save(meeting);

        // 모임의 카테고리 값도 넣어줘야 함.
        for (Long categoryId : requestDto.getCategoryIds()) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(CategoryNotFoundException::new);

            MeetingCategory.Pk pk = new MeetingCategory.Pk(meeting.getMeetingId(), category.getCategoryId());
            MeetingCategory meetingCategory = MeetingCategory.builder()
                    .pk(pk)
                    .meeting(meeting)
                    .category(category)
                    .build();

            meetingCategoryRepository.save(meetingCategory);
        }
    }

    /**
     * 전체 모임 목록을 조회하는 메서드입니다.
     *
     * @return 모임 목록
     */
    @Transactional(readOnly = true)
    public List<MeetingResponseDto> getMeetingList() {

        List<MeetingResponseDto> meetingResponseDtoList = new ArrayList<>();
        List<Meeting> meetingList = meetingRepository.findAll();
        for (Meeting meeting : meetingList) {
            MeetingResponseDto meetingResponseDto = MeetingResponseDto.builder()
                    .meetingId(meeting.getMeetingId())
                    .meetingName(meeting.getMeetingName())
                    .meetingStartDate(meeting.getMeetingStartDate())
                    .meetingEndDate(meeting.getMeetingEndDate())
                    .numberOfPeople(meeting.getNumberOfPeople())
                    .isOnline(meeting.getIsOnline())
                    .isAnonymous(meeting.getIsAnonymous())
                    .voteEndDate(meeting.getVoteEndDate())
                    .build();

            meetingResponseDtoList.add(meetingResponseDto);
        }

        return meetingResponseDtoList;
    }

    /**
     * 모임을 조회하는 메서드입니다.
     *
     * @param id 조회할 모임 ID
     * @return 모임 응답 DTO
     */
    @Transactional(readOnly = true)
    public MeetingResponseDto getMeeting(Long id) {

        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(MeetingNotFoundException::new);

        return MeetingResponseDto.builder()
                .meetingId(meeting.getMeetingId())
                .meetingName(meeting.getMeetingName())
                .meetingStartDate(meeting.getMeetingStartDate())
                .meetingEndDate(meeting.getMeetingEndDate())
                .numberOfPeople(meeting.getNumberOfPeople())
                .isOnline(meeting.getIsOnline())
                .isAnonymous(meeting.getIsAnonymous())
                .voteEndDate(meeting.getVoteEndDate())
                .build();
    }
}
