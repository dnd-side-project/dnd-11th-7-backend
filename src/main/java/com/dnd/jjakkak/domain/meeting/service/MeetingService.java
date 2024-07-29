package com.dnd.jjakkak.domain.meeting.service;

import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.category.exception.CategoryNotFoundException;
import com.dnd.jjakkak.domain.category.repository.CategoryRepository;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingConfirmRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingUpdateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.exception.MeetingNotFoundException;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
import com.dnd.jjakkak.domain.meetingcategory.entity.MeetingCategory;
import com.dnd.jjakkak.domain.meetingcategory.repository.MeetingCategoryRepository;
import com.dnd.jjakkak.domain.schedule.service.ScheduleService;
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

    private final ScheduleService scheduleService;
    private final MeetingRepository meetingRepository;
    private final MeetingCategoryRepository meetingCategoryRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 모임을 생성하는 메서드입니다.
     *
     * @param requestDto 생성할 모임 정보 DTO
     */
    @Transactional
    public void createMeeting(MeetingCreateRequestDto requestDto) {

        // checkMeetingDate 메서드를 호출하여 유효성 검사를 진행합니다.
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

        // 모임 생성 시 인원 수 만큼 기본 일정을 생성합니다.
        // TODO: 개선할 수 있는 방법 찾아보기
        for (int i = 0; i < meeting.getNumberOfPeople(); i++) {
            scheduleService.createDefaultSchedule(meeting);
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

    /**
     * 모임을 수정하는 메서드입니다.
     *
     * @param id         모임 ID
     * @param requestDto 수정된 모임 정보 DTO
     */
    @Transactional
    public void updateMeeting(Long id, MeetingUpdateRequestDto requestDto) {

        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(MeetingNotFoundException::new);

        // checkMeetingDate 메서드를 호출하여 유효성 검사를 진행합니다.
        requestDto.checkMeetingDate();

        // dirty checking - 모임 정보를 수정합니다.
        meeting.updateMeeting(requestDto);

        // 모임의 카테고리 값도 수정 필요
        // 기존 모임과 연결된 카테고리 정보를 삭제합니다.
        meetingCategoryRepository.deleteByMeetingId(meeting.getMeetingId());

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
     * 모임의 확정된 일자를 설정하는 메서드입니다.
     *
     * @param id         모임 ID
     * @param requestDto 모임 확정 요청 DTO
     */
    @Transactional
    public void confirmMeeting(Long id, MeetingConfirmRequestDto requestDto) {

        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(MeetingNotFoundException::new);

        meeting.updateConfirmedSchedule(requestDto);
    }

    /**
     * 모임을 삭제하는 메서드입니다.
     *
     * @param id 삭제할 모임 ID
     */
    @Transactional
    public void deleteMeeting(Long id) {

        // TODO 1: 모임을 생성한 리더가 맞는지 검증 확인 필요

        // TODO 2: 모임과 엮인 모임 일정 테이블 삭제 로직 추가 필요
        if (!meetingRepository.existsById(id)) {
            throw new MeetingNotFoundException();
        }

        meetingRepository.deleteById(id);
    }
}
