package com.credential.cubrism.server.s3.utils;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Util {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    // presigned url request 생성
    public GeneratePresignedUrlRequest generatePreSignedUrlRequest(String filePath) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, filePath)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(preSignedUrlExpiration());
        generatePresignedUrlRequest.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());
        return generatePresignedUrlRequest;
    }

    // presigned url 생성
    public String generatePresignedUrl(GeneratePresignedUrlRequest generatePresignedUrlRequest) {
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    // 파일 경로 생성
    public String createPath(String filePath, String fileName) {
        String fileId = UUID.randomUUID().toString();
        return String.format("%s/%s_%s", filePath, fileId, fileName); // 폴더/UUID_파일명
    }

    // 파일 url
    public String fileUrl(String filePath) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucket, filePath);
    }

    // 파일 삭제
    public void deleteFile(String fileUrl) {
        String bucketName = fileUrl.split(".s3.amazonaws.com")[0].replace("https://", "");
        String keyName = fileUrl.split(".s3.amazonaws.com/")[1];
        amazonS3.deleteObject(bucketName, keyName);
    }

    // 파일 확인
    public boolean isFileExists(String fileUrl) {
        String bucketName = fileUrl.split(".s3.amazonaws.com")[0].replace("https://", "");
        String keyName = fileUrl.split(".s3.amazonaws.com/")[1];
        return amazonS3.doesObjectExist(bucketName, keyName);
    }

    // presigned url 유효 기간 설정
    private Date preSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2; // 2분
        expiration.setTime(expTimeMillis);
        return expiration;
    }
}
