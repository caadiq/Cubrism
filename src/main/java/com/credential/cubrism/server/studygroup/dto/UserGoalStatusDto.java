package com.credential.cubrism.server.studygroup.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserGoalStatusDto {
    private String username;
    private List<StudyGroupGoalDto> goals;
    private double completionPercentage;

    public UserGoalStatusDto(String username, List<StudyGroupGoalDto> goals, double completionPercentage) {
        this.username = username;
        this.goals = goals;
        this.completionPercentage = completionPercentage;
    }
}
