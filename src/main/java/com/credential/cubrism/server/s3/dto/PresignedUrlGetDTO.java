package com.credential.cubrism.server.s3.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PresignedUrlGetDTO {
    private List<Files> files;

    @Getter
    public static class Files {
        private String filePath;
        private String fileName;
    }
}
