package com.credential.cubrism.server.studygroup.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class UserGoalEnterDto {
    private List<StudyGroupGoalEnterDto> goals;
    private double completionPercentage;

    public UserGoalEnterDto(List<StudyGroupGoalEnterDto> goals, double completionPercentage) {
        this.goals = goals;
        this.completionPercentage = completionPercentage;
    }
}
