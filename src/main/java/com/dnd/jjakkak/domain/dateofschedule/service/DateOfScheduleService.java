package com.dnd.jjakkak.domain.dateofschedule.service;

import com.dnd.jjakkak.domain.dateofschedule.dto.request.DateOfScheduleCreateRequestDto;
import com.dnd.jjakkak.domain.dateofschedule.entity.DateOfSchedule;
import com.dnd.jjakkak.domain.dateofschedule.repository.DateOfScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 일정 날짜를 생성하는 메서드입니다.
     *
     * @param scheduleId 일정 ID
     * @param requestDto 일정 날짜 생성 요청 DTO
     */
    @Transactional
    public void createDateOfSchedule(Long scheduleId, DateOfScheduleCreateRequestDto requestDto) {

        // 일정 날짜 생성 로직
        DateOfSchedule dateOfSchedule = DateOfSchedule.builder()
                .scheduleId(scheduleId)
                .dateOfScheduleStart(requestDto.getDateOfScheduleStart())
                .dateOfScheduleEnd(requestDto.getDateOfScheduleEnd())
                .dateOfScheduleRank(requestDto.getDateOfScheduleRank())
                .build();

        dateOfScheduleRepository.save(dateOfSchedule);
    }
}
