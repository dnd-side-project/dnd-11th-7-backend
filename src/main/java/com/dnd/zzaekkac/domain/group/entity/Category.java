package com.dnd.zzaekkac.domain.group.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 카테고리를 나타내는 ENUM 입니다.
 *
 * @author 정승조
 * @version 2024. 07. 24.
 */
@Getter
@RequiredArgsConstructor
public enum Category {
    SCHOOL("학교"),
    FRIEND("친구"),
    TEAM("팀플"),
    MEETING("회의"),
    STUDY("스터디"),
    HOBBY("취미"),
    VOLUNTEER("봉사"),
    ETC("기타");

    private final String categoryName;
}
