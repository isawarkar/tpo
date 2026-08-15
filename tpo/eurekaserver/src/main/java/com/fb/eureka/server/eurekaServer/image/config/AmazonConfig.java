package com.fb.eureka.server.eurekaServer.image.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fb.eureka.server.eurekaServer.util.SystemUtil;

@Configuration
public class AmazonConfig {
    
    @Bean
    public AmazonS3 s3() {
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(SystemUtil.getLabel("region"))
                .build();
    }
}
