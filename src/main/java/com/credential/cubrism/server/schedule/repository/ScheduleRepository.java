package com.credential.cubrism.server.schedule.repository;

import com.credential.cubrism.server.schedule.entity.Schedules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedules, Long> {
    @Query("SELECT s FROM Schedules s WHERE s.user.uuid = :userId AND ((YEAR(s.startDate) <= :year AND MONTH(s.startDate) <= :month AND YEAR(s.endDate) >= :year AND MONTH(s.endDate) >= :month) OR (YEAR(s.startDate) = :year AND MONTH(s.startDate) = :month) OR (YEAR(s.endDate) = :year AND MONTH(s.endDate) = :month)) ORDER BY s.startDate ASC")
    List<Schedules> findByUserIdAndYearAndMonth(@Param("userId") UUID userId, @Param("year") int year, @Param("month") int month);

    Optional<Schedules> findByUserUuidAndScheduleId(UUID userId, Long scheduleId);

    List<Schedules> findByUserUuid(UUID userId);
}