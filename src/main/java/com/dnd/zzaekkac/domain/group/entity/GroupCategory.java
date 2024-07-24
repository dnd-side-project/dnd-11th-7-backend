package com.dnd.zzaekkac.domain.group.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * 모임과 카테고리 중간 테이블 엔티티입니다.
 *
 * @author 정승조
 * @version 2024. 07. 24.
 */
@Getter
@Entity
@Table(name = "Group_Category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupCategory {

    @EmbeddedId
    private Pk pk;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id")
    private Group group;

    private Category category;

    @Builder
    public GroupCategory(Pk pk, Group group, Category category) {
        this.pk = pk;
        this.group = group;
        this.category = category;
    }

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Pk implements Serializable {

        @Column(nullable = false, name = "group_id")
        private Long groupId;

        @Column(nullable = false, name = "category_id")
        private Category categoryId;
    }
}
