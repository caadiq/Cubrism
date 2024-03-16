package com.credential.cubrism.server.studygroup.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class StudyGroupCreatePostDTO {
    private String groupName;
    private String groupDescription;
    private int maxMembers;
    private List<Tags> tags;

    @Getter
    public static class Tags {
        private String tagName;
    }
}
