package com.fb.eureka.server.eurekaServer.image.config;

import com.fb.eureka.server.eurekaServer.util.SystemUtil;

public interface BucketName {

	String bucketName = SystemUtil.getLabel("bucketName");
}
