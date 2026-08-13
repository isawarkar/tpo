/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.catalina.core.ApplicationPart;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.admin.excel.ExcelHandler;
import tpo.admin.excel.ExcelSheetReader;
import tpo.dao.CommonDBBean;
import tpo.hibernate.Registration;
import tpo.pdf.generator.PDFGenerator;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("UploadProgress")
@Transactional(readOnly = true)
@Scope("request")
public class UploadProgress {

	private Logger logger = LoggerFactory.getLogger(UploadProgress.class);

	private String errorMessage = null;

	private ApplicationPart file;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private ExcelSheetReader excelSheetReader;
	
	@Autowired
	private PDFGenerator pDFGenerator;

	public void exportFile() {
		List<Registration> list = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Registration.class);
			criteria.createAlias("percentageinfo", "percentageinfo");
			criteria.add(Restrictions.ne("percentageinfo.rollnumber", ""));
			CommonDBBean commonDBBean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
			if (commonDBBean != null) {
				List<String> collegeList = AdminUser.getUser().getCollegeList();
				if (collegeList != null && collegeList.size() > 0) {
					criteria.add(Restrictions.in("collegeName", collegeList));
					list = criteria.list();
					if (list != null && list.size() > 0) {
						String reportName = "FB_" +  "StudentList_" + AdminUser.getUser().getUserName() + "_" + TpoUtil.getDateToStringYYYYMMdd(new Date());
						TpoUtil.renderEXcelFile(ExcelHandler.generateStudentList(list, reportName),reportName);
					} else {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("No_Record_Found"));
					}
				} else {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error2));
				}
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void exportToPdfFile() {
		List<Registration> list = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Registration.class);
			criteria.createAlias("percentageinfo", "percentageinfo");
			criteria.add(Restrictions.ne("percentageinfo.rollnumber", ""));
			CommonDBBean commonDBBean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
			if (commonDBBean != null) {
				List<String> collegeList = AdminUser.getUser().getCollegeList();
				if (collegeList != null && collegeList.size() > 0) {
					criteria.add(Restrictions.in("collegeName", collegeList));
					list = criteria.list();
					if (list != null && list.size() > 0) {
						pDFGenerator.generateStudentList(list);

						list = null;
					} else {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("No_Record_Found"));
					}
				} else {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error2));
				}
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void importFile() throws IOException {
		List<Registration> list = null;

		if (!"".equals(file.getSubmittedFileName())) {
			if (file.getSize() > 0) {
				list = excelSheetReader.readExcelFile(file.getInputStream(), null, null, null);
				try {
					if (list != null && list.size() > 0) {
						Session session = sessionFactory.getCurrentSession();
						for (Registration registration : list) {
							session.saveOrUpdate(registration);
							session.saveOrUpdate(registration.getPersonalinfo());
							session.saveOrUpdate(registration.getPercentageinfo());
							session.saveOrUpdate(registration.getBackdetails());
							session.saveOrUpdate(registration.getContactinfo());
							session.saveOrUpdate(registration.getAchivements());
						}
						UIBackingBean.setSuccessMessage(
								FbMessageUtil.getLabel("Records_are_successfully_imported", list.size()));
					}
				} catch (HibernateException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
			}
		} else {
			UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_xls_file"));
		}

	}

	public ApplicationPart getFile() {
		return file;
	}

	public void setFile(ApplicationPart file) {
		this.file = file;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public ExcelSheetReader getExcelSheetReader() {
		return excelSheetReader;
	}

	public void setExcelSheetReader(ExcelSheetReader excelSheetReader) {
		this.excelSheetReader = excelSheetReader;
	}

}