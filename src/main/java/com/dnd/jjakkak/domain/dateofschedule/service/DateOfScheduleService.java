package com.dnd.jjakkak.domain.dateofschedule.service;

import com.dnd.jjakkak.domain.dateofschedule.dto.request.DateOfScheduleCreateRequestDto;
import com.dnd.jjakkak.domain.dateofschedule.entity.DateOfSchedule;
import com.dnd.jjakkak.domain.dateofschedule.exception.ScheduleDateOutOfMeetingDateException;
import com.dnd.jjakkak.domain.dateofschedule.repository.DateOfScheduleRepository;
import com.dnd.jjakkak.domain.meeting.entity.Meeting;
import com.dnd.jjakkak.domain.schedule.entity.Schedule;
import com.dnd.jjakkak.domain.schedule.exception.ScheduleNotFoundException;
import com.dnd.jjakkak.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 일정 날짜 서비스 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 07. 30.
 */
@Service
@RequiredArgsConstructor
public class DateOfScheduleService {

    private final DateOfScheduleRepository dateOfScheduleRepository;
    private final ScheduleRepository scheduleRepository;

    /**
     * 일정 날짜를 생성하는 메서드입니다.
     *
     * @param scheduleId 일정 ID
     * @param requestDto 일정 날짜 생성 요청 DTO
     */
    @Transactional
    public void createDateOfSchedule(Long scheduleId, DateOfScheduleCreateRequestDto requestDto) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);

        validateDateOfSchedule(schedule, requestDto);

        LocalDateTime startTime = requestDto.getStartTime();
        LocalDateTime endTime = requestDto.getEndTime();
        Integer rank = requestDto.getRank();

        Duration interval = Duration.ofHours(1);

        while (startTime.isBefore(endTime)) {
            LocalDateTime nextEndTime = startTime.plus(interval);

            // 마지막 시간 간격 조정 (endTime을 넘어가지 않도록)
            if (nextEndTime.isAfter(endTime)) {
                nextEndTime = endTime;
            }

            // 일정 날짜 생성
            DateOfSchedule dateOfSchedule = DateOfSchedule.builder()
                    .schedule(schedule)
                    .dateOfScheduleStart(startTime)
                    .dateOfScheduleEnd(nextEndTime)
                    .dateOfScheduleRank(rank) // rank는 동일하게 사용
                    .build();

            dateOfScheduleRepository.save(dateOfSchedule);

            // 다음 시간으로 이동
            startTime = nextEndTime;
        }
    }

    private void validateDateOfSchedule(Schedule schedule, DateOfScheduleCreateRequestDto requestDto) {
        Meeting meeting = schedule.getMeeting();
        LocalDate meetingStartDate = meeting.getMeetingStartDate();
        LocalDate meetingEndDate = meeting.getMeetingEndDate();

        LocalDateTime startTime = requestDto.getStartTime();
        LocalDateTime endTime = requestDto.getEndTime();

        if (startTime.toLocalDate().isBefore(meetingStartDate) || endTime.toLocalDate().isAfter(meetingEndDate)) {
            throw new ScheduleDateOutOfMeetingDateException();
        }
    }

    /**
     * 일정 날짜 리스트를 업데이트하는 메서드입니다. (모든 일정 날짜 삭제 후 새로 생성)
     *
     * @param scheduleId 일정 ID
     * @param requestDto 일정 날짜 생성 요청 DTO 리스트
     */
    @Transactional
    public void updateDateList(Long scheduleId, List<DateOfScheduleCreateRequestDto> requestDto) {

        dateOfScheduleRepository.deleteAllByScheduleId(scheduleId);

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);

        // dateOfSchedule 생성
        requestDto.forEach(dto -> {
            DateOfSchedule dateOfSchedule = DateOfSchedule.builder()
                    .schedule(schedule)
                    .dateOfScheduleStart(dto.getStartTime())
                    .dateOfScheduleEnd(dto.getEndTime())
                    .dateOfScheduleRank(dto.getRank())
                    .build();

            dateOfScheduleRepository.save(dateOfSchedule);
        });
    }
}
