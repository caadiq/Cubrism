package com.credential.cubrism.server.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileImageUploadResultDTO {
    private boolean success;
    private String message;
    private String imageUrl;
}
