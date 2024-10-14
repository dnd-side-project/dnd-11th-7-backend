package com.dnd.jjakkak.domain.schedule.facade;

import com.dnd.jjakkak.domain.redis.RedisRepository;
import com.dnd.jjakkak.domain.schedule.dto.request.ScheduleAssignRequestDto;
import com.dnd.jjakkak.domain.schedule.dto.response.ScheduleAssignResponseDto;
import com.dnd.jjakkak.domain.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 일정 Facade 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 10. 11.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleFacade {

    private final RedisRepository redisRepository;
    private final ScheduleService scheduleService;

    public void assignScheduleToMember(Long memberId, String meetingUuid, ScheduleAssignRequestDto requestDto) {
        String key = "meeting-" + meetingUuid;
        while (!redisRepository.lock(key)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            scheduleService.assignScheduleToMember(memberId, meetingUuid, requestDto);
        } finally {
            redisRepository.delete(key);
        }
    }

    public ScheduleAssignResponseDto assignScheduleToGuest(String meetingUuid, ScheduleAssignRequestDto requestDto) {
        String key = "meeting-" + meetingUuid;
        while (!redisRepository.lock(key)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            return scheduleService.assignScheduleToGuest(meetingUuid, requestDto);
        } finally {
            redisRepository.delete(key);
        }
    }
}
