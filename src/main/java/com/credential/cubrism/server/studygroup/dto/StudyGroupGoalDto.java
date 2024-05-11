package com.credential.cubrism.server.studygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyGroupGoalDto {
    private Integer index;
    private Long goalId;
    private String goalName;
}