package com.credential.cubrism.server.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDto {
    private String targetToken;
    private String title;
    private String body;
}
