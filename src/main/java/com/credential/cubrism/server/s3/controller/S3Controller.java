package com.credential.cubrism.server.s3.controller;

import com.credential.cubrism.server.s3.dto.PresignedUrlDto;
import com.credential.cubrism.server.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {
    private final S3Service s3Service;

    // Pre-Signed URL 생성
    @GetMapping("/pre-signed-url")
    public ResponseEntity<List<PresignedUrlDto>> presignedUrl(
            @RequestParam List<String> filePath,
            @RequestParam List<String> fileName
    ) {
        return s3Service.presignedUrl(filePath, fileName);
    }
}
