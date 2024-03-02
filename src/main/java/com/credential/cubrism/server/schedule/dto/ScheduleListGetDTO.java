package com.credential.cubrism.server.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ScheduleListGetDTO {
    private UUID scheduleId;
    private String startDate;
    private String endDate;
    private boolean isAllDay;
    private String title;
    private String content;
}
