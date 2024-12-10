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
    private final Boolean isAnonymous;
    private final List<ParticipantInfo> participantInfoList;

    @Builder
    public MeetingParticipantResponseDto(Integer numberOfPeople, Boolean isAnonymous) {
        this.numberOfPeople = numberOfPeople;
        this.isAnonymous = isAnonymous;
        this.participantInfoList = new ArrayList<>();
    }

    public void addParticipantInfoList(List<ParticipantInfo> participantInfoList) {
        this.participantInfoList.addAll(participantInfoList);
    }

    @Getter
    public static class ParticipantInfo {

        private final String nickname;
        private final Boolean isAssigned;
        private final Boolean isLeader;

        public ParticipantInfo(String nickname, Boolean isAssigned, Boolean isLeader) {
            this.nickname = nickname;
            this.isAssigned = isAssigned;
            this.isLeader = isLeader != null && isLeader;
        }
    }
}
