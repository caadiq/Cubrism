package com.credential.cubrism.server.studygroup.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class StudyGroupDDayDto {
    private Long groupId;
    private String dName;
    private LocalDate dDay;

    public StudyGroupDDayDto(Long groupId, String dName, LocalDate dDay) {
        this.groupId = groupId;
        this.dName = dName;
        this.dDay = dDay;
    }
}
