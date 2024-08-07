package com.dnd.jjakkak.domain.meeting.service;

import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.category.exception.CategoryNotFoundException;
import com.dnd.jjakkak.domain.category.repository.CategoryRepository;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingConfirmRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.request.MeetingCreateRequestDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingCreateResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.exception.MeetingNotFoundException;
import com.dnd.jjakkak.domain.meeting.exception.MeetingUnauthorizedException;
import com.dnd.jjakkak.domain.meeting.repository.MeetingRepository;
import com.dnd.jjakkak.domain.meetingcategory.entity.MeetingCategory;
import com.dnd.jjakkak.domain.meetingcategory.repository.MeetingCategoryRepository;
import com.dnd.jjakkak.domain.meetingmember.repository.MeetingMemberRepository;
import com.dnd.jjakkak.domain.member.dto.response.MemberResponseDto;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.member.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 모임 서비스 클래스입니다.
 *
 * @author 정승조, 류태웅
 * @version 2024. 07. 30.
 */
@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingCategoryRepository meetingCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final MeetingMemberRepository meetingMemberRepository;

    /**
     * 모임을 생성하는 메서드입니다.
     *
     * @param user       로그인한 회원 정보
     * @param requestDto 모임 생성 요청 DTO
     * @return 모임 생성 응답 DTO (UUID)
     */
    @Transactional
    public MeetingCreateResponseDto createMeeting(OAuth2User user, MeetingCreateRequestDto requestDto) {

        // checkMeetingDate 메서드를 호출하여 유효성 검사를 진행합니다.
        requestDto.checkMeetingDate();
        String uuid = generateUuid();

        if (user == null) {
            throw new UnauthorizedException();
        }

        Member member = (Member) user;

        // 모임 생성 로직
        Meeting meeting = Meeting.builder()
                .meetingName(requestDto.getMeetingName())
                .meetingStartDate(requestDto.getMeetingStartDate())
                .meetingEndDate(requestDto.getMeetingEndDate())
                .numberOfPeople(requestDto.getNumberOfPeople())
                .isAnonymous(requestDto.getIsAnonymous())
                .voteEndDate(requestDto.getVoteEndDate())
                .meetingLeaderId(member.getMemberId())
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

        return MeetingCreateResponseDto.builder()
                .meetingUuid(uuid)
                .build();
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
     * @param user 로그인한 회원 정보
     * @param id   삭제할 모임 ID
     */
    @Transactional
    public void deleteMeeting(OAuth2User user, Long id) {

        if (user == null) {
            throw new UnauthorizedException();
        }

        Member member = (Member) user;

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
     * UUID로 모임을 조회하는 메서드입니다.
     *
     * @param uuid 조회할 모임 UUID
     * @return 모임 응답 DTO
     */
    @Transactional(readOnly = true)
    public MeetingResponseDto getMeetingByUuid(String uuid) {

        Meeting meeting = meetingRepository.findByMeetingUuid(uuid)
                .orElseThrow(MeetingNotFoundException::new);

        return MeetingResponseDto.builder()
                .meeting(meeting)
                .build();
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
