package com.credential.cubrism.server.studygroup.dto;

import com.credential.cubrism.server.studygroup.entity.GoalDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserGoalStatusDto {
    private String username;
    private List<GoalDetailDto> completedDetails;
    private List<GoalDetailDto> uncompletedDetails;
    private double completionPercentage;

    public UserGoalStatusDto(String username, List<GoalDetailDto> completedDetails, List<GoalDetailDto> uncompletedDetails, double completionPercentage) {
        this.username = username;
        this.completedDetails = completedDetails;
        this.uncompletedDetails = uncompletedDetails;
        this.completionPercentage = completionPercentage;
    }

}
