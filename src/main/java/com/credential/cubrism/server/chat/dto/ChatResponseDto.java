package com.credential.cubrism.server.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ChatResponseDto {
    private UUID id;
    private String email;
    private String username;
    private String profileImgUrl;
    private LocalDateTime createdAt;
    private String content;
    private Boolean isDateHeader;
}
