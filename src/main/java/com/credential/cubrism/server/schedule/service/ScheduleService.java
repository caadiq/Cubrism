package com.credential.cubrism.server.schedule.service;

import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.oauth.PrincipalDetails;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.schedule.dto.AddScheduleRequestDTO;
import com.credential.cubrism.server.schedule.model.Schedules;
import com.credential.cubrism.server.schedule.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addSchedule(AddScheduleRequestDTO addScheduleRequestDTO, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        Users user;
        if (principal instanceof PrincipalDetails principalDetails) {
            user = principalDetails.user();
        } else if (principal instanceof String email) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + principal));
        } else {
            throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass().getName());
        }

        Schedules schedules = new Schedules();
        schedules.setUser(user);
        schedules.setStartDate(LocalDateTime.parse(addScheduleRequestDTO.getStartDate()));
        schedules.setEndDate(LocalDateTime.parse(addScheduleRequestDTO.getEndDate()));
        schedules.setAllDay(addScheduleRequestDTO.isAllDay());
        schedules.setTitle(addScheduleRequestDTO.getTitle());
        schedules.setContent(addScheduleRequestDTO.getContent());
        scheduleRepository.save(schedules);
    }
}
