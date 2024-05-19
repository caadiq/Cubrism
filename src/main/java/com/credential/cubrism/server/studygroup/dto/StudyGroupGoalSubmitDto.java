package com.credential.cubrism.server.studygroup.dto;

import lombok.Getter;

@Getter
public class StudyGroupGoalSubmitDto {
    private Long userGoalId;
    private Long groupId;
    private String content;
    private String imageUrl;
}
