package com.credential.cubrism.server.schedule.service;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.utils.SecurityUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.schedule.dto.ScheduleAddDto;
import com.credential.cubrism.server.schedule.dto.ScheduleListDto;
import com.credential.cubrism.server.schedule.dto.ScheduleUpdateDto;
import com.credential.cubrism.server.schedule.entity.Schedules;
import com.credential.cubrism.server.schedule.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    private final SecurityUtil securityUtil;

    // 일정 추가
    @Transactional
    public ResponseEntity<MessageDto> addSchedule(ScheduleAddDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        Schedules schedules = new Schedules();
        setScheduleFields(schedules, currentUser, dto.getStartDate(), dto.getEndDate(), dto.isAllDay(), dto.getTitle(), dto.getContent());
        scheduleRepository.save(schedules);

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDto("일정이 추가되었습니다."));
    }

    // 일정 삭제
    @Transactional
    public ResponseEntity<MessageDto> deleteSchedule(Long scheduleId) {
        Users currentUser = securityUtil.getCurrentUser();

        Schedules schedules = scheduleRepository.findByUserIdAndScheduleId(currentUser.getUuid(), scheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        scheduleRepository.delete(schedules);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("일정을 삭제했습니다."));
    }

    // 일정 수정
    @Transactional
    public ResponseEntity<MessageDto> updateSchedule(ScheduleUpdateDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        Schedules schedules = scheduleRepository.findByUserIdAndScheduleId(currentUser.getUuid(), dto.getScheduleId())
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        setScheduleFields(schedules, currentUser, dto.getStartDate(), dto.getEndDate(), dto.isAllDay(), dto.getTitle(), dto.getContent());
        scheduleRepository.save(schedules);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("일정을 수정했습니다."));
    }

    // 일정 목록
    public ResponseEntity<List<ScheduleListDto>> scheduleList(int year, int month) {
        Users currentUser = securityUtil.getCurrentUser();

        List<ScheduleListDto> scheduleList = scheduleRepository.findByUserIdAndYearAndMonth(currentUser.getUuid(), year, month).stream()
                .map(schedule -> new ScheduleListDto(
                        schedule.getScheduleId(),
                        schedule.getStartDate().toString(),
                        schedule.getEndDate() != null ? schedule.getEndDate().toString() : null,
                        schedule.isAllDay(),
                        schedule.getTitle(),
                        schedule.getContent()
                )).toList();

        return ResponseEntity.status(HttpStatus.OK).body(scheduleList);
    }

    private void setScheduleFields(Schedules schedules, Users user, String startDate, String endDate, boolean isAllDay, String title, String content) {
        schedules.setUser(user);
        schedules.setStartDate(LocalDateTime.parse(startDate));
        schedules.setEndDate(endDate != null && !endDate.isEmpty() ? LocalDateTime.parse(endDate) : null);
        schedules.setAllDay(isAllDay);
        schedules.setTitle(title);
        schedules.setContent(content);
    }
}
