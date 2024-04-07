package com.credential.cubrism.server.s3.controller;

import com.credential.cubrism.server.s3.dto.PresignedUrlDto;
import com.credential.cubrism.server.s3.dto.PresignedUrlRequestDto;
import com.credential.cubrism.server.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {
    private final S3Service s3Service;

    // Pre-Signed URL 생성
    @PostMapping("/pre-signed-url")
    public ResponseEntity<List<PresignedUrlDto>> presignedUrl(@RequestBody List<PresignedUrlRequestDto> dto) {
        return s3Service.presignedUrl(dto);
    }
}
