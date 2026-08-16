package com.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.email.EmailUtil;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;


@Component("parent")
public class Parent {

	@Autowired
	private EmailUtil emailUtil;
	
	public static String imageService;
	public static String fileService;
	
	public static String fbService;
	
	@Value("${dbBackups3bucketName}")
	public String bucketName;

	@Value("${jdbc.schemaName}")
	public String jdbc_SchemaName;

	@Value("${jdbc.username}")
	public String jdbc_Username;

	@Value("${jdbc.password}")
	public String jdbc_Password;

	@Value("${jdbc.driverClassName}")
	public String jdbc_DriverClassName;

	@Value("${jdbc.url}")
	public String jdbc_Url;

	@Value("${superUserEmail}")
	public String superUserEmail;

	@Value("${supperUser}")
	public String supperUser;

	@Value("${defaultPass}")
	public String defaultPass;

	@Value("${defaultLocal}")
	public String defaultLocal;

	@Value("${envirnment}")
	public String envirnment;

	@Value("${expiryString}")
	public String expiryString;

	@Value("${hibernate.show_sql}")
	public boolean hibernate_ShowSql;

	@Value("${cronExpression}")
	public String cronExpression;

	@Value("${dataLoaderExpression}")
	public String dataLoaderExpression;

	@Value("${enableReferralReward}")
	public String enableReferralReward;

	@Value("${enableStudentFeeReminder}")
	public String enableStudentFeeReminder;

	@Value("${sendBirthdayEmail}")
	public String sendBirthdayEmail;

	@Value("${version}")
	public String version;

	@Value("${dbBackupEnable}")
	public String dbBackupEnable;

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
	
	public String getImageServiceUrl() {
		try {
			if (imageService == null) {
				String s3Url = discoveryClient.getServiceUrlsFromConfig("eureka.client.serviceUrl.defaultZone", true).get(0);
				s3Url = s3Url.replace("/eureka", "");
				imageService = s3Url + "imageService";

			}
		} catch (RuntimeException e) {
		}
		return imageService;
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
