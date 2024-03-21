package com.credential.cubrism.server.s3.service;

import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.s3.dto.PresignedUrlDto;
import com.credential.cubrism.server.s3.utils.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Util s3Util;

    // Pre-Signed URL 생성
    public ResponseEntity<List<PresignedUrlDto>> presignedUrl(List<String> filePathList, List<String> fileNameList) {
        // filePath 배열과 fileName 배열의 크기가 일치하는지 확인
        if (filePathList.size() != fileNameList.size()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        List<PresignedUrlDto> results = IntStream.range(0, filePathList.size())
                .mapToObj(i -> {
                    String filePath = filePathList.get(i);
                    String fileName = fileNameList.get(i);

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
