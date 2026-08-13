package com.beans;

import java.io.IOException;
import java.io.Serializable;

import org.apache.catalina.core.ApplicationPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dao.CommonDBBean;
import com.hibernate.Registration;
import com.util.FbMessageUtil;
import com.util.TpoUtil;

@Component("DynamicImageByteArrayBean")
@Scope("session")
public class DynamicImageByteArrayBean implements Serializable {

	private Logger logger = LoggerFactory.getLogger(DynamicImageByteArrayBean.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationPart file;
	private ApplicationPart resume;
	byte[] imageArray = null;

	public byte[] getImageBytes() {
		try {
			if (imageArray == null) {
				StudentRegistrationBean bean = (StudentRegistrationBean) TpoUtil
						.getManagedBean(StudentRegistrationBean.class.getSimpleName());
				if (bean != null && bean.getRegistration() != null) {
					CommonDBBean commonDBBean = (CommonDBBean) TpoUtil
							.getManagedBean(CommonDBBean.class.getSimpleName());
					if (commonDBBean != null) {
						imageArray = commonDBBean.getStudentProfilePic(bean.getRegistration().getRollnumber());
						if (imageArray == null) {
							imageArray = TpoUtil.convertInputStreamToBytesArray(TpoUtil.getNAFile());
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return imageArray;
	}

	public void uploadFile() {
		try {
			if (TpoUtil.doImageUploadValidation(file)) {
				StudentRegistrationBean bean = (StudentRegistrationBean) TpoUtil
						.getManagedBean(StudentRegistrationBean.class.getSimpleName());
				CommonDBBean commonDBBean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
				if (commonDBBean != null) {
					byte[] a = TpoUtil.convertInputStreamToBytesArray(file.getInputStream());
					if (commonDBBean.uploadStudentProfilePic(bean.getRegistration().getRollnumber(), a)) {
						UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Your_photo_is_uploaded_successfully"));
						imageArray = a;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			file = null;
		}
	}

	public void uploadResumeFile() {
		try {
			if (TpoUtil.doUploadResumeValidation(resume)) {
				StudentRegistrationBean bean = (StudentRegistrationBean) TpoUtil
						.getManagedBean(StudentRegistrationBean.class.getSimpleName());
				CommonDBBean commonDBBean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
				if (commonDBBean != null) {
					Registration registration = bean.getRegistration();
					registration.setPersonalinfo(bean.getPersonalinfo());
					if (commonDBBean.uploadStudentResume(registration,
							TpoUtil.convertInputStreamToBytesArray(resume.getInputStream()),
							resume.getSubmittedFileName(),TpoUtil.getFileExt(resume.getContentType()))) {
						UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Your_resume_is_uploaded_successfully"));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			resume = null;
		}
	}

	public ApplicationPart getFile() {
		return file;
	}

	public void setFile(ApplicationPart file) {
		this.file = file;
	}

	public ApplicationPart getResume() {
		return resume;
	}

	public void setResume(ApplicationPart resume) {
		this.resume = resume;
	}

	public byte[] getCompnayBytes() {
		try {
			if (imageArray == null) {
				StudentRegistrationBean bean = (StudentRegistrationBean) TpoUtil
						.getManagedBean(StudentRegistrationBean.class.getSimpleName());
				if (bean != null && bean.getRegistration() != null) {
					CommonDBBean commonDBBean = (CommonDBBean) TpoUtil
							.getManagedBean(CommonDBBean.class.getSimpleName());
					if (commonDBBean != null) {
						imageArray = commonDBBean.getStudentProfilePic(bean.getRegistration().getRollnumber());
						if (imageArray == null) {
							imageArray = TpoUtil.convertInputStreamToBytesArray(TpoUtil.getNAFile());
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return imageArray;
	}
}