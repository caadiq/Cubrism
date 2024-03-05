package com.credential.cubrism.server.authentication.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.authentication.utils.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProfileImageService {
    private final AmazonS3 s3;
    private final String bucketName;
    private final UserRepository userRepository;

    private final static long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private final static String filePath = "profile_images/";

    @Autowired
    public ProfileImageService(AmazonS3 s3, @Value("${cloud.aws.s3.bucket}") String bucketName, UserRepository userRepository) {
        this.s3 = s3;
        this.bucketName = bucketName;
        this.userRepository = userRepository;
    }

    public String uploadProfileImage(MultipartFile file, Authentication authentication) throws IOException {
        String fileType = file.getContentType();
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);
        UUID uuid = user.getUuid();

        if (file.getSize() > MAX_FILE_SIZE) { // 파일의 크기를 10MB로 제한
            throw new RuntimeException("10MB 이하의 이미지만 업로드 가능합니다");
        }

        if (!Objects.requireNonNull(fileType).startsWith("image")) { // MIME 타입을 image로 제한
            throw new RuntimeException("이미지 파일만 업로드 가능합니다");
        }

        String originalFileName = file.getOriginalFilename(); // 원본 파일명
        String extension = Objects.requireNonNull(originalFileName).substring(originalFileName.lastIndexOf(".")); // 파일 확장자

        // S3 버킷에 해당 uuid로 저장된 파일이 있으면 삭제
        ObjectListing objectListing = s3.listObjects(bucketName, filePath + uuid.toString());
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            s3.deleteObject(bucketName, objectSummary.getKey());
        }

        String fileName = filePath + uuid + extension; // 파일 이름

        ObjectMetadata metadata = new ObjectMetadata(); // 파일 메타데이터
        metadata.setContentLength(file.getSize()); // 파일 크기
        metadata.setContentType(fileType); // 파일 MIME 타입

        s3.putObject(bucketName, fileName, file.getInputStream(), metadata); // S3 버킷에 파일 업로드
        return s3.getUrl(bucketName, fileName).toString();
    }
}
