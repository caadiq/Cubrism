package com.credential.cubrism.server.studygroup.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class StudyGroupCreateDto {
    private String groupName;
    private String groupDescription;
    private int maxMembers;
    private List<String> tags;
}
