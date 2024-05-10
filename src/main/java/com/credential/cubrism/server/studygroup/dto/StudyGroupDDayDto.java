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
    private String title;
    private LocalDate day;

    public StudyGroupDDayDto(Long groupId, String title, LocalDate day) {
        this.groupId = groupId;
        this.title = title;
        this.day = day;
    }
}
