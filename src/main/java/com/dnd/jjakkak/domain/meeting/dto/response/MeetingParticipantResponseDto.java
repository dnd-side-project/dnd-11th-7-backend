package com.dnd.jjakkak.domain.meeting.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 모임의 참가자 정보를 담은 응답 DTO 입니다.
 *
 * @author 정승조
 * @version 2024. 08. 19.
 */
@Getter
public class MeetingParticipantResponseDto {

    private final Integer numberOfPeople;
    private final boolean anonymousStatus;
    private final List<ParticipantInfo> participantInfoList;

    @Builder
    public MeetingParticipantResponseDto(Integer numberOfPeople, boolean anonymousStatus) {
        this.numberOfPeople = numberOfPeople;
        this.anonymousStatus = anonymousStatus;
        this.participantInfoList = new ArrayList<>();
    }

    public void addParticipantInfoList(List<ParticipantInfo> participantInfoList) {
        this.participantInfoList.addAll(participantInfoList);
    }

    @Getter
    public static class ParticipantInfo {

        private final String nickname;
        private final boolean votedStatus;
        private final boolean leaderStatus;

        public ParticipantInfo(String nickname, boolean votedStatus, boolean leaderStatus) {
            this.nickname = nickname;
            this.votedStatus = votedStatus;
            this.leaderStatus = leaderStatus;
        }
    }
}
