package com.dnd.jjakkak.domain.meetingcategory.entity;

import com.dnd.jjakkak.domain.category.entity.Category;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * 모임 카테고리 엔티티 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 25.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingCategory {

    @EmbeddedId
    private Pk pk;

    @MapsId("meetingId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @MapsId("categoryId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public MeetingCategory(Pk pk, Meeting meeting, Category category) {
        this.pk = pk;
        this.meeting = meeting;
        this.category = category;
    }

    @Embeddable
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Pk implements Serializable {

        @Column(name = "meeting_id")
        private Long meetingId;

        @Column(name = "category_id")
        private Long categoryId;
    }
}
