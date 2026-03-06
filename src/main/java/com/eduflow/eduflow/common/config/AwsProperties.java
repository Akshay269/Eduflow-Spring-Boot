package com.eduflow.eduflow.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "aws.s3")
@Data
public class AwsProperties {
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String region;
}
