package com.dnd.jjakkak.domain.meeting.service;

import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.category.exception.CategoryNotFoundException;
import com.dnd.jjakkak.domain.category.repository.CategoryRepository;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingCreateResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingInfoResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingParticipantResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingTimeResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.enums.MeetingSort;
import com.dnd.jjakkak.domain.meeting.exception.MeetingNotFoundException;
import com.dnd.jjakkak.domain.meeting.exception.MeetingUnauthorizedException;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
import com.dnd.jjakkak.domain.meetingcategory.entity.MeetingCategory;
import com.dnd.jjakkak.domain.meetingcategory.repository.MeetingCategoryRepository;
import com.dnd.jjakkak.domain.meetingmember.repository.MeetingMemberRepository;
import com.dnd.jjakkak.domain.member.dto.response.MemberResponseDto;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.exception.MemberNotFoundException;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import com.dnd.jjakkak.domain.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 모임 서비스 클래스입니다.
 *
 * @author 정승조, 류태웅
 * @version 2024. 07. 25.
 */
@Service
@RequiredArgsConstructor
public class MeetingService {

    private final ScheduleService scheduleService;
    private final MeetingRepository meetingRepository;
    private final MeetingCategoryRepository meetingCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final MemberRepository memberRepository;

    /**
     * 모임을 생성하는 메서드입니다.
     *
     * @param memberId   인증된 회원 ID
     * @param requestDto 모임 생성 요청 DTO
     * @return 모임 생성 응답 DTO (UUID)
     */
    @Transactional
    public MeetingCreateResponseDto createMeeting(Long memberId, MeetingCreateRequestDto requestDto) {

        // checkMeetingDate 메서드를 호출하여 유효성 검사를 진행합니다.
        requestDto.checkMeetingDate();

        String uuid = generateUuid();

        // 모임 생성 로직
        Meeting meeting = Meeting.builder()
                .meetingName(requestDto.getMeetingName())
                .meetingStartDate(requestDto.getMeetingStartDate())
                .meetingEndDate(requestDto.getMeetingEndDate())
                .numberOfPeople(requestDto.getNumberOfPeople())
                .isAnonymous(requestDto.getIsAnonymous())
                .scheduleInputEndDateTime(requestDto.getScheduleInputEndDateTime())
                .meetingLeaderId(memberId)
                .meetingUuid(uuid)
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

        return new MeetingCreateResponseDto(uuid);
    }

    /**
     * 모임에 속한 회원 조회
     *
     * @param id 조회할 모임 ID
     * @return 회원 응답 DTO
     */
    @Transactional(readOnly = true)
    public List<MemberResponseDto> getMeetingListByMeetingId(Long id) {
        List<Member> memberList = meetingMemberRepository.findByMeetingId(id);
        return memberList.stream()
                .map(MemberResponseDto::new)
                .toList();
    }

    /**
     * 모임을 삭제하는 메서드입니다.
     *
     * @param memberId 인증된 회원 ID
     * @param id       삭제할 모임 ID
     */
    @Transactional
    public void deleteMeeting(Long memberId, Long id) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(MeetingNotFoundException::new);

        // 요청한 회원이 모임의 리더가 아닌 경우 예외 처리
        if (!meeting.getMeetingLeaderId().equals(member.getMemberId())) {
            throw new MeetingUnauthorizedException();
        }

        meetingRepository.deleteById(id);
        meetingCategoryRepository.deleteByMeetingId(id);
    }

    /**
     * 모임의 정보를 조회하는 메서드입니다.
     *
     * @param uuid 조회할 모임 UUID
     * @return 모임 정보 응답 DTO
     */
    @Transactional(readOnly = true)
    public MeetingInfoResponseDto getMeetingInfo(String uuid) {

        if (!meetingRepository.existsByMeetingUuid(uuid)) {
            throw new MeetingNotFoundException();
        }

        return meetingRepository.getMeetingInfo(uuid);
    }

    /**
     * 모임의 시간을 조회하는 메서드입니다.
     *
     * @param uuid 조회할 모임 UUID
     * @param sort 정렬 기준 (COUNT: 인원 수, LATEST: 최신순)
     * @return 정렬된 시간 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<MeetingTimeResponseDto> getMeetingTimes(String uuid, MeetingSort sort) {

        if (!meetingRepository.existsByMeetingUuid(uuid)) {
            throw new MeetingNotFoundException();
        }

        return meetingRepository.getMeetingTimes(uuid, sort);
    }

    /**
     * 모임의 참여자를 조회하는 메서드입니다.
     *
     * @param uuid 조회할 모임 UUID
     * @return 참여자 응답 DTO
     */
    @Transactional(readOnly = true)
    public MeetingParticipantResponseDto getParticipants(String uuid) {

        if (!meetingRepository.existsByMeetingUuid(uuid)) {
            throw new MeetingNotFoundException();
        }

        return meetingRepository.getParticipant(uuid);
    }

    /**
     * UUID를 생성하는 메서드입니다.
     *
     * @return UUID
     */
    private String generateUuid() {
        String uuid;

        do {
            uuid = UUID.randomUUID().toString().substring(0, 8);
        } while (meetingRepository.existsByMeetingUuid(uuid));

        return uuid;
    }

}
