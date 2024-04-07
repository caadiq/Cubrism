package com.credential.cubrism.server.s3.service;

import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.s3.dto.PresignedUrlDto;
import com.credential.cubrism.server.s3.dto.PresignedUrlRequestDto;
import com.credential.cubrism.server.s3.utils.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Util s3Util;

    // Pre-Signed URL 생성
    public ResponseEntity<List<PresignedUrlDto>> presignedUrl(List<PresignedUrlRequestDto> dtoList) {
        List<PresignedUrlDto> results = dtoList.stream()
                .map(dto -> {
                    String filePath = dto.getFilePath();
                    String fileName = dto.getFileName();

                    // filePath 또는 fileName이 비어있는지 확인
                    if (filePath.isEmpty() || fileName.isEmpty()) {
                        throw new CustomException(ErrorCode.INVALID_REQUEST);
                    }

                    try {
                        String fullPath = s3Util.createPath(filePath, fileName); // 파일 경로 생성
                        GeneratePresignedUrlRequest request = s3Util.generatePreSignedUrlRequest(fullPath); // Pre-Signed URL 요청 생성
                        String presignedUrl = s3Util.generatePresignedUrl(request); // Pre-Signed URL 생성
                        String fileUrl = s3Util.fileUrl(fullPath); // 파일 URL 생성
                        return new PresignedUrlDto(fileName, presignedUrl, fileUrl);
                    } catch (Exception e) {
                        throw new CustomException(ErrorCode.S3_PRE_SIGNED_URL_FAILURE);
                    }
                }).toList();

        return ResponseEntity.status(HttpStatus.OK).body(results);
    }
}
