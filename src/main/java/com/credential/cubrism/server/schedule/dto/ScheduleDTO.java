package com.credential.cubrism.server.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ScheduleDTO {
    private String startDate;
    private String endDate;
    private boolean isAllDay;
    private String title;
    private String content;
}
