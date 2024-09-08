package com.dnd.jjakkak.domain.schedule.repository;

import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.schedule.entity.Schedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 일정 레포지토리 테스트입니다.
 *
 * @author 정승조
 * @version 2024. 09. 06.
 */
@DataJpaTest
class ScheduleRepositoryTest {

    private static final String MEETING_UUID = "met123";
    private static final String SCHEDULE_UUID = "sch123";
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    TestEntityManager em;
    private Meeting meeting;
    private Member member;

    @BeforeEach
    void setUp() {
        Meeting createMeeting = Meeting.builder()
                .meetingName("세븐일레븐")
                .meetingStartDate(LocalDate.of(2024, 8, 27))
                .meetingEndDate(LocalDate.of(2024, 8, 29))
                .numberOfPeople(6)
                .isAnonymous(false)
                .dueDateTime(LocalDateTime.of(2024, 8, 26, 23, 59, 59))
                .meetingUuid("met123")
                .meetingLeaderId(1L)
                .build();

        meeting = em.persist(createMeeting);

        Member createMember = Member.builder()
                .memberNickname("정승조")
                .kakaoId(1234L)
                .build();

        member = em.persist(createMember);
    }

    @Test
    @DisplayName("회원 ID와 모임 UUID로 일정을 조회")
    void find_by_memberId_and_meetingUuid() {

        // given
        Schedule schedule = Schedule.builder()
                .member(member)
                .meeting(meeting)
                .scheduleUuid(SCHEDULE_UUID)
                .scheduleNickname("정승조")
                .build();

        em.persist(schedule);

        // when
        Optional<Schedule> optionalSchedule = scheduleRepository.findByMemberIdAndMeetingUuid(member.getMemberId(), MEETING_UUID);

        // then
        assertTrue(optionalSchedule.isPresent());

        Schedule actual = optionalSchedule.get();
        assertAll(
                () -> assertEquals(schedule.getScheduleId(), actual.getScheduleId()),
                () -> assertEquals(schedule.getMeeting(), actual.getMeeting()),
                () -> assertEquals(schedule.getMember(), actual.getMember()),
                () -> assertEquals(schedule.getScheduleUuid(), actual.getScheduleUuid()),
                () -> assertEquals(schedule.getScheduleNickname(), actual.getScheduleNickname())
        );
    }

    @Test
    @DisplayName("모임의 UUID로 할당되지 않은 일정을 조회")
    void find_not_assigned_schedule_by_meeting_uuid() {

        // given (default isAssigned = false)
        Schedule schedule = Schedule.builder()
                .member(member)
                .meeting(meeting)
                .scheduleUuid(SCHEDULE_UUID)
                .scheduleNickname("정승조")
                .build();

        em.persist(schedule);

        // when
        Optional<Schedule> optionalSchedule = scheduleRepository.findNotAssignedScheduleByMeetingUuid(MEETING_UUID);

        // then
        assertTrue(optionalSchedule.isPresent());

        Schedule actual = optionalSchedule.get();
        assertAll(
                () -> assertEquals(schedule.getScheduleId(), actual.getScheduleId()),
                () -> assertEquals(schedule.getMeeting(), actual.getMeeting()),
                () -> assertEquals(schedule.getMember(), actual.getMember()),
                () -> assertEquals(schedule.getScheduleUuid(), actual.getScheduleUuid()),
                () -> assertEquals(schedule.getScheduleNickname(), actual.getScheduleNickname())
        );
    }


}