package com.credential.cubrism.server.studygroup.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class StudyGroupAddGoalDto {
    public Long studyGroupId;
    public String goalName;
    public String goalDescription;
    public List<String> details;
}
