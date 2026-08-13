/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.openfaces.event.AjaxActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.admin.excel.ExcelHandler;
import tpo.hibernate.annotation.StudentFeeDetails;
import tpo.pdf.generator.PDFGenerator;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

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

	public void setFeeRecordToAdd(AjaxActionEvent event) {
		try {
			StudentFeeBean bean = (StudentFeeBean) TpoUtil.getManagedBean(StudentFeeBean.class.getSimpleName());
			if (bean != null) {
				bean.setCurrentDocMode(CCPConstant.CREATE);
				bean.setStudentFeeDetails(new StudentFeeDetails());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setFeeDetails(AjaxActionEvent event) {
		try {
			StudentFeeBean bean = (StudentFeeBean) TpoUtil.getManagedBean(StudentFeeBean.class.getSimpleName());
			if (selectedList != null && selectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Info12"));
				if (bean != null) {
					bean.setStudentFeeDetails(null);
				}
			} else {
				if (selectedList.size() > 1) {
					UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
					if (bean != null) {
						bean.setStudentFeeDetails(null);
					}
				} else {
					if (bean != null) {
						bean.initFee(selectedList.get(0));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void inIt() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(StudentFeeDetails.class);
			String userName = AdminUser.getUser().getUserName();
			List<String> userList = AdminUser.getUser().getUserList();
			criteria.add(Restrictions.in("createdBy", userList));
			String queryStr = "select count(id) from studentfeedetails where  createdBy in ("
					+ TpoUtil.getComaSeprateValue(userList) + ")";
			if (enrollmentNumber != null && !enrollmentNumber.equals("")) {
				criteria.add(Restrictions.ilike("rollNumber", "%" + enrollmentNumber + "%"));
				queryStr = queryStr + " and rollnumber like '%" + enrollmentNumber + "%'";
			}
			NativeQuery<BigInteger> query = session.createSQLQuery(queryStr);
			criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			feeDetailsList = criteria.list();
			/*
			 * if(showWarnig && feeDetailsList !=null && feeDetailsList.size() > 0 ){
			 * for(StudentFeeDetails detail : feeDetailsList){ if(detail.getDueOn() !=null
			 * && detail.getDueOn().before(new Date())){ Object param[] = new Object[3];
			 * param[0] = detail.getDueOn(); param[1] = detail.getRollNumber(); param[2] =
			 * detail.getAmountDue(); UIBackingBean
			 * .setErrorMessage(FbMessageUtil.getLabel("due_Date_Passed",param)); } } }
			 */
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	
	public void generateXls() {
		try {
			if (feeDetailsList != null && !feeDetailsList.isEmpty()) {
				String reportName = "FB_" + "FeeList_" + AdminUser.getUser().getUserName() + "_"
						+ TpoUtil.getDateToStringYYYYMMdd(new Date());
				TpoUtil.renderEXcelFile(ExcelHandler.generateFeeDetailsXls(
						(selectedList != null && selectedList.size() > 0) ? selectedList : feeDetailsList, reportName),
						reportName);
			} else {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("list_is_empty"));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void generateStudentXls() {
		try {
			if (feeDetailsList != null && !feeDetailsList.isEmpty()) {
				String reportName = "FB_" + "FeeList_" + feeDetailsList.get(0).getRollNumber() + "_"
						+ TpoUtil.getDateToStringYYYYMMdd(new Date());
				TpoUtil.renderEXcelFile(ExcelHandler.generateFeeDetailsXls(feeDetailsList, reportName),
						reportName);
			} else {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("list_is_empty"));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void generatePdfReport() {
		try {
			if (feeDetailsList != null && !feeDetailsList.isEmpty()) {
				pDFGenerator.generateFeeList(
						(selectedList != null && selectedList.size() > 0) ? selectedList : feeDetailsList);
			} else {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("list_is_empty"));
			}
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