package tpo.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import tpo.email.EmailUtil;
import tpo.util.SystemUtil;

@Component("parent")
public class Parent {

	@Autowired
	private EmailUtil emailUtil;

	public static String imageService;
	public static String fileService;

	public static String examService;

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

	public String getExamServiceUrl() {
		try {
			if (examService == null && discoveryClient != null) {
				InstanceInfo instance = discoveryClient.getNextServerFromEureka("EXAM", false);
				examService = instance.getHomePageUrl() + "login/studentlogin.xhtml";

			}
		} catch (RuntimeException e) {
		}
		return examService;
	}

	public static String studentService;

	public String getStudentServiceUrl() {
		try {
			if (studentService == null && discoveryClient != null) {
				InstanceInfo instance = discoveryClient.getNextServerFromEureka("STUDENT", false);
				studentService = instance.getHomePageUrl() + "login/login.xhtml";
			}
		} catch (RuntimeException e) {
		}
		return studentService;
	}
}
