package com.credential.cubrism.server.studygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StudyGroupGoalSubmitListDto {
    private Long userGoalId;
    private String nickname;
    private String profileImageUrl;
    private String content;
    private String imageUrl;
    private LocalDateTime submittedAt;
}
