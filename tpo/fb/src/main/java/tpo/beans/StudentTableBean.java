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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlPanelGroup;

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
import org.openfaces.component.chart.impl.GridPointInfoImpl;
import org.openfaces.component.chart.impl.PieSectorInfoImpl;
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
import tpo.admin.excel.ExcelHandler;
import tpo.dao.CommonDBBean;
import tpo.hibernate.Exam;
import tpo.hibernate.Questions;
import tpo.hibernate.Registration;
import tpo.hibernate.Result;
import tpo.hibernate.annotation.SessionData;
import tpo.imageservice.client.FileUploadUtility;
import tpo.pdf.generator.PDFGenerator;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.FbResourceUtil;
import tpo.util.IMAGECONS;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("StudentTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class StudentTableBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(StudentTableBean.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private PDFGenerator pDFGenerator;

	@Autowired
	private FileUploadUtility fileUploadUtility;

	@Autowired
	private CommonDBBean CommonDBBean;

	@Autowired
	private Pagination pagination;

	private String loginName;

	private String testName;

	private String result;

	private List<Result> resultList;

	private List<Result> list = new ArrayList<Result>();

	private List<Questions> questionList = null;

	private List<Questions> questionListCorrect = null;

	private List<SessionData> studentPojos = null;

	private List<SessionData> studentPojosList = new ArrayList<SessionData>();

	private int totalResultCount;

	public PieSectorInfoImpl pieSelectedCategory;

	public GridPointInfoImpl selectedCategory;

	public boolean temp;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setQuestionList(AjaxActionEvent event) {
		try {
			if (event != null) {
				questionList = null;
				questionListCorrect = null;
				Session session = sessionFactory.getCurrentSession();
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					HtmlPanelGroup pg = (HtmlPanelGroup) link.getParent();
					if (pg != null) {
						List<UIComponent> list = pg.getChildren();
						if (list != null) {
							String testName = (String) ((HtmlInputHidden) list.get(0)).getValue();
							String allNo = (String) ((HtmlInputHidden) list.get(1)).getValue();
							String[] allNoA = allNo.split("#");
							if (allNoA != null && allNoA.length > 0) {

								// Correct Answers
								if (allNoA[0] != null) {
									String correctNos = allNoA[0];
									String[] correctNosArray = null;
									if (correctNos != null && !correctNos.isEmpty()) {
										correctNosArray = correctNos.split(",");
										List<Integer> correctNoList = new ArrayList<>();
										for (String s : correctNosArray) {
											correctNoList.add(Integer.valueOf(s.trim()));
										}
										Criteria criteria = session.createCriteria(Questions.class);
										criteria.add(Restrictions.eq("id.qtype", testName));
										criteria.add(Restrictions.in("id.qno", correctNoList));
										questionListCorrect = criteria.list();
									}
								}

								// All Question
								if (allNoA.length > 1 && allNoA[1] != null) {
									Criteria criteria = session.createCriteria(Questions.class);
									criteria.add(Restrictions.eq("id.qtype", testName));
									String nos = allNoA[1];
									String[] nosArray = nos.split(",");

									List<Integer> allNoList = new ArrayList<>();
									for (String s : nosArray) {
										allNoList.add(Integer.valueOf(s.trim()));
									}
									criteria.add(Restrictions.in("id.qno", allNoList));
									questionList = criteria.list();
								}
							}
						}
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

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSelectedResult() {
		try {
			if (list != null && list.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (Result result : list) {
					session.delete(result);
					String certFileName = "Certificate_" + result.getTestName() + "_" + result.getId().getLoginname()
							+ "_" + result.getTotalnumbers();
					fileUploadUtility.deleteFileWithParam(getFileServiceUrl() + "/delete", certFileName,
							IMAGECONS.student.toString() + result.getId().getLoginname() + "/"
									+ IMAGECONS.certificate.toString());
					UIBackingBean
							.setSuccessMessage(FbMessageUtil.getLabel("Selected_results_are_successfully_deleted"));
				}
				resultList = null;
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
	public void deleteJunkResult() {
		try {
			Session session = sessionFactory.getCurrentSession();
			String query = "delete FROM indwaar_fb.result where result='Test Started' and datetaken < now() - INTERVAL 1 DAY";
			NativeQuery<?> nativeQuery = session.createSQLQuery(query);
			int count = nativeQuery.executeUpdate();
			if (count > 0) {
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("junk_records_deleted", count));
			} else {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Error2"));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void deleteSelectedPojo(AjaxActionEvent event) {
		try {
			if (studentPojosList != null && studentPojosList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				for (SessionData pojo : studentPojosList) {
					studentPojos.remove(pojo);
					UIBackingBean
							.setSuccessMessage(FbMessageUtil.getLabel("Selected_results_are_successfully_deleted"));
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

	public void downloadCertificate() {
		try {
			if (list != null && list.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_generate"));
			} else if (list != null && list.size() > 1) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_generate"));
			} else {
				Result result = list.get(0);
				if (result != null) {
					String certFileName = "Certificate_" + result.getTestName() + "_" + result.getId().getLoginname()
							+ "_" + result.getTotalnumbers() + ".pdf";
					byte[] certificate = fileUploadUtility.downloadFileWithParam(getFileServiceUrl() + "/download",
							certFileName, IMAGECONS.student.toString() + result.getId().getLoginname() + "/"
									+ IMAGECONS.certificate.toString());
					TpoUtil.renderPDFFile(certificate, certFileName);
					resultList = null;
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
	public void createCertificate() {
		try {
			if (list != null && list.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				ResultBean resultBean;

				for (Result result : list) {
					if (!CCPConstant.Disqualified.equalsIgnoreCase(result.getResult())) {
						resultBean = new ResultBean();
						Registration registration = (Registration) session.get(Registration.class,
								result.getId().getLoginname());
						if (registration != null) {
							resultBean.setRegistration(registration);
							Exam exam = session.get(Exam.class, result.getTestName());
							if (exam != null) {
								resultBean.setExam(exam);
								if ((exam.getFirstClassMark() != 0
										&& result.getTotalnumbers() >= exam.getFirstClassMark())
										&& (exam.getHonoursMark() != 0
												&& result.getTotalnumbers() < exam.getHonoursMark())) {
									resultBean.setResultIn("First Class");
								}
								if ((exam.getHonoursMark() != 0 && result.getTotalnumbers() >= exam.getHonoursMark())) {
									resultBean.setResultIn("Honours");
								}
							}
							resultBean.setPercent(result.getTotalnumbers());
							byte[] certificate = pDFGenerator.generateCertificate(resultBean);
							String certFileName = "Certificate_" + result.getTestName() + "_"
									+ result.getId().getLoginname() + "_" + result.getTotalnumbers() + ".pdf";

							fileUploadUtility.uploadFileWithByteArrayWithExt(getFileServiceUrl() + "/upload",
									certFileName, certificate, IMAGECONS.student.toString()
											+ result.getId().getLoginname() + "/" + IMAGECONS.certificate.toString());
							result.setCertificateAvialable(true);
							session.update(result);
							TpoUtil.renderPDFFile(certificate, certFileName);
							UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("certificate_Created"));
						} else {
							UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Error10"));
						}
					}
				}
				resultList = null;
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
		pieSelectedCategory = null;
		selectedCategory = null;
		temp = false;
	}

	public List<Result> getAdminResultList() {
		/*
		 * AdminUser user = AdminUser.getUser(); if (user != null) { if (resultList !=
		 * null) { for (Result result : resultList) { if (resultRefreshBean != null) {
		 * Map<String, StudentPojo> map = resultRefreshBean.getStudentMap(); StudentPojo
		 * pojo = map.get(result.getId().getLoginname()); if (pojo != null) { if
		 * (user.getUserName().equals(pojo.getCreatedBy()) && pojo.getAttempt() ==
		 * result.getId().getAttempt()) { result.setTempRecord(true); } } } } } }
		 */
		return resultList;
	}

	public List<Result> getResultList() {
		return resultList;
	}

	public List<Result> getList() {
		return list;
	}

	public void setList(List<Result> list) {
		this.list = list;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void adminInit() {
		if (temp) {
			return;
		}
		Session session = sessionFactory.getCurrentSession();
		try {
			Criteria criteria = session.createCriteria(Result.class);
			String userName = AdminUser.getUser().getUserName();
			if (userName != null) {
				List<String> userList = AdminUser.getUser().getUserList();
				criteria.add(Restrictions.in("createdBy", userList));

				criteria.addOrder(Order.asc("id.loginname"));
				String queryStr = "select count(loginname) from result where createdBy in ("
						+ TpoUtil.getComaSeprateValue(userList) + ")";
				if (loginName != null && !"".equals(loginName)) {
					criteria.add(Restrictions.eq("id.loginname", loginName));
					queryStr = queryStr + " and loginname = '" + loginName + "'";
				}
				if (testName != null && !"".equals(testName)) {
					criteria.add(Restrictions.eq("testName", testName));
					queryStr = queryStr + " and testName = '" + testName + "'";
				}
				if (pieSelectedCategory != null) {
					temp = true;
					result = pieSelectedCategory.getKey().toString();
					pieSelectedCategory = null;
				}
				if (selectedCategory != null) {
					temp = true;
					result = selectedCategory.getKey().toString();
					selectedCategory = null;
				}
				if (result != null && !result.isEmpty()) {
					criteria.add(Restrictions.eq("result", result));
					queryStr = queryStr + " and result = '" + result + "'";
				}

				NativeQuery<BigInteger> query = session.createSQLQuery(queryStr);
				criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
				criteria.setMaxResults(pagination.getPageSize());
				BigInteger totalCount = (BigInteger) query.uniqueResult();
				int count = totalCount.intValue();
				resultList = criteria.list();
				if (resultList != null && !resultList.isEmpty()) {
					Iterator<Result> iterator = resultList.iterator();
					while (iterator.hasNext()) {
						Result result = iterator.next();
						if (CCPConstant.TEST_STARTED.equals(result.getResult())) {
							Date d1 = TpoUtil.getFormatedDateInddMMyyyyHHMM(Calendar.getInstance().getTime());
							Date d2 = TpoUtil.getFormatedDateInddMMyyyyHHMM(result.getDateTaken());
							long diff = d1.getTime() - d2.getTime();
							long diffDays = diff / (24 * 60 * 60 * 1000);
							long diffHours = diff / (60 * 60 * 1000) % 24;
							long diffMinutes = diff / (60 * 1000) % 60;
							if (diffDays > 0) {
								diffHours = diffHours + (diffDays * 24);
							}
							if (diffHours > 0) {
								diffMinutes = diffMinutes + (diffHours * 60);
							}
							if (diffMinutes > result.getTotalTime() + 5) {
								iterator.remove();
								deleteResult(result, session);
								count -= 1;
							}
						} else {
							CommonDBBean.updateCertStatus(result);
						}
					}
				}
				pagination.setTotalDisplayRecords(count);
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	private void deleteResult(Result r, Session session) {
		session.delete(r);
	}

	public boolean isTemp() {
		return temp;
	}

	public void setTemp(boolean temp) {
		this.temp = temp;
	}

	public void inIt() {
		/*
		 * Session session = sessionFactory.getCurrentSession(); try { Criteria criteria
		 * = session.createCriteria(Result.class);
		 * criteria.addOrder(Order.asc("totalnumbers")); Student student =
		 * Student.getStudent(); if (student != null && student.getRollNumber() != null)
		 * { criteria.add(Restrictions.eq("id.loginname", student.getRollNumber()));
		 * criteria.add(Restrictions.eq("createdBy", student.getCreateBy())); String
		 * queryStr = "select count(loginname) from result where loginname = '" +
		 * student.getRollNumber() + "' and createdBy = '" + student.getCreateBy() +
		 * "'"; NativeQuery<BigInteger> query = session.createSQLQuery(queryStr);
		 * criteria.setFirstResult(pagination.getPageSize() *
		 * (pagination.getCurrentPage() - 1));
		 * criteria.setMaxResults(pagination.getPageSize()); BigInteger totalCount =
		 * (BigInteger) query.uniqueResult(); resultList = criteria.list(); Set<String>
		 * examNames = new HashSet<String>(); for (Result result : resultList) {
		 * examNames.add(result.getTestName()); } criteria =
		 * session.createCriteria(Exam.class); criteria.add(Restrictions.in("testname",
		 * examNames)); List<Exam> examList = criteria.list(); File file; for (Result
		 * result : resultList) { for (Exam exam : examList) { if
		 * (exam.getTestname().equals(result.getTestName())) {
		 * result.setShowResult(exam.getShowResult()); } }
		 * CommonDBBean.updateCertStatus(result); }
		 * pagination.setTotalDisplayRecords(totalCount.intValue()); } } catch
		 * (HibernateException e) { logger.error(e.getMessage()); e.printStackTrace(); }
		 * catch (Exception e) { logger.error(e.getMessage()); e.printStackTrace(); }
		 */}

	public String showInfo() {
		try {
			if (list != null && list.size() == 0) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_select_at_least_one_record_to_see_full_information"));
			} else {
				Session session = sessionFactory.getCurrentSession();
				if (list.size() > 1) {
					UIBackingBean.setInfoMessage(
							FbMessageUtil.getLabel("Please_select_only_one_record_to_see_full_information"));
				} else {
					Result result = list.get(0);
					Registration registration = (Registration) session.get(Registration.class,
							result.getId().getLoginname());
					if (registration != null) {
						StudentRegistrationBean bean = (StudentRegistrationBean) TpoUtil
								.getManagedBean(StudentRegistrationBean.class.getSimpleName());
						if (bean != null) {
							bean.setCurrentMode(CCPConstant.UPDATE);
							bean.setCurrentCourse(registration.getPersonalinfo().getCurrentCourse());
							bean.setRegistration(registration);
							bean.setPersonalinfo(registration.getPersonalinfo());
							bean.setPercentageinfo(registration.getPercentageinfo());
							bean.setBackdetails(registration.getBackdetails());
							bean.setContactinfo(registration.getContactinfo());
							bean.setAchivements(registration.getAchivements());
							return "newStudent";
						}
					} else {
						UIBackingBean.setErrorMessage(FbMessageUtil
								.getLabel("Record_has_been_deleted_for_Enrollment_No", result.getId().getLoginname()));
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
		return null;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String showExamInfo() {
		try {
			if (list != null && list.size() == 0) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_select_at_least_one_record_to_see_Question_List"));
			} else {
				if (list.size() > 1) {
					UIBackingBean.setInfoMessage(
							FbMessageUtil.getLabel("Please_select_only_one_record_to_see_Question_List"));
				} else {
					Result result = list.get(0);
					if (result != null) {
						QuestionListTableBean bean = (QuestionListTableBean) TpoUtil
								.getManagedBean(QuestionListTableBean.class.getSimpleName());
						if (bean != null) {
							bean.setTestName(result.getTestName());
							return "questionsList";
						}
					} /*
						 * else{ UIBackingBean .setErrorMessage(
						 * "Record has been deleted for Enrollment No : " +selectedStudentList.getId()
						 * .getRollnumber()); }
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

	public void generateXls() {
		try {
			if (resultList != null && !resultList.isEmpty()) {
				String reportName = "FB_" + "ResultList_" + AdminUser.getUser().getUserName() + "_"
						+ TpoUtil.getDateToStringYYYYMMdd(new Date());
				TpoUtil.renderEXcelFile(ExcelHandler.generateResultList(
						(list != null && list.size() > 0) ? list : resultList, reportName), reportName);
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
			if (resultList != null && !resultList.isEmpty()) {
				pDFGenerator.generateResultList((list != null && list.size() > 0) ? list : resultList);
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

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public List<Questions> getQuestionList() {
		return questionList;
	}

	public void setQuestionList(List<Questions> questionList) {
		this.questionList = questionList;
	}

	public List<Questions> getQuestionListCorrect() {
		return questionListCorrect;
	}

	public void setQuestionListCorrect(List<Questions> questionListCorrect) {
		this.questionListCorrect = questionListCorrect;
	}

	public void refreshResult(AjaxActionEvent event) {
		try {
			AdminUser user = AdminUser.getUser();
			List<String> userNames = user.getUserList();
			if (user != null) {
				Session session = sessionFactory.getCurrentSession();
				Criteria criteria = (Criteria) session.createCriteria(SessionData.class);
				if (user != null && CCPConstant.SUPERUSER.equals(user.getRole())) {
					studentPojos = criteria.list();
				} else {
					criteria.add(Restrictions.in("createdBy", userNames));
					studentPojos = criteria.list();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void clearAll(AjaxActionEvent event) {
		try {
			AdminUser user = AdminUser.getUser();
			if (user != null && CCPConstant.SUPERUSER.equals(user.getRole())) {
				if (studentPojos != null) {
					studentPojos.clear();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void resetResult(AjaxActionEvent event) {
		try {
			AdminUser user = AdminUser.getUser();
			if (user != null) {
				studentPojos = new ArrayList<SessionData>();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public List<SessionData> getStudentPojos() {
		if (studentPojos == null) {
			refreshResult(null);
		}
		return studentPojos;
	}

	public void setStudentPojos(List<SessionData> studentPojos) {
		this.studentPojos = studentPojos;
	}

	public List<SessionData> getStudentPojosList() {
		return studentPojosList;
	}

	public void setStudentPojosList(List<SessionData> studentPojosList) {
		this.studentPojosList = studentPojosList;
	}

	@SuppressWarnings("unchecked")
	public ChartModel getResultAnalysis() {
		if (studentPojos != null && !studentPojos.isEmpty()) {
			Map data = new HashMap();
			int qualified = 0;
			int disqualified = 0;
			int qualifiedinFirstClass = 0;
			int qualifiedInHonours = 0;
			for (SessionData pojo : studentPojos) {
				if (CCPConstant.Qualified.equals(pojo.getResult())) {
					qualified++;
				}
				if (CCPConstant.Disqualified.equals(pojo.getResult())) {
					disqualified++;
				}
				if (CCPConstant.QualifiedinFirstClass.equals(pojo.getResult())) {
					qualifiedinFirstClass++;
				}
				if (CCPConstant.QualifiedInHonours.equals(pojo.getResult())) {
					qualifiedInHonours++;
				}

			}
			PlainModel model = getPieModel(data, qualified, disqualified, qualifiedinFirstClass, qualifiedInHonours);
			return model;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public ChartModel getResultBarAnalysis() {
		if (studentPojos != null && !studentPojos.isEmpty()) {
			Map qualifiedS = new HashMap();
			Map disqualifiedS = new HashMap();
			Map qualifiedinFirstClassS = new HashMap();
			Map qualifiedInHonoursS = new HashMap();
			int qualified = 0;
			int disqualified = 0;
			int qualifiedinFirstClass = 0;
			int qualifiedInHonours = 0;
			for (SessionData pojo : studentPojos) {
				if (CCPConstant.Qualified.equals(pojo.getResult())) {
					qualified++;
				}
				if (CCPConstant.Disqualified.equals(pojo.getResult())) {
					disqualified++;
				}
				if (CCPConstant.QualifiedinFirstClass.equals(pojo.getResult())) {
					qualifiedinFirstClass++;
				}
				if (CCPConstant.QualifiedInHonours.equals(pojo.getResult())) {
					qualifiedInHonours++;
				}

			}
			PlainModel model = new PlainModel();
			getBarModel(qualifiedS, disqualifiedS, qualifiedinFirstClassS, qualifiedInHonoursS, qualified, disqualified,
					qualifiedinFirstClass, qualifiedInHonours, model);
			return model;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public ChartModel getResultAnalysisStudent() {
		if (resultList != null && !resultList.isEmpty()) {
			Map data = new HashMap();
			int qualified = 0;
			int disqualified = 0;
			int qualifiedinFirstClass = 0;
			int qualifiedInHonours = 0;
			boolean modelData = false;
			for (Result result : resultList) {
				if (!CCPConstant.TEST_STARTED.equals(result.getResult())) {
					modelData = true;
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
			}
			if (!modelData) {
				return null;
			}
			PlainModel model = getPieModel(data, qualified, disqualified, qualifiedinFirstClass, qualifiedInHonours);
			return model;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public ChartModel getResultBarAnalysisStudent() {
		if (resultList != null && !resultList.isEmpty()) {
			Map qualifiedS = new HashMap();
			Map disqualifiedS = new HashMap();
			Map qualifiedinFirstClassS = new HashMap();
			Map qualifiedInHonoursS = new HashMap();
			int qualified = 0;
			int disqualified = 0;
			int qualifiedinFirstClass = 0;
			int qualifiedInHonours = 0;
			boolean modelData = false;
			for (Result result : resultList) {
				if (!CCPConstant.TEST_STARTED.equals(result.getResult())) {
					modelData = true;
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
			}
			if (!modelData) {
				return null;
			}
			PlainModel model = new PlainModel();
			getBarModel(qualifiedS, disqualifiedS, qualifiedinFirstClassS, qualifiedInHonoursS, qualified, disqualified,
					qualifiedinFirstClass, qualifiedInHonours, model);
			return model;
		} else {
			return null;
		}
	}

	private PlainModel getPieModel(Map data, int qualified, int disqualified, int qualifiedinFirstClass,
			int qualifiedInHonours) {
		totalResultCount = qualified + disqualified + qualifiedinFirstClass + qualifiedInHonours;
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
	}

	private void getBarModel(Map qualifiedS, Map disqualifiedS, Map qualifiedinFirstClassS, Map qualifiedInHonoursS,
			int qualified, int disqualified, int qualifiedinFirstClass, int qualifiedInHonours, PlainModel model) {
		qualifiedinFirstClassS.put(FbResourceUtil.getLabel("Qualified_in_First_Class"),
				new Integer(qualifiedinFirstClass));
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

		model.addSeries(series);
		model.addSeries(series2);
		model.addSeries(series3);
		model.addSeries(series1);
	}

	public int getTotalResultCount() {
		return totalResultCount;
	}

	public void setTotalResultCount(int totalResultCount) {
		this.totalResultCount = totalResultCount;
	}

	/*
	 * public String selectPieChart() { StudentTableBean bean = (StudentTableBean)
	 * TpoUtil.getManagedBean(StudentTableBean.class.getSimpleName()); if (bean !=
	 * null) { bean.setResult(pieSelectedCategory.getKey().toString()); } return "";
	 * }
	 * 
	 * public String selectBarChart() { StudentTableBean bean = (StudentTableBean)
	 * TpoUtil.getManagedBean(StudentTableBean.class.getSimpleName()); if (bean !=
	 * null) { bean.setResult(selectedCategory.getKey().toString()); } return ""; }
	 */

	public GridPointInfoImpl getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(GridPointInfoImpl selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public PieSectorInfoImpl getPieSelectedCategory() {
		return pieSelectedCategory;
	}

	public void setPieSelectedCategory(PieSectorInfoImpl pieSelectedCategory) {
		this.pieSelectedCategory = pieSelectedCategory;
	}

	public String showStudentInfo() {
		try {
			if (studentPojosList != null && studentPojosList.size() == 0) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_select_at_least_one_record_to_see_full_information"));
			} else {
				Session session = sessionFactory.getCurrentSession();
				if (studentPojosList.size() > 1) {
					UIBackingBean.setInfoMessage(
							FbMessageUtil.getLabel("Please_select_only_one_record_to_see_full_information"));
				} else {
					Registration registration = session.get(Registration.class,
							studentPojosList.get(0).getEnrollmentNumber());
					if (registration != null) {
						StudentRegistrationBean bean = (StudentRegistrationBean) TpoUtil
								.getManagedBean(StudentRegistrationBean.class.getSimpleName());
						if (bean != null) {
							bean.setCurrentMode(CCPConstant.UPDATE);
							bean.setCurrentCourse(registration.getPersonalinfo().getCurrentCourse());
							bean.setRegistration(registration);
							bean.setPersonalinfo(registration.getPersonalinfo());
							bean.setPercentageinfo(registration.getPercentageinfo());
							bean.setBackdetails(registration.getBackdetails());
							bean.setContactinfo(registration.getContactinfo());
							bean.setAchivements(registration.getAchivements());
							return "newStudent";
						}
					} /*
						 * else{ UIBackingBean .setErrorMessage(FbMessageUtil.getLabel(Record has been
						 * deleted for Enrollment No : "+selectedStudentList.getId() .getRollnumber());
						 * }
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

}