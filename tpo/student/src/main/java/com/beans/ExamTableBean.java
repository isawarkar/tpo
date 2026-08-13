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
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dao.CommonDBBean;
import com.hibernate.Exam;
import com.hibernate.Result;
import com.util.FbMessageUtil;
import com.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("ExamTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class ExamTableBean {

	private Logger logger = LoggerFactory.getLogger(ExamTableBean.class);

	private String examName;

	private String clickedTestName;

	private List<Exam> examList = null;

	private List<Exam> selectedExamList = new ArrayList<Exam>();

	private List<Result> resultList;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private Pagination pagination;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String testStart() {
		try {
			if (selectedExamList != null && selectedExamList.isEmpty()) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_at_least_one_Exam"));
			} else {
				if (selectedExamList != null && selectedExamList.size() > 1) {
					UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_Exam"));
				} else {
					Exam exam = selectedExamList.get(0);
					if (exam != null) {
						CommonDBBean bean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
						Date date = new Date();
						if (exam.getValidFrom() != null && exam.getValidTo() != null) {
							if (!(date.after(exam.getValidFrom()) && date.before(exam.getValidTo()))) {
								UIBackingBean
										.setErrorMessage(FbMessageUtil.getLabel("Exam_Expired", exam.getTestname()));
								return "";
							}
						}
						BigInteger count = bean.getQuestionCount(exam);
						if (count != null && count.intValue() < (exam.getEndrange() - exam.getStartrange())) {
							String arg[] = new String[2];
							arg[0] = String.valueOf(exam.getEndrange() - exam.getStartrange());
							arg[1] = String.valueOf(count);
							UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Your_can_not_give_this_exam", arg));
							return "";
						}
					}
					QuestionBean questionBean = (QuestionBean) TpoUtil
							.getManagedBean(QuestionBean.class.getSimpleName());
					String createdBy = null;
					Student studentUser = Student.getStudent();
					if (studentUser != null && studentUser.getRollNumber() != null
							&& !studentUser.getRollNumber().equals("")) {
						questionBean.setLoginName(studentUser.getRollNumber());
						createdBy = studentUser.getCreateBy();
					}
					questionBean.setTest(exam.getTestname());
					questionBean.setStartNumber(exam.getStartrange());
					questionBean.setEndNumber(exam.getEndrange());
					questionBean.setNoOfQuestions(exam.getNoOfQuestions());
					questionBean.setMinute(String.valueOf(exam.getMinute() - 1));
					Double time = new Double(questionBean.getMinute().concat(".60"));
					CommonDBBean bean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
					if (bean != null) {
						bean.insertIntoResult(sessionFactory.getCurrentSession(), questionBean.getLoginName(),
								exam.getTestname(), exam.getNoOfQuestions(), time, createdBy);
					}
					return "mainTestNewWindow";
				}
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public void searchRecord() {
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
	}

	public List<Exam> getExamList() {
		return examList;
	}

	public void inItStudent() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Exam.class);
			String rollNumber = Student.getStudent().getRollNumber();
			if (rollNumber != null && !rollNumber.equals("")) {
				NativeQuery<String> query = session.createSQLQuery(
						"SELECT userName from college where collegeName = (select collegeName from registration where rollnumber='"
								+ rollNumber + "')");
				String userName = (String) query.uniqueResult();
				criteria.add(Restrictions.eq("createdBy", userName));
				String queryStr = "select count(testname) from exam where createdBy='" + userName + "'";

				if (examName != null && !"".equals(examName)) {
					criteria.add(Restrictions.like("testname", "%" + examName + "%").ignoreCase());
					queryStr = queryStr + " and testname like '%" + examName + "%'";
				}
				NativeQuery<BigInteger> query1 = session.createSQLQuery(queryStr);
				criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
				criteria.setMaxResults(pagination.getPageSize());
				criteria.addOrder(Order.asc("testname"));

				BigInteger totalCount = (BigInteger) query1.uniqueResult();
				pagination.setTotalDisplayRecords(totalCount.intValue());
				examList = criteria.list();
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public List<Exam> getSelectedExamList() {
		return selectedExamList;
	}

	public void setSelectedExamList(List<Exam> selectedExamList) {
		this.selectedExamList = selectedExamList;
	}


	public List<Result> getResultList() {
		return resultList;
	}

	public void setResultList(List<Result> resultList) {
		this.resultList = resultList;
	}

	public String getClickedTestName() {
		return clickedTestName;
	}

	public void setClickedTestName(String clickedTestName) {
		this.clickedTestName = clickedTestName;
	}



}