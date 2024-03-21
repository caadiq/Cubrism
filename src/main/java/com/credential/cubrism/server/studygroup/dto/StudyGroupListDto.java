package com.credential.cubrism.server.studygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StudyGroupListDto {
    private Pageable page;
    private List<StudyGroupList> studyGroupList;

    @Getter
    @AllArgsConstructor
    public static class Pageable {
        private Integer previousPage;
        private int currentPage;
        private Integer nextPage;
    }

    @Getter
    @AllArgsConstructor
    public static class StudyGroupList {
        private Long studyGroupId;
        private String groupName;
        private String groupDescription;
        private int currentMembers;
        private int maxMembers;
        private boolean isRecruiting;
        private List<String> tags;
    }
}
