package com.credential.cubrism.server.common.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3ImageUploadUtil {
    private final AmazonS3 s3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<String> uploadImages(String type, List<MultipartFile> files, long maxImageSize, String filePath, UUID userId) {
        // 파일 유효성 검사
        for (MultipartFile file : files) {
            validateFile(file, maxImageSize);
        }

        // 파일 업로드
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = uploadFileToS3(type, file, filePath, userId);
            urls.add(url);
        }

        return urls;
    }

    private void validateFile(MultipartFile file, long maxImageSize) {
        String originalFileName = file.getOriginalFilename();
        String fileType;
        try {
            fileType = Files.probeContentType(Paths.get(Objects.requireNonNull(originalFileName)));
        } catch (IOException e) {
            throw new IllegalArgumentException("MIME 타입을 확인할 수 없습니다");
        }

        if (file.getSize() > maxImageSize * 1024 * 1024) {
            throw new IllegalArgumentException(maxImageSize + "MB 이하의 이미지만 업로드 가능합니다");
        }

        if (!Objects.requireNonNull(fileType).startsWith("image")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다");
        }
    }

    private String uploadFileToS3(String type, MultipartFile file, String filePath, UUID userId) {
        String originalFileName = file.getOriginalFilename(); // 원본 파일명
        String extension = Objects.requireNonNull(originalFileName).substring(originalFileName.lastIndexOf(".")); // 파일 확장자
        String fileName;

        if (type.equals("profile")) { // 프로필 이미지
            // S3 버킷에 유저id로 저장된 파일이 있으면 삭제
            ObjectListing objectListing = s3.listObjects(bucketName, filePath + userId.toString());
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                s3.deleteObject(bucketName, objectSummary.getKey());
            }
            fileName = filePath + userId + extension;
        } else if (type.equals("post")) { // 게시글 이미지
            fileName = filePath + userId + "_" + UUID.randomUUID() + extension;
        } else {
            throw new IllegalArgumentException("지원하지 않는 타입입니다");
        }

        ObjectMetadata metadata = new ObjectMetadata(); // 파일 메타데이터
        metadata.setContentLength(file.getSize()); // 파일 크기
        metadata.setContentType(file.getContentType()); // 파일 MIME 타입

        try {
            s3.putObject(bucketName, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new IllegalArgumentException("이미지 업로드에 실패했습니다");
        }

        return s3.getUrl(bucketName, fileName).toString();
    }
}
