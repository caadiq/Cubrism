package com.credential.cubrism.server.s3.dto;

import lombok.Getter;

@Getter
public class PresignedUrlRequestDto {
    private String filePath;
    private String fileName;
}
