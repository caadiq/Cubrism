package com.credential.cubrism.server.studygroup.dto;

import lombok.Getter;

@Getter
public class StudyGroupGoalSubmitDto {
    private Long goalId;
    private Long groupId;
    private String content;
    private String imageUrl;
}
