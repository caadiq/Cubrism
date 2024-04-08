package com.credential.cubrism.server.studygroup.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class StudyGroupUpdateGoalDto {
    public Long goalId;
    public String goalName;
    public String goalDescription;
    public List<String> details;
}
