package com.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.email.EmailUtil;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.util.SystemUtil;

@Component("parent")
public class Parent {

	@Autowired
	private EmailUtil emailUtil;

	public static String fileService;
	public static String fbService;

	public String getFBServiceUrl() {
		if (fbService == null && discoveryClient != null) {
			try {
				InstanceInfo instance = discoveryClient.getNextServerFromEureka("FB", false);
				fbService = instance.getHomePageUrl() + "FB";
			} catch (RuntimeException e) {
			}
		}
		return fbService;
	}

	@Autowired
	private EurekaClient discoveryClient;

	public EmailUtil getEmailInstance() {
		if (emailUtil != null && !"".equals(emailUtil.getEnvirnment())) {
			return emailUtil;
		} else {
			return null;
		}

	}

	

	public String getFileServiceUrl() {
		try {
			if (fileService == null) {
				String s3Url = discoveryClient.getServiceUrlsFromConfig("eureka.client.serviceUrl.defaultZone", true).get(0);
				s3Url = s3Url.replace("/eureka", "");
				fileService = s3Url + "fileService";

			}
		} catch (RuntimeException e) {
		}
		return fileService;
	}

}
