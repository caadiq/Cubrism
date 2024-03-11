package com.credential.cubrism.server.s3.dto;

import lombok.Getter;

@Getter
public class PresignedUrlGetDTO {
    private String filePath;
    private String fileName;
}
