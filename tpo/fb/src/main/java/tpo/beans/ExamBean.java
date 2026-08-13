/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.hibernate.Exam;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;

/**
 * @author Uddanda Technologies
 */
@Repository("ExamBean")
@Transactional(readOnly = true)
@Scope("session")
public class ExamBean {

	private Logger logger = LoggerFactory.getLogger(ExamBean.class);

	@Autowired
	private SessionFactory sessionFactory;

	private Exam exam;

	private String currentDocMode = CCPConstant.CREATE;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addExam() {
		try {
			Session session = sessionFactory.getCurrentSession();
			if(CCPConstant.PERCENT.equals(exam.getResultType()) && exam.getPassingcriteria() >100){
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Passing_Criteria_Message"));
				return;
			}
			if(CCPConstant.PERCENT.equals(exam.getResultType()) && exam.getFirstClassMark()>100){
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("firstClass_Criteria_Message"));
				return;
			}
			if(CCPConstant.PERCENT.equals(exam.getResultType()) && exam.getHonoursMark() >100){
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("honors_Criteria_Message"));
				return;
			}
			
			if(exam.getFirstClassMark() !=0 && exam.getFirstClassMark() < exam.getPassingcriteria()){
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("firstClass1_Criteria_Message"));
				return;
			}
			if(exam.getFirstClassMark() !=0 && exam.getFirstClassMark() > exam.getHonoursMark()){
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("firstClass2_Criteria_Message"));
				return;
			}
			
			if(exam.getHonoursMark() !=0 && (exam.getHonoursMark() < exam.getPassingcriteria() || exam.getHonoursMark() < exam.getFirstClassMark())){
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("honors1_Criteria_Message"));
				return;
			}
		
			
			
			if(CCPConstant.PERCENT.equals(exam.getResultType()) && exam.getNegativeMark() != null){
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("negative_marks_not_applicable"));
				return;
			}
			if (exam.getStartrange() > exam.getEndrange()) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("StartRangeShouldBeLessThenEndRange"));
				return;
			}
			if (exam.getNoOfQuestions() >= (exam.getEndrange() - exam
					.getStartrange())) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("No_of_quetions_can_not_be_GT_EQ",(exam.getEndrange() - exam.getStartrange()))
						);
				return;
			}
			if (CCPConstant.CREATE.equals(currentDocMode)) {
				Criteria criteria = session.createCriteria(Exam.class).setProjection(Projections.property("testname"));
				criteria.add(Restrictions.eq("testname", exam.getTestname()));
				String testName = (String)criteria.uniqueResult();
				if (testName != null) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Exam_is_already_exists"));
					return;
				}
				AdminUser user = AdminUser.getUser();
				if (user != null) {
					exam.setCreatedBy(user.getUserName());
				}
				session.save(exam);
				currentDocMode = CCPConstant.UPDATE;
				UIBackingBean.setSuccessMessage(FbMessageUtil
						.getLabel(ResourceID.Success21));
			} else {
				session.update(exam);
				UIBackingBean.setSuccessMessage(FbMessageUtil
						.getLabel(ResourceID.Success22));
			}
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public Exam getExam() {
		return exam;
	}

	public void setExam(Exam exam) {
		this.exam = exam;
	}

	public String getCurrentDocMode() {
		return currentDocMode;
	}

	public void setCurrentDocMode(String currentDocMode) {
		this.currentDocMode = currentDocMode;
	}

}
