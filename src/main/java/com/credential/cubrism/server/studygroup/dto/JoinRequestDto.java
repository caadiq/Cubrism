package com.credential.cubrism.server.studygroup.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class JoinRequestDto {
    private UUID memberId;
    private Long groupId;
    private String groupName;
    private String userName;
    private LocalDateTime requestDate;
}
