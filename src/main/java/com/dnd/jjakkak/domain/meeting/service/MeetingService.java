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
import com.dnd.jjakkak.domain.member.exception.MemberNotFoundException;
import com.dnd.jjakkak.domain.member.jwt.provider.JwtProvider;
import com.dnd.jjakkak.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 모임 서비스 클래스입니다.
 *
 * @author 류태웅
 * @version 2024. 07. 30.
 */
@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingCategoryRepository meetingCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    /**
     * 모임을 생성하는 메서드입니다.
     *
     * @param token      JWT Token
     * @param requestDto 모임 생성 요청 DTO
     * @return 모임 생성 응답 DTO (UUID)
     */
    @Transactional
    public MeetingCreateResponseDto createMeeting(String token, MeetingCreateRequestDto requestDto) {

        // checkMeetingDate 메서드를 호출하여 유효성 검사를 진행합니다.
        requestDto.checkMeetingDate();

        Member member = getMemberByToken(token);
        String uuid = generateUuid();

        // 모임 생성 로직
        Meeting meeting = Meeting.builder()
                .meetingName(requestDto.getMeetingName())
                .meetingStartDate(requestDto.getMeetingStartDate())
                .meetingEndDate(requestDto.getMeetingEndDate())
                .numberOfPeople(requestDto.getNumberOfPeople())
                .isOnline(requestDto.getIsOnline())
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
     * @param id MeetingId
     * @return List<MemberResponseDto>
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
     * @param token JWT Token
     * @param id    모임 ID
     */
    @Transactional
    public void deleteMeeting(String token, Long id) {

        Member member = getMemberByToken(token);

        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(MeetingNotFoundException::new);

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

    /**
     * 토큰을 통해 회원 정보를 조회하는 메서드입니다.
     *
     * @param token AccessToken
     * @return Member Entity
     */
    private Member getMemberByToken(String token) {
        String kakaoId = Objects.requireNonNull(jwtProvider.validate(token));
        return memberRepository.findByKakaoId(Long.parseLong(kakaoId))
                .orElseThrow(MemberNotFoundException::new);
    }

}
