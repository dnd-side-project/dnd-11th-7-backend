package com.dnd.jjakkak.domain.meeting.repository;

import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.dateofschedule.entity.DateOfSchedule;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingInfoResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingParticipantResponseDto;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingTime;
import com.dnd.jjakkak.domain.meeting.dto.response.MeetingTimeResponseDto;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meeting.enums.MeetingSort;
import com.dnd.jjakkak.domain.meetingcategory.entity.MeetingCategory;
import com.dnd.jjakkak.domain.member.entity.Member;
import com.dnd.jjakkak.domain.schedule.entity.Schedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 모임 레포지토리 테스트 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 21.
 */
@DataJpaTest
class MeetingRepositoryTest {

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    TestEntityManager em;

    Meeting testMeeting;

    @BeforeEach
    void setUp() {

        Category category = Category.builder()
                .categoryName("스터디")
                .build();

        em.persist(category);

        Meeting meeting = Meeting.builder()
                .meetingLeaderId(1L)
                .meetingName("백엔드 개발")
                .meetingUuid("123abc")
                .numberOfPeople(5)
                .meetingStartDate(LocalDate.of(2024, 8, 21))
                .meetingEndDate(LocalDate.of(2024, 8, 23))
                .dueDateTime(LocalDateTime.of(2024, 8, 20, 23, 59, 59))
                .isAnonymous(false)
                .build();

        testMeeting = em.persist(meeting);


        MeetingCategory.Pk pk = new MeetingCategory.Pk(testMeeting.getMeetingId(), category.getCategoryId());
        MeetingCategory meetingCategory = MeetingCategory.builder()
                .pk(pk)
                .category(category)
                .meeting(testMeeting)
                .build();

        em.persist(meetingCategory);

        em.flush();
    }

    @Test
    @DisplayName("모임 uuid 존재 여부 확인")
    void existsByMeetingUuid() {
        // given
        String uuid = "123abc";

        // when
        boolean exists = meetingRepository.existsByMeetingUuid(uuid);

        // then
        assertTrue(exists);
    }

    @Test
    @DisplayName("모임 uuid 존재 여부 확인 - 존재하지 않는 경우")
    void existsByMeetingUuid_notExists() {
        // given
        String uuid = "abc123";

        // when
        boolean exists = meetingRepository.existsByMeetingUuid(uuid);

        // then
        assertFalse(exists);
    }

    @Test
    @DisplayName("모임 인원 체크 테스트")
    void checkMeetingIsFull() {

        // given
        Schedule schedule1 = Schedule.builder()
                .meeting(testMeeting)
                .scheduleNickname("멤버1")
                .scheduleUuid("123abc")
                .build();

        Schedule schedule2 = Schedule.builder()
                .meeting(testMeeting)
                .scheduleNickname("멤버2")
                .scheduleUuid("456def")
                .build();

        em.persist(schedule1);
        em.persist(schedule2);

        // when
        boolean isFull = meetingRepository.checkMeetingFull(testMeeting.getMeetingId());

        // then
        assertFalse(isFull);
    }

    @Test
    @DisplayName("모임이 익명인지 확인")
    void isAnonymous() {
        // when
        boolean isAnonymous = meetingRepository.isAnonymous(testMeeting.getMeetingId());

        // then
        assertFalse(isAnonymous);
    }

    @Test
    @DisplayName("모임 정보 조회")
    void getMeetingInfo() {

        // given
        String uuid = "123abc";

        // when
        MeetingInfoResponseDto meetingInfo = meetingRepository.getMeetingInfo(uuid);

        // then
        assertAll(
                () -> assertEquals(testMeeting.getMeetingId(), meetingInfo.getMeetingId()),
                () -> assertEquals(testMeeting.getMeetingName(), meetingInfo.getMeetingName()),
                () -> assertEquals(testMeeting.getMeetingStartDate(), meetingInfo.getMeetingStartDate()),
                () -> assertEquals(testMeeting.getMeetingEndDate(), meetingInfo.getMeetingEndDate())
        );

        List<String> categoryNames = meetingInfo.getCategoryNames();
        assertEquals(1, categoryNames.size());
        assertEquals("스터디", categoryNames.get(0));
    }

