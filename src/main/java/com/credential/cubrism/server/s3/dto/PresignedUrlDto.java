package com.credential.cubrism.server.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlDto {
    private String fileName;
    private String presignedUrl;
    private String fileUrl;
}
