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
public class PostImageUploadUtil {
    private final AmazonS3 s3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final static long MAX_FILE_SIZE = 5;
    private final static String filePath = "post_images/";

    public List<String> uploadImages(List<MultipartFile> files, Long postId) {
        // 파일 유효성 검사
        for (MultipartFile file : files) {
            validateFile(file);
        }

        // 파일 업로드
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = uploadImageToS3(file, postId);
            urls.add(url);
        }

        return urls;
    }

    private void validateFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String fileType;

        try {
            fileType = Files.probeContentType(Paths.get(Objects.requireNonNull(originalFileName)));
        } catch (IOException e) {
            throw new IllegalArgumentException("MIME 타입을 확인할 수 없습니다");
        }

        if (file.getSize() > MAX_FILE_SIZE * 1024 * 1024) {
            throw new IllegalArgumentException(MAX_FILE_SIZE + "MB 이하의 이미지만 업로드 가능합니다");
        }

        if (!Objects.requireNonNull(fileType).startsWith("image")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다");
        }
    }

    private String uploadImageToS3(MultipartFile file, Long postId) {
        String fileName = getImageName(file, postId);
        removePreviousImage(fileName, postId);
        uploadImage(file, fileName);
        return s3.getUrl(bucketName, fileName).toString();
    }

    private String getImageName(MultipartFile file, Long postId) {
        String originalFileName = Objects.requireNonNull(file.getOriginalFilename()).replace("{", "").replace("}", "");
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return filePath + postId + "_" + UUID.randomUUID() + "_{" + originalFileName + "}" + extension;
    }

    private void removePreviousImage(String fileName, Long postId) {
        String targetKey = postId + "_.*_\\{" + fileName + "\\}";
        ObjectListing objectListing = s3.listObjects(bucketName, filePath);

        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            if (objectSummary.getKey().contains(targetKey)) {
                s3.deleteObject(bucketName, objectSummary.getKey());
            }
        }
    }

    private void uploadImage(MultipartFile file, String fileName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            s3.putObject(bucketName, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new IllegalArgumentException("이미지 업로드에 실패했습니다");
        }
    }
}