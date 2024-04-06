package com.credential.cubrism.server.studygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class StudyGroupJoinRequestDto {
    private UUID memberId;
    private Long groupId;
    private String groupName;
    private String userName;
    private LocalDateTime requestDate;
}