    @Test
    @DisplayName("모임 시간 조회 - COUNT 기준 정렬")
    void getMeetingTimes_defaultSort() {

        // given
        Schedule schedule1 = Schedule.builder()
                .meeting(testMeeting)
                .scheduleNickname("멤버1")
                .scheduleUuid("123abc")
                .build();

        em.persist(schedule1);


        DateOfSchedule dateOfSchedule1 = DateOfSchedule.builder()
                .schedule(schedule1)
                .dateOfScheduleRank(1)
                .dateOfScheduleStart(LocalDateTime.of(2024, 8, 21, 10, 0))
                .dateOfScheduleEnd(LocalDateTime.of(2024, 8, 21, 12, 0))
                .build();

        DateOfSchedule dateOfSchedule2 = DateOfSchedule.builder()
                .schedule(schedule1)
                .dateOfScheduleRank(2)
                .dateOfScheduleStart(LocalDateTime.of(2024, 8, 22, 10, 0))
                .dateOfScheduleEnd(LocalDateTime.of(2024, 8, 22, 12, 0))
                .build();

        em.persist(dateOfSchedule1);
        em.persist(dateOfSchedule2);

        Schedule schedule2 = Schedule.builder()
                .meeting(testMeeting)
                .scheduleNickname("멤버2")
                .scheduleUuid("456def")
                .build();

        em.persist(schedule2);

        DateOfSchedule dateOfSchedule3 = DateOfSchedule.builder()
                .schedule(schedule2)
                .dateOfScheduleRank(1)
                .dateOfScheduleStart(LocalDateTime.of(2024, 8, 21, 10, 0))
                .dateOfScheduleEnd(LocalDateTime.of(2024, 8, 21, 12, 0))
                .build();

        em.persist(dateOfSchedule3);

        em.flush();

        String uuid = "123abc";

        // when
        MeetingTimeResponseDto actual = meetingRepository.getMeetingTimes(uuid, MeetingSort.COUNT);

        // then
        assertEquals(2, actual.getMeetingTimeList().size());

        MeetingTime primary = actual.getMeetingTimeList().get(0);
        assertAll(
                () -> assertEquals(dateOfSchedule1.getDateOfScheduleStart(), primary.getStartTime()),
                () -> assertEquals(dateOfSchedule1.getDateOfScheduleEnd(), primary.getEndTime())
        );

        MeetingTime secondary = actual.getMeetingTimeList().get(1);
        assertAll(
                () -> assertEquals(dateOfSchedule2.getDateOfScheduleStart(), secondary.getStartTime()),
                () -> assertEquals(dateOfSchedule2.getDateOfScheduleEnd(), secondary.getEndTime())
        );
    }


    @Test
    @DisplayName("모임 시간 조회 - LATEST 기준 정렬")
    void getMeetingTimes_latestSort() {
        // given

        Schedule schedule1 = Schedule.builder()
                .meeting(testMeeting)
                .scheduleNickname("멤버1")
                .scheduleUuid("123abc")
                .build();


        DateOfSchedule dateOfSchedule1 = DateOfSchedule.builder()
                .schedule(schedule1)
                .dateOfScheduleRank(1)
                .dateOfScheduleStart(LocalDateTime.of(2024, 8, 23, 10, 0))
                .dateOfScheduleEnd(LocalDateTime.of(2024, 8, 23, 12, 0))
                .build();

        em.persist(schedule1);
        em.persist(dateOfSchedule1);

        Schedule schedule2 = Schedule.builder()
                .meeting(testMeeting)
                .scheduleNickname("멤버2")
                .scheduleUuid("456def")
                .build();

        DateOfSchedule dateOfSchedule2 = DateOfSchedule.builder()
                .schedule(schedule2)
                .dateOfScheduleRank(2)
                .dateOfScheduleStart(LocalDateTime.of(2024, 8, 21, 10, 0))
                .dateOfScheduleEnd(LocalDateTime.of(2024, 8, 21, 12, 0))
                .build();

        em.persist(schedule2);
        em.persist(dateOfSchedule2);

        em.flush();

        String uuid = "123abc";

        // when
        MeetingTimeResponseDto actual = meetingRepository.getMeetingTimes(uuid, MeetingSort.LATEST);

        // then
        assertEquals(2, actual.getMeetingTimeList().size());

        MeetingTime primary = actual.getMeetingTimeList().get(0);
        assertAll(
                () -> assertEquals(dateOfSchedule2.getDateOfScheduleStart(), primary.getStartTime()),
                () -> assertEquals(dateOfSchedule2.getDateOfScheduleEnd(), primary.getEndTime())
        );

        MeetingTime secondary = actual.getMeetingTimeList().get(1);
        assertAll(
                () -> assertEquals(dateOfSchedule1.getDateOfScheduleStart(), secondary.getStartTime()),
                () -> assertEquals(dateOfSchedule1.getDateOfScheduleEnd(), secondary.getEndTime())
        );
    }

    @Test
    @DisplayName("모임 참가자 조회")
    void getParticipant() {

        // given
        Member leader = Member.builder()
                .memberNickname("승조")
                .build();

        ReflectionTestUtils.setField(leader, "memberId", 1L);

        em.merge(leader);

        Schedule schedule1 = Schedule.builder()
                .member(leader)
                .scheduleNickname(leader.getMemberNickname())
                .meeting(testMeeting)
                .scheduleUuid("123abc")
                .build();

        schedule1.scheduleAssign();

        Schedule schedule2 = Schedule.builder()
                .meeting(testMeeting)
                .scheduleNickname("멤버2")
                .scheduleUuid("456def")
                .build();

        em.persist(schedule1);
        em.persist(schedule2);

        em.flush();
        // em.clear();  // clear를 사용하면 엔티티가 detached 상태가 되므로 주의 필요

        String uuid = "123abc";

        // when
        MeetingParticipantResponseDto participant = meetingRepository.getParticipant(uuid);

        // then
        assertEquals(5, participant.getNumberOfPeople());

        List<MeetingParticipantResponseDto.ParticipantInfo> participantInfoList = participant.getParticipantInfoList();
        assertEquals(2, participantInfoList.size());

        MeetingParticipantResponseDto.ParticipantInfo leaderInfo = participantInfoList.get(0);
        assertAll(
                () -> assertEquals("승조", leaderInfo.getNickname()),
                () -> assertTrue(leaderInfo.isVotedStatus()),
                () -> assertTrue(leaderInfo.isLeaderStatus())
        );

        MeetingParticipantResponseDto.ParticipantInfo memberInfo = participantInfoList.get(1);
        assertAll(
                () -> assertEquals("멤버2", memberInfo.getNickname()),
                () -> assertFalse(memberInfo.isVotedStatus()),
                () -> assertFalse(memberInfo.isLeaderStatus())
        );
    }
}