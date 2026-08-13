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
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.openfaces.component.command.CommandLink;
import org.openfaces.event.AjaxActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.hibernate.Questions;
import tpo.hibernate.QuestionsId;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("QuestionListTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class QuestionListTableBean {

	private Logger logger = LoggerFactory.getLogger(StudentTableBean.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private Pagination pagination;

	private String testName;
	
	private byte[] imageBytes;

	private List<Questions> questionList = null;

	private List<Questions> seletectQuestionList = new ArrayList<Questions>();

	public List<Questions> getQuestionList() {

		return questionList;
	}
	
	

	public void setQuestionList(List<Questions> questionList) {
		this.questionList = questionList;
	}



	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public List<Questions> getSeletectQuestionList() {
		return seletectQuestionList;
	}

	public void setSeletectQuestionList(List<Questions> seletectQuestionList) {
		this.seletectQuestionList = seletectQuestionList;
	}

	public void searchRecord() {
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSelectedQuestion() {
		try {
			if (seletectQuestionList != null
					&& seletectQuestionList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil
						.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (Questions questions : seletectQuestionList) {
					session.delete(questions);
					UIBackingBean
							.setSuccessMessage(FbMessageUtil.getLabel("Selected_results_are_successfully_deleted"));
				}

				questionList = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setQuestion(AjaxActionEvent event) {
		try {
			QuestionAddBean bean = (QuestionAddBean) TpoUtil
					.getManagedBean(QuestionAddBean.class.getSimpleName());
			if (seletectQuestionList != null
					&& seletectQuestionList.size() == 0) {
				UIBackingBean
						.setInfoMessage(FbMessageUtil.getLabel("Info12"));
				if (bean != null) {
					bean.setQuestions(null);
					bean.setId(null);
				}
			} else {
				if (seletectQuestionList.size() > 1) {
					UIBackingBean
							.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
					if (bean != null) {
						bean.setQuestions(null);
						bean.setId(null);
					}
				} else {
					if (bean != null) {
						bean.setQuestions(seletectQuestionList.get(0));
						bean.setId(seletectQuestionList.get(0).getId());
						bean.setCurrentDocMode(CCPConstant.UPDATE);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setQuestionToAdd(AjaxActionEvent event) {
		QuestionAddBean bean = (QuestionAddBean) TpoUtil
				.getManagedBean(QuestionAddBean.class.getSimpleName());
		if (bean != null) {
			bean.setCurrentDocMode(CCPConstant.CREATE);
			bean.setQuestions(new Questions());
			bean.setId(new QuestionsId());
		}
	}

	public void inIt() {
		try {
			if (testName != null) {
				NativeQuery<BigInteger> query;
				BigInteger totalCount = null;
				Session session = sessionFactory.getCurrentSession();
				AdminUser user = AdminUser.getUser();
				if (user != null
						&& (CCPConstant.USER.equals(user.getRole()) || CCPConstant.ADMIN
								.equals(user.getRole()))) {
					query = session
							.createSQLQuery("select count(testname) from exam where testname = '"
									+ testName
									+ "' and  createdBy = '"
									+ user.getUserName() + "'");
					totalCount = (BigInteger) query.uniqueResult();
					if (totalCount != null && totalCount.intValue() <= 0) {
						UIBackingBean.setErrorMessage(FbMessageUtil
								.getLabel(ResourceID.Error2));
						return;
					}
				}
				Criteria criteria = session.createCriteria(Questions.class);
				criteria.add(Restrictions.eq("id.qtype", testName));
				query = session
						.createSQLQuery("select count(qno) from questions where qtype = '"
								+ testName + "'");
				criteria.setFirstResult(pagination.getPageSize()
						* (pagination.getCurrentPage() - 1));
				criteria.setMaxResults(pagination.getPageSize());
				BigInteger totalCnt = (BigInteger) query.uniqueResult();
				questionList = criteria.list();
				pagination.setTotalDisplayRecords(totalCnt.intValue());
			}

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void setFileName(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
						List<UIComponent> list = link.getChildren();
						UIParameter parameter = (UIParameter) list.get(0);
						imageBytes = (byte[])parameter.getValue();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}



	public byte[] getImageBytes() {
		return imageBytes;
	}



	public void setImageBytes(byte[] imageBytes) {
		this.imageBytes = imageBytes;
	}



	
}