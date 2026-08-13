package com.fb.image.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fb.util.AES;
import com.fb.util.SystemUtil;

@Configuration
public class AmazonConfig {
    @Bean
    public AmazonS3 s3() {
        AWSCredentials awsCredentials =
                new BasicAWSCredentials(AES.symmetricDecrypt(SystemUtil.getLabel("S3Key"), SystemUtil.getLabel("key")),AES.symmetricDecrypt(SystemUtil.getLabel("S3Key1"), SystemUtil.getLabel("key")));
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(SystemUtil.getLabel("region"))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

    }
}
