package com.credential.cubrism.server.studygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class StudyGroupJoinListDto {
    private UUID memberId;
    private String groupName;
    private String userName;
    private String userImage;
    private LocalDateTime requestDate;
}
