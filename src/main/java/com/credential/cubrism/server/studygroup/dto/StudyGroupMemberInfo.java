package com.credential.cubrism.server.studygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StudyGroupMemberInfo {
    private String nickname;
    private String email;
    private boolean admin;
}
