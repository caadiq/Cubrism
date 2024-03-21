package com.credential.cubrism.server.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleUpdateDto {
    private Long scheduleId;
    private String startDate;
    private String endDate;
    private boolean isAllDay;
    private String title;
    private String content;
}
