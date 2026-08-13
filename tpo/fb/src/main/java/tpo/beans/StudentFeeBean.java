/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openfaces.event.AjaxActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.hibernate.annotation.StudentFeeDetails;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("StudentFeeBean")
@Transactional(readOnly = true)
@Scope("session")
public class StudentFeeBean {

	private Logger logger = LoggerFactory.getLogger(StudentFeeBean.class);

	private StudentFeeDetails studentFeeDetails;

	private String currentDocMode = CCPConstant.CREATE;

	@Autowired
	private SessionFactory sessionFactory;

	public void initFee(StudentFeeDetails studentFeeDetails) {
		this.studentFeeDetails = studentFeeDetails;
		currentDocMode = CCPConstant.UPDATE;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addFeeDetails() {
		try {
			if((studentFeeDetails.getRollNumber()== null || "".equals(studentFeeDetails.getRollNumber())) 
					||(studentFeeDetails.getAmountPaid()== null || "".equals(studentFeeDetails.getAmountPaid()))
					||(studentFeeDetails.getPaidOn()== null || "".equals(studentFeeDetails.getPaidOn()))
					||(studentFeeDetails.getAmountDue()== null || "".equals(studentFeeDetails.getAmountDue()))
					||(studentFeeDetails.getDueOn()== null || "".equals(studentFeeDetails.getDueOn()))){
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_enter_all_the_mandatory_fields_in","Form"));
				return;
			}
			Session session = sessionFactory.getCurrentSession();
			FeeTableBean bean = (FeeTableBean) TpoUtil.getManagedBean(FeeTableBean.class.getSimpleName());
			if (bean != null) {
				bean.setShowWarnig(false);
			}
			AdminUser user = AdminUser.getUser();
			if (user != null) {
				studentFeeDetails.setCreatedBy(user.getUserName());
			}
			if (studentFeeDetails.getDueOn().after(new Date())) {
				studentFeeDetails.setReminderOn(true);
			}
			if (CCPConstant.CREATE.equals(currentDocMode)) {
				Criteria criteria = session.createCriteria(StudentFeeDetails.class);
				criteria.add(Restrictions.ilike("rollNumber", "%" + studentFeeDetails.getRollNumber() + "%"));
				criteria.add(Restrictions.isNotNull("amountDue"));
				List<StudentFeeDetails> list = criteria.list();
				for (StudentFeeDetails detail : list) {
					detail.setAmountDue(null);
					detail.setDueOn(null);
					detail.setReminderOn(false);
					session.update(detail);
				}
				session.save(studentFeeDetails);
				currentDocMode = CCPConstant.UPDATE;
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("feedetailsAddedSuccessfully"));
			} else {
				session.update(studentFeeDetails);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("feedetailsUpdatedSuccessfully"));
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

	public StudentFeeDetails getStudentFeeDetails() {
		return studentFeeDetails;
	}

	public void setStudentFeeDetails(StudentFeeDetails studentFeeDetails) {
		this.studentFeeDetails = studentFeeDetails;
	}

	public String getCurrentDocMode() {
		return currentDocMode;
	}

	public void setCurrentDocMode(String currentDocMode) {
		this.currentDocMode = currentDocMode;
	}

	public void calculateDueAmount(AjaxActionEvent actionEvent) {
		if (CCPConstant.CREATE.equals(currentDocMode)) {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(StudentFeeDetails.class);
			criteria.add(Restrictions.eq("rollNumber", studentFeeDetails.getRollNumber()));
			// criteria.add(Restrictions.ne("dueOn",null));
			List<StudentFeeDetails> details = criteria.list();
			if (details != null && !details.isEmpty()) {
				for (StudentFeeDetails feeDetail : details) {
					if (feeDetail.getDueOn() != null) {
						studentFeeDetails.setDueOn(feeDetail.getDueOn());
						studentFeeDetails.setAmountDue(feeDetail.getAmountDue() - studentFeeDetails.getAmountPaid());
						break;
					}
				}
			}
		}
	}
}
