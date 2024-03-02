package com.credential.cubrism.server.schedule.repository;

import com.credential.cubrism.server.schedule.model.Schedules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedules, UUID> {
    @Query("SELECT s FROM Schedules s WHERE s.user.uuid = :userId AND YEAR(s.startDate) = :year AND MONTH(s.startDate) = :month ORDER BY s.startDate ASC")
    List<Schedules> getScheduleByYearAndMonth(@Param("userId") UUID userId, @Param("year") int year, @Param("month") int month);
}
