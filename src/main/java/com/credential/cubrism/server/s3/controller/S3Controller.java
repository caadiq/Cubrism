package com.credential.cubrism.server.s3.controller;

import com.credential.cubrism.server.common.dto.ErrorDTO;
import com.credential.cubrism.server.s3.dto.PresignedUrlGetDTO;
import com.credential.cubrism.server.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {
    private final S3Service s3Service;

    @GetMapping("/presigned-url")
    public ResponseEntity<?> getPreSignedUrl(
            @RequestBody PresignedUrlGetDTO dto
    ) {
        try {
            return ResponseEntity.ok().body(s3Service.getPreSignedUrl(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO(e.getMessage()));
        }
    }
}
