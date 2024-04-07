package com.credential.cubrism.server.s3.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PresignedUrlRequestDto {
    private List<String> filePath;
    private List<String> fileName;
}
