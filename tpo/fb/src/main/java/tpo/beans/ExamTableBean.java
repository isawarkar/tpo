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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputHidden;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.openfaces.component.chart.ChartModel;
import org.openfaces.component.chart.PlainModel;
import org.openfaces.component.chart.PlainSeries;
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
import tpo.hibernate.Exam;
import tpo.hibernate.Result;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.FbResourceUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

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

	private List<String> testList = new ArrayList<String>();

	private List<Exam> examList = null;

	private List<Exam> selectedExamList = new ArrayList<Exam>();

	private List<Result> resultList;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private Pagination pagination;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSelectedExam() {
		try {
			if (selectedExamList != null && selectedExamList.isEmpty()) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (Exam exam : selectedExamList) {
					session.delete(exam);
				}
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success1));
				examList = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteWithQuestions() {
		try {
			if (selectedExamList != null && selectedExamList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				NativeQuery<?> query = null;
				for (Exam exam : selectedExamList) {
					query = session.createSQLQuery("delete from questions where qtype = '" + exam.getTestname() + "'");
					query.executeUpdate();
					session.delete(exam);
					examList = null;
				}
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success1));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public String updateRecord() {

		return null;
	}

	

	public void searchRecord() {
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
	}

	public List<Exam> getExamList() {
		return examList;
	}

	public void inIt() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Exam.class);
			String userName = AdminUser.getUser().getUserName();
			List<String> userList = AdminUser.getUser().getUserList();
			criteria.add(Restrictions.in("createdBy", userList));
			String queryStr = "select count(testname) from exam where createdBy in ("
					+ TpoUtil.getComaSeprateValue(userList) + ")";

			if (examName != null && !"".equals(examName)) {
				criteria.add(Restrictions.like("testname", "%" + examName + "%").ignoreCase());
				queryStr = queryStr + " and testname like '%" + examName + "%'";
			}
			NativeQuery<BigInteger> query = session.createSQLQuery(queryStr);
			criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			criteria.addOrder(Order.asc("testname"));

			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			examList = criteria.list();
			if (examList != null && examList.size() > 0) {
				for (Exam exam : examList) {
					query = session.createSQLQuery(
							"select count(qno) from questions where qtype = '" + exam.getTestname() + "'");
					totalCount = (BigInteger) query.uniqueResult();
					if (totalCount != null) {
						exam.setNumberOfQuestions(totalCount.intValue());
					}
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

	public void setExam(AjaxActionEvent event) {
		try {
			ExamBean bean = (ExamBean) TpoUtil.getManagedBean(ExamBean.class.getSimpleName());
			if (selectedExamList != null && selectedExamList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Info12"));
				if (bean != null) {
					bean.setExam(null);
				}
			} else {
				if (selectedExamList.size() > 1) {
					UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
					if (bean != null) {
						bean.setExam(null);
					}
				} else {
					if (bean != null) {
						bean.setCurrentDocMode(CCPConstant.UPDATE);
						bean.setExam(selectedExamList.get(0));
					}
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

	public void setExamToAdd(AjaxActionEvent event) {
		ExamBean bean = (ExamBean) TpoUtil.getManagedBean(ExamBean.class.getSimpleName());
		if (bean != null) {
			bean.setCurrentDocMode(CCPConstant.CREATE);
			bean.setExam(new Exam());
		}
	}

	public String showInfo() {
		try {
			if (selectedExamList != null && selectedExamList.size() == 0) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_select_at_least_one_record_to_see_Question_List"));
			} else {
				if (selectedExamList.size() > 1) {
					UIBackingBean.setInfoMessage(
							FbMessageUtil.getLabel("Please_select_only_one_record_to_see_Question_List"));
				} else {
					Exam exam = selectedExamList.get(0);
					if (exam != null) {
						QuestionListTableBean bean = (QuestionListTableBean) TpoUtil
								.getManagedBean(QuestionListTableBean.class.getSimpleName());
						if (bean != null) {
							bean.setTestName(exam.getTestname());
							return "questionsList";
						}
					} /*
						 * else{ UIBackingBean .setErrorMessage(
						 * "Record has been deleted for Enrollment No : "
						 * +selectedStudentList.getId() .getRollnumber()); }
						 */
				}
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public List<String> getTestList() {
		try {
			if (testList.size() == 0) {
				Session session = sessionFactory.getCurrentSession();
				List<String> userList = AdminUser.getUser().getUserList();
				NativeQuery<String> query = session.createSQLQuery("select testname from exam where createdBy in ("+ TpoUtil.getComaSeprateValue(userList) + ")");
				testList = query.list();
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return testList;
	}

	public void setExamResult(AjaxActionEvent event) {
		try {
			if (event != null) {
				Session session = null;
				CommandLink link = (CommandLink) event.getSource();
			  List<UIComponent> components = 	link.getChildren();
				if (components != null) {
					HtmlInputHidden hidden = (HtmlInputHidden)components.get(1);
					session = sessionFactory.getCurrentSession();
					Criteria criteria = session.createCriteria(Result.class);
					clickedTestName = (String) hidden.getValue();
					criteria.add(Restrictions.in("testName", clickedTestName));
					resultList = criteria.list();
					if(resultList == null || resultList.isEmpty()){
						UIBackingBean.setInfoMessage(
								FbMessageUtil.getLabel("No_one_has_given_this_exam"));
					}
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

	@SuppressWarnings("unchecked")
	public ChartModel getResultPieAnalysis() {

		if (resultList != null && !resultList.isEmpty()) {
			Map data = new HashMap();
			int qualified = 0;
			int disqualified = 0;
			int qualifiedinFirstClass = 0;
			int qualifiedInHonours = 0;
			for (Result result : resultList) {
				if (CCPConstant.Qualified.equals(result.getResult())) {
					qualified++;
				}
				if (CCPConstant.Disqualified.equals(result.getResult())) {
					disqualified++;
				}
				if (CCPConstant.QualifiedinFirstClass.equals(result.getResult())) {
					qualifiedinFirstClass++;
				}
				if (CCPConstant.QualifiedInHonours.equals(result.getResult())) {
					qualifiedInHonours++;
				}

			}
			data.put(FbResourceUtil.getLabel("Qualified_in_First_Class"), new Integer(qualifiedinFirstClass));
			data.put(FbResourceUtil.getLabel("DisQualified"), new Integer(disqualified));
			data.put(FbResourceUtil.getLabel("Qualified"), new Integer(qualified));
			data.put(FbResourceUtil.getLabel("Qualified_In_Honors"), new Integer(qualifiedInHonours));
			
			PlainSeries series = new PlainSeries();
			series.setData(data);
			series.setKey("StudentChart");

			PlainModel model = new PlainModel();
			model.addSeries(series);
			return model;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public ChartModel getResultAnalysis() {

		if (resultList != null && !resultList.isEmpty()) {
			Map qualifiedS = new HashMap();
			Map disqualifiedS = new HashMap();
			Map qualifiedinFirstClassS = new HashMap();
			Map qualifiedInHonoursS = new HashMap();
			int qualified = 0;
			int disqualified = 0;
			int qualifiedinFirstClass = 0;
			int qualifiedInHonours = 0;
			for (Result result : resultList) {
				if (CCPConstant.Qualified.equals(result.getResult())) {
					qualified++;
				}
				if (CCPConstant.Disqualified.equals(result.getResult())) {
					disqualified++;
				}
				if (CCPConstant.QualifiedinFirstClass.equals(result.getResult())) {
					qualifiedinFirstClass++;
				}
				if (CCPConstant.QualifiedInHonours.equals(result.getResult())) {
					qualifiedInHonours++;
				}

			}
			qualifiedinFirstClassS.put(FbResourceUtil.getLabel("Qualified_in_First_Class"), new Integer(qualifiedinFirstClass));
			qualifiedS.put(FbResourceUtil.getLabel("Qualified"), new Integer(qualified));
			qualifiedInHonoursS.put(FbResourceUtil.getLabel("Qualified_In_Honors"), new Integer(qualifiedInHonours));
			disqualifiedS.put(FbResourceUtil.getLabel("DisQualified"), new Integer(disqualified));
			
			PlainSeries series = new PlainSeries();
			series.setData(qualifiedS);
			series.setKey(FbResourceUtil.getLabel("Qualified"));
			
			PlainSeries series1 = new PlainSeries();
			series1.setData(disqualifiedS);
			series1.setKey(FbResourceUtil.getLabel("DisQualified"));
			
			PlainSeries series2 = new PlainSeries();
			series2.setData(qualifiedinFirstClassS);
			series2.setKey(FbResourceUtil.getLabel("Qualified_in_First_Class"));
			
			PlainSeries series3 = new PlainSeries();
			series3.setData(qualifiedInHonoursS);
			series3.setKey(FbResourceUtil.getLabel("Qualified_In_Honors"));

			PlainModel model = new PlainModel();
			model.addSeries(series);
			model.addSeries(series2);
			model.addSeries(series3);
			model.addSeries(series1);
			return model;
		} else {
			return null;
		}
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

	public String goAdminResultList(){
		StudentTableBean bean = (StudentTableBean) TpoUtil.getManagedBean(StudentTableBean.class.getSimpleName());
		if (bean != null) {
			Pagination pagination = Pagination.getPagination();
			pagination.resetCurrentPage();
			bean.setLoginName(null);
			bean.setResult(null);
			bean.setTemp(false);
		}
		return "adminResultList";
	}
	
}