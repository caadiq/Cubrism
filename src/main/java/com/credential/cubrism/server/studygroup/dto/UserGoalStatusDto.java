package com.credential.cubrism.server.studygroup.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserGoalStatusDto {
    private String username;
    private List<StudyGroupGoalDto> completedGoals; // 수정된 부분
    private List<StudyGroupGoalDto> uncompletedGoals; // 수정된 부분
    private double completionPercentage;

    public UserGoalStatusDto(String username, List<StudyGroupGoalDto> completedGoals, List<StudyGroupGoalDto> uncompletedGoals, double completionPercentage) {
        this.username = username;
        this.completedGoals = completedGoals;
        this.uncompletedGoals = uncompletedGoals;
        this.completionPercentage = completionPercentage;
    }
}
