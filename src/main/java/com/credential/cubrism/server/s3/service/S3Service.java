package com.credential.cubrism.server.s3.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.credential.cubrism.server.s3.dto.PresignedUrlGetDTO;
import com.credential.cubrism.server.s3.dto.PresignedUrlResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    public PresignedUrlResultDTO getPreSignedUrl(PresignedUrlGetDTO dto) {
        try {
            String fileName = createPath(dto.getFilePath(), dto.getFileName());
            GeneratePresignedUrlRequest generatePresignedUrlRequest = generatePreSignedUrlRequest(bucket, fileName);
            String presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
            String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucket, fileName);
            return new PresignedUrlResultDTO(presignedUrl, fileUrl);
        } catch (Exception e) {
            throw new IllegalArgumentException("Presigned URL 생성에 실패했습니다");
        }
    }

    // presigned url 생성
    private GeneratePresignedUrlRequest generatePreSignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, fileName)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(preSignedUrlExpiration());
        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString());
        return generatePresignedUrlRequest;
    }

    // presigned url 유효 기간 설정
    private Date preSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2; // 2분
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    // 파일 경로 생성
    private String createPath(String filePath, String fileName) {
        String fileId = UUID.randomUUID().toString();
        return String.format("%s/%s_%s", filePath, fileId, fileName);
    }


    public void deleteFileFromS3(String fileUrl) {
        String bucketName = fileUrl.split(".s3.amazonaws.com")[0].replace("https://", "");
        String keyName = fileUrl.split(".s3.amazonaws.com/")[1];
        amazonS3.deleteObject(bucketName, keyName);
    }
}
