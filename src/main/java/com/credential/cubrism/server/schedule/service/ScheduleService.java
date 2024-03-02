package com.credential.cubrism.server.schedule.service;

import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.authentication.utils.AuthenticationUtil;
import com.credential.cubrism.server.schedule.dto.ScheduleAddPostDTO;
import com.credential.cubrism.server.schedule.dto.ScheduleListGetDTO;
import com.credential.cubrism.server.schedule.model.Schedules;
import com.credential.cubrism.server.schedule.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addSchedule(ScheduleAddPostDTO dto, Authentication authentication) {
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);

        Schedules schedules = new Schedules();
        schedules.setUser(user);
        schedules.setStartDate(LocalDateTime.parse(dto.getStartDate()));
        schedules.setEndDate(dto.getEndDate() != null && !dto.getEndDate().isEmpty() ? LocalDateTime.parse(dto.getEndDate()) : null);
        schedules.setAllDay(dto.isAllDay());
        schedules.setTitle(dto.getTitle());
        schedules.setContent(dto.getContent());
        scheduleRepository.save(schedules);
    }

    public List<ScheduleListGetDTO> getScheduleList(int year, int month, Authentication authentication) {
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);

        List<Schedules> schedules = scheduleRepository.getScheduleByYearAndMonth(user.getUuid(), year, month);

        return schedules.stream()
                .map(schedule -> new ScheduleListGetDTO(
                        schedule.getScheduleId(),
                        schedule.getStartDate().toString(),
                        schedule.getEndDate() != null ? schedule.getEndDate().toString() : null,
                        schedule.isAllDay(),
                        schedule.getTitle(),
                        schedule.getContent()
                ))
                .collect(Collectors.toList());
    }
}
