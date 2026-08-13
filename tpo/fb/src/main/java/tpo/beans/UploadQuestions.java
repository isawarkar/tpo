/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.catalina.core.ApplicationPart;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.admin.excel.ExamSheetReader;
import tpo.admin.excel.ExcelHandler;
import tpo.hibernate.Questions;
import tpo.util.FbMessageUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("UploadQuestions")
@Transactional(readOnly = true)
@Scope("request")
public class UploadQuestions {

	private Logger logger = LoggerFactory.getLogger(UploadQuestions.class);

	private ApplicationPart file;

	private String examName;

	private List<String> examList = null;

	@Autowired
	private SessionFactory sessionFactory;

	public void exportFile() {
		List<Questions> list = null;
		byte[] bArray = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Questions.class);
			NativeQuery<String> query = null;
			List<String> examList = null;
			if (examName == null || "".equals(examName)) {
				AdminUser user = AdminUser.getUser();
				if (user != null && user.getUserName() != null) {
					query = session
							.createSQLQuery("select testname from exam where createdBy = '"
									+ user.getUserName() + "'");
				}
				examList = query.list();
			} else {
				examList = new ArrayList<String>(1);
				examList.add(examName);
			}
			if (examList != null && examList.size() > 0) {
				criteria.add(Restrictions.in("id.qtype", examList));
				list = criteria.list();
				if (list != null) {
					String reportName = "FB_" +  "QList_" + AdminUser.getUser().getUserName() + "_" + TpoUtil.getDateToStringYYYYMMdd(new Date());
					bArray =ExcelHandler.generateExam(list, reportName);
					TpoUtil.renderEXcelFile(bArray,reportName);
				} else {
					UIBackingBean
							.setErrorMessage(FbMessageUtil.getLabel("Record_List_is_Null"));
				}
			} else {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Exams_are_not_available"));
			}

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			list = null;
			bArray = null;
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void importFile() {
		List<Questions> list = null;
		try {
			if (!"".equals(file.getSubmittedFileName())) {
				if (file.getSize() > 0) {
					ExamSheetReader ExamSheetReader = new ExamSheetReader();
					list = ExamSheetReader.readExcelFile(file.getInputStream());
					if (list != null) {
						Session session = sessionFactory.getCurrentSession();
						for (Questions questions : list) {
							session.saveOrUpdate(questions);
						}
						UIBackingBean
								.setSuccessMessage(FbMessageUtil.getLabel("Records_are_successfully_imported"));
					} else {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_enter_correct_answer"));
					}
				}
			} else {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_xls_file"));
			}
		} catch (ConstraintViolationException e) {
			UIBackingBean.setErrorMessage(e.getMessage() + e.getSQLException());
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			list = null;
		}
	}

	/**
	 * @return the examName
	 */
	public String getExamName() {
		return examName;
	}

	/**
	 * @param examName
	 *            the examName to set
	 */
	public void setExamName(String examName) {
		this.examName = examName;
	}

	public List<String> getExamList() {
		try {
			if (examList == null && AdminUser.getUser().getRole() != null) {
				Session session = sessionFactory.getCurrentSession();
				String queryStr = null;
				AdminUser user = AdminUser.getUser();
				if (user != null && user.getUserName() != null) {
					queryStr = "select testname from exam where createdBy = '"
							+ user.getUserName() + "'";
				}
				NativeQuery query;
				query = session.createNativeQuery(queryStr);
				examList = query.getResultList();
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return examList;
	}

	public ApplicationPart getFile() {
		return file;
	}

	public void setFile(ApplicationPart file) {
		this.file = file;
	}

}