package com.eduflow.eduflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.eduflow.eduflow.common.config.AwsProperties;

@SpringBootApplication
@EnableConfigurationProperties(AwsProperties.class)

public class EduflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(EduflowApplication.class, args);
	}

}
