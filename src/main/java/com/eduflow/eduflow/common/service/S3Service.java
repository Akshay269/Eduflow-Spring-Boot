package com.eduflow.eduflow.common.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eduflow.eduflow.common.config.AwsProperties;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public String uploadFile(MultipartFile file, String folder) {
        String fileName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(awsProperties.getBucketName())
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

                    
                          s3Client.putObject(putRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
                  
          

            return getFileUrl(fileName);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage());
        }
    }

    public void deleteFile(String fileUrl) {
        String key = fileUrl.substring(fileUrl.indexOf(".com/") + 5);  // extract key from URL

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(awsProperties.getBucketName())
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    private String getFileUrl(String fileName) {
        return "https://" + awsProperties.getBucketName()
                + ".s3." + awsProperties.getRegion()
                + ".amazonaws.com/" + fileName;
    }
}
