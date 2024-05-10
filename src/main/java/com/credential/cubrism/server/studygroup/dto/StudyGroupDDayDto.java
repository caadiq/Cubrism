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
    private String name;
    private LocalDate day;

    public StudyGroupDDayDto(Long groupId, String Name, LocalDate day) {
        this.groupId = groupId;
        this.name = Name;
        this.day = day;
    }
}
