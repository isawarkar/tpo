/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import java.io.IOException;
import java.util.Calendar;

import org.apache.catalina.core.ApplicationPart;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.hibernate.Company;
import tpo.imageservice.client.FileUploadUtility;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.IMAGECONS;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("CompanyBean")
@Transactional(readOnly = true)
@Scope("session")
public class CompanyBean extends Parent{

	private Logger logger = LoggerFactory.getLogger(CompanyBean.class);

	private Company company;

	private String currentDocMode = CCPConstant.CREATE;

	private ApplicationPart logo;
	
	@Autowired
	private FileUploadUtility fileUploadUtility;

	@Autowired
	private SessionFactory sessionFactory;

	public void initCompany(Company company) {
		this.company = company;
		this.currentDocMode = CCPConstant.UPDATE;
	}
	
	public void viewCompany(Company company) {
		this.company = company;
		this.currentDocMode = CCPConstant.VIEW;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addCompany() {
		try {
			Session session = sessionFactory.getCurrentSession();
			
			if (company.getCompanyname().contains(",")) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_remove_comma_from_Company_Name"));
				return;
			}

		
			if (CCPConstant.CREATE.equals(currentDocMode)) {
				AdminUser user = AdminUser.getUser();
				if (user != null) {
					company.setCreatedBy(user.getUserName());
				}
				company.setCompanyID(TpoUtil.get6DigitRandomNumber());
				company.setDateofvisit(Calendar.getInstance().getTime());
				company.setTotal(0);
				session.save(company);
				setCompanyLogo();
				currentDocMode = CCPConstant.UPDATE;
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success2));
			} else {
				session.update(company);
				setCompanyLogo();
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success8));
			}

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private void setCompanyLogo() throws IOException {
		if (logo != null && logo.getSize() > 0) {
				if (TpoUtil.imageTypes.contains(logo.getContentType())) {
					fileUploadUtility.uploadFileWithByteArray(getImageServiceUrl()+"/upload", String.valueOf(company.getCompanyID()), TpoUtil.convertInputStreamToBytesArray(logo.getInputStream()),IMAGECONS.company);
				} else {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error21));
			}
		}
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getCurrentDocMode() {
		return currentDocMode;
	}

	public void setCurrentDocMode(String currentDocMode) {
		this.currentDocMode = currentDocMode;
	}

	public ApplicationPart getLogo() {
		return logo;
	}

	public void setLogo(ApplicationPart logo) {
		this.logo = logo;
	}

}
