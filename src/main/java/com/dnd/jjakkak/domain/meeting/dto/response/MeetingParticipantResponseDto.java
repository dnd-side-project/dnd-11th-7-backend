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
    private final boolean isAnonymous;
    private final List<ParticipantInfo> participantInfoList;

    @Builder
    public MeetingParticipantResponseDto(Integer numberOfPeople, boolean isAnonymous) {
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
        private final boolean isVoted;
        private final boolean isLeader;


        public ParticipantInfo(String nickname, boolean isVoted, boolean isLeader) {
            this.nickname = nickname;
            this.isVoted = isVoted;
            this.isLeader = isLeader;
        }
    }
}
