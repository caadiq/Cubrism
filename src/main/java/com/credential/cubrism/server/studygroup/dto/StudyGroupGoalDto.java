package com.credential.cubrism.server.studygroup.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyGroupGoalDto {
    private Long goalId;
    private String goalName;

    public StudyGroupGoalDto(Long goalId, String goalName) {
        this.goalId = goalId;
        this.goalName = goalName;
    }
}