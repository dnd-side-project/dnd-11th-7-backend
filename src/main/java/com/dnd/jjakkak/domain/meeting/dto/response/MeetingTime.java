package com.dnd.jjakkak.domain.meeting.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 모임 시간 정보를 담은 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 19.
 */
@Getter
public class MeetingTime {

    private final List<String> memberNames;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Double rank;


    @Builder
    public MeetingTime(LocalDateTime startTime, LocalDateTime endTime, Double rank) {
        this.memberNames = new ArrayList<>();
        this.startTime = startTime;
        this.endTime = endTime;
        this.rank = rank;
    }

    public void addMemberNames(List<String> memberNames) {
        this.memberNames.addAll(memberNames);
    }

    public void setAnonymous() {

        List<String> anonymous = new ArrayList<>();
        for (int i = 1; i <= memberNames.size(); i++) {
            anonymous.add("익명" + i);
        }

        this.memberNames.clear();
        this.memberNames.addAll(anonymous);
    }
}
