package com.credential.cubrism.server.studygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StudyGroupGoalEnterDto {
    private Long goalId;
    private String goalName;
    private boolean completed;
}
