package com.credential.cubrism.server.authentication.dto;

import lombok.Getter;

@Getter
public class SocialLogInDto {
    private String token;
    private String fcmToken;
}
