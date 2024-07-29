package com.dnd.jjakkak.domain.meetingcategory.repository;

import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.meetingcategory.entity.MeetingCategory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 모임 카테고리 레포지토리 테스트 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 28.
 */
@DataJpaTest
class MeetingCategoryRepositoryTest {

    @Autowired
    MeetingCategoryRepository meetingCategoryRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("모임 ID로 전체 모임 카테고리 삭제 테스트")
    void testDeleteByMeetingId() {

        // given

        Category category = Category.builder()
                .categoryName("스터디")
                .build();

        entityManager.persist(category);

        Meeting meeting = Meeting.builder()
                .meetingName("세븐일레븐 스터디")
                .meetingStartDate(LocalDate.of(2024, 7, 27))
                .meetingEndDate(LocalDate.of(2024, 7, 29))
                .numberOfPeople(6)
                .isOnline(true)
                .isAnonymous(false)
                .voteEndDate(LocalDateTime.of(2024, 7, 26, 23, 59, 59))
                .build();

        entityManager.persist(meeting);

        entityManager.flush();

        MeetingCategory.Pk pk = new MeetingCategory.Pk(meeting.getMeetingId(), category.getCategoryId());

        MeetingCategory meetingCategory = MeetingCategory.builder()
                .pk(pk)
                .meeting(meeting)
                .category(category)
                .build();

        meetingCategoryRepository.save(meetingCategory);
        Assertions.assertEquals(1, meetingCategoryRepository.findAll().size());

        // when
        meetingCategoryRepository.deleteByMeetingId(meeting.getMeetingId());

        // then
        Assertions.assertEquals(0, meetingCategoryRepository.findAll().size());
    }

}