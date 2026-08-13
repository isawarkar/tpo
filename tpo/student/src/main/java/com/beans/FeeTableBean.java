/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.beans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.annotation.StudentFeeDetails;
import com.pdf.generator.PDFGenerator;
import com.util.FbMessageUtil;
import com.util.ResourceID;

/**
 * @author Uddanda Technologies
 */
@Repository("FeeTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class FeeTableBean {

	private Logger logger = LoggerFactory.getLogger(FeeTableBean.class);

	private String enrollmentNumber;

	private Boolean showWarnig;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private Pagination pagination;
	
	@Autowired
	private PDFGenerator pDFGenerator;

	private List<StudentFeeDetails> feeDetailsList;
	private List<StudentFeeDetails> selectedList = new ArrayList<StudentFeeDetails>();

	public void updateRecord() {

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSeletedRecord() {
		showWarnig = false;
		try {
			if (selectedList != null && selectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (StudentFeeDetails feeDetails : selectedList) {
					session.delete(feeDetails);
				}
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("feedetailsDeletedSuccessfully"));
				feeDetailsList = null;
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
	public void setReminderOn() {
		setShowWarnig(false);
		try {
			if (selectedList != null && selectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (StudentFeeDetails feeDetails : selectedList) {
					feeDetails.setReminderOn(true);
					session.update(feeDetails);
				}
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("fee_Reminder_Set_Successfully"));
				feeDetailsList = null;
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
	public void setReminderOff() {
		setShowWarnig(false);
		try {
			if (selectedList != null && selectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (StudentFeeDetails feeDetails : selectedList) {
					feeDetails.setReminderOn(false);
					session.update(feeDetails);
				}
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("fee_Reminder_removed_Successfully"));
				feeDetailsList = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void searchRecord() {
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
		showWarnig = true;
	}

	public String getEnrollmentNumber() {
		return enrollmentNumber;
	}

	public void setEnrollmentNumber(String enrollmentNumber) {
		this.enrollmentNumber = enrollmentNumber;
	}


	

	public void inItStudent() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(StudentFeeDetails.class);
			String rollNumber = Student.getStudent().getRollNumber();
			criteria.add(Restrictions.eq("rollNumber", rollNumber));
			String queryStr = "select count(id) from studentfeedetails where rollnumber='" + rollNumber + "'";
			NativeQuery<BigInteger> query = session.createSQLQuery(queryStr);
			criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			feeDetailsList = criteria.list();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	
	
	

	public List<StudentFeeDetails> getFeeDetailsList() {
		return feeDetailsList;
	}

	public void setFeeDetailsList(List<StudentFeeDetails> feeDetailsList) {
		this.feeDetailsList = feeDetailsList;
	}

	public List<StudentFeeDetails> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(List<StudentFeeDetails> selectedList) {
		this.selectedList = selectedList;
	}

	public Boolean getShowWarnig() {
		return showWarnig;
	}

	public void setShowWarnig(Boolean showWarnig) {
		this.showWarnig = showWarnig;
	}

}