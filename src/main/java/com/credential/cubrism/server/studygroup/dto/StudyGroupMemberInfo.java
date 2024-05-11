package com.credential.cubrism.server.studygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyGroupMemberInfo {
    private String nickname;
    private String email;
    private String profileImage;
    private boolean admin;
    private UserGoalEnterDto userGoal;
}
