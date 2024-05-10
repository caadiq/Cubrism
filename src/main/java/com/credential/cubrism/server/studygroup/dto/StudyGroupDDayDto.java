package com.credential.cubrism.server.studygroup.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
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
