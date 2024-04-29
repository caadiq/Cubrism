package com.credential.cubrism.server.notification.dto;

import lombok.Getter;

@Getter
public class PushMessageDto {
    private String targetToken;
    private String title;
    private String body;
    private String type;
    private Long id;
}
