package com.credential.cubrism.server.schedule.controller;

import com.credential.cubrism.server.schedule.dto.AddScheduleRequestDTO;
import com.credential.cubrism.server.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping("/add")
    public ResponseEntity<?> addSchedule(@RequestBody AddScheduleRequestDTO addScheduleRequestDTO, Authentication authentication) {
        try {
            scheduleService.addSchedule(addScheduleRequestDTO, authentication);
            return ResponseEntity.ok("일정 추가 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}