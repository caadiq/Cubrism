package com.credential.cubrism.server.studygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StudyGroupInfoDto {
    private Long studyGroupId;
    private String groupName;
    private String groupDescription;
    private String groupAdmin;
    private String adminProfileImage;
    private int currentMembers;
    private int maxMembers;
    private List<String> tags;
    private boolean isRecruiting;
    private List<String> members;
}
