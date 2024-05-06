package com.credential.cubrism.server.studygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class StudyGroupJoinListDto {
    private UUID memberId;
    private String groupName;
    private String groupDescription;
    private List<String> tags;
    private String groupAdmin;
    private String groupAdminProfileImage;
    private String requestDate;
}
