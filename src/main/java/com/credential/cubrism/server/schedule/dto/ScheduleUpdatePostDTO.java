package com.credential.cubrism.server.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ScheduleUpdatePostDTO {
    private UUID scheduleId;
    private String startDate;
    private String endDate;
    private boolean isAllDay;
    private String title;
    private String content;
}
