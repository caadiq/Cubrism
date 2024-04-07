package com.credential.cubrism.server.schedule.controller;

import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.schedule.dto.ScheduleAddDto;
import com.credential.cubrism.server.schedule.dto.ScheduleListDto;
import com.credential.cubrism.server.schedule.dto.ScheduleUpdateDto;
import com.credential.cubrism.server.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping("/schedule") // 일정 추가
    public ResponseEntity<MessageDto> addSchedule(@RequestBody ScheduleAddDto dto) {
        return scheduleService.addSchedule(dto);
    }

    @DeleteMapping("/schedule/{scheduleId}") // 일정 삭제
    public ResponseEntity<MessageDto> deleteSchedule(@PathVariable Long scheduleId) {
        return scheduleService.deleteSchedule(scheduleId);
    }

    @PutMapping("/schedule/{scheduleId}") // 일정 수정
    public ResponseEntity<MessageDto> updateSchedule(@PathVariable Long scheduleId, @RequestBody ScheduleUpdateDto dto) {
        return scheduleService.updateSchedule(scheduleId, dto);
    }

    @GetMapping("/schedules") // 일정 목록
    public ResponseEntity<List<ScheduleListDto>> getScheduleList(
            @RequestParam(defaultValue = "#{T(java.time.YearMonth).now().getYear()}") int year,
            @RequestParam(defaultValue = "#{T(java.time.YearMonth).now().getMonthValue()}") int month
    ) {
        return scheduleService.scheduleList(year, month);
    }
}