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
    public void createDateOfSchedule(Long scheduleId, List<DateOfScheduleCreateRequestDto> requestDto) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleNotFoundException::new);

        validateDateOfSchedule(schedule, requestDto);

        requestDto.forEach(dto -> {

            LocalDateTime startTime = dto.getStartTime();
            LocalDateTime endTime = dto.getEndTime();

            Duration interval = Duration.ofHours(1);
            while (startTime.isBefore(endTime)) {
                LocalDateTime nextEndTime = startTime.plus(interval);

                if (nextEndTime.isAfter(endTime)) {
                    nextEndTime = endTime;
                }

                DateOfSchedule dateOfSchedule = DateOfSchedule.builder()
                        .schedule(schedule)
                        .dateOfScheduleStart(startTime)
                        .dateOfScheduleEnd(nextEndTime)
                        .dateOfScheduleRank(1)
                        .build();

                dateOfScheduleRepository.save(dateOfSchedule);

                startTime = nextEndTime;
            }
        });
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

        validateDateOfSchedule(schedule, requestDto);

        // TODO: rank 값 추후에 변경해줘야 함!
        requestDto.forEach(dto -> {

            LocalDateTime startTime = dto.getStartTime();
            LocalDateTime endTime = dto.getEndTime();

            Duration interval = Duration.ofHours(1);
            while (startTime.isBefore(endTime)) {
                LocalDateTime nextEndTime = startTime.plus(interval);

                if (nextEndTime.isAfter(endTime)) {
                    nextEndTime = endTime;
                }

                DateOfSchedule dateOfSchedule = DateOfSchedule.builder()
                        .schedule(schedule)
                        .dateOfScheduleStart(startTime)
                        .dateOfScheduleEnd(nextEndTime)
                        .dateOfScheduleRank(1)
                        .build();

                dateOfScheduleRepository.save(dateOfSchedule);

                startTime = nextEndTime;
            }
        });
    }

    private void validateDateOfSchedule(Schedule schedule, List<DateOfScheduleCreateRequestDto> requestDtos) {
        Meeting meeting = schedule.getMeeting();
        LocalDate meetingStartDate = meeting.getMeetingStartDate();
        LocalDate meetingEndDate = meeting.getMeetingEndDate();

        for (DateOfScheduleCreateRequestDto requestDto : requestDtos) {

            LocalDateTime startTime = requestDto.getStartTime();
            LocalDateTime endTime = requestDto.getEndTime();

            if (startTime.toLocalDate().isBefore(meetingStartDate) || endTime.toLocalDate().isAfter(meetingEndDate)) {
                throw new ScheduleDateOutOfMeetingDateException();
            }
        }
    }
}
