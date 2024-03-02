package com.credential.cubrism.server.schedule.controller;

import com.credential.cubrism.server.common.dto.ErrorDTO;
import com.credential.cubrism.server.schedule.dto.ScheduleDTO;
import com.credential.cubrism.server.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping("/add")
    public ResponseEntity<?> addSchedule(@RequestBody ScheduleDTO scheduleDTO, Authentication authentication) {
        try {
            scheduleService.addSchedule(scheduleDTO, authentication);
            return ResponseEntity.ok("일정 추가 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//    @PostMapping("/delete")
//    public ResponseEntity<?> deleteSchedule(@RequestBody RemoveScheduleRequestDTO removeScheduleRequestDTO, Authentication authentication) {
//        try {
//            scheduleService.deleteSchedule(removeScheduleRequestDTO, authentication);
//            return ResponseEntity.ok("일정 삭제 성공");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

//    @PostMapping("/update")
//    public ResponseEntity<?> updateSchedule(@RequestBody UpdateScheduleRequestDTO updateScheduleRequestDTO, Authentication authentication) {
//        try {
//            scheduleService.updateSchedule(updateScheduleRequestDTO, authentication);
//            return ResponseEntity.ok("일정 수정 성공");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

    @GetMapping("/list")
    public ResponseEntity<?> getScheduleList(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Authentication authentication
    ) {
        try {
            if (year == null && month == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("'year', 'month' 파라미터가 필요합니다."));
            } else if (year == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("'year' 파라미터가 필요합니다."));
            } else if (month == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("'month' 파라미터가 필요합니다."));
            }
            return ResponseEntity.ok(scheduleService.getScheduleList(year, month, authentication));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}