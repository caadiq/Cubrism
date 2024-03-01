package com.credential.cubrism.server.schedule.repository;

import com.credential.cubrism.server.schedule.model.Schedules;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedules, UUID> {

}
