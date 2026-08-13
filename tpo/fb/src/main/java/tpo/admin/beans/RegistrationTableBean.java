/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.admin.beans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.openfaces.component.ajax.Ajax;
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

import tpo.admin.excel.ExcelHandler;
import tpo.beans.CCPLoginBean;
import tpo.beans.Pagination;
import tpo.beans.Parent;
import tpo.beans.PlacedStudentTableBean;
import tpo.beans.StudentRegistrationBean;
import tpo.beans.UIBackingBean;
import tpo.beans.WebFlow;
import tpo.dao.CommonDBBean;
import tpo.email.EmailUtil;
import tpo.hibernate.Backdetails;
import tpo.hibernate.College;
import tpo.hibernate.Company;
import tpo.hibernate.HallTicket;
import tpo.hibernate.Personalinfo;
import tpo.hibernate.Registration;
import tpo.hibernate.Result;
import tpo.imageservice.client.FileUploadUtility;
import tpo.pdf.generator.PDFGenerator;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.FbResourceUtil;
import tpo.util.IMAGECONS;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;
import tpo.util.WebFlowTabName;

/**
 * @author Uddanda Technologies
 */
@Repository("RegistrationTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class RegistrationTableBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(RegistrationTableBean.class);

	private String rollNumber;

	private String companyName;

	private Boolean selectedFlag;

	private String status;

	private String branchName;

	private String semester;

	private String course;

	private String pgcourse;

	private String collegeName;

	private Integer yearOfPassing;

	private boolean addStudentFlag;

	private boolean selected;

	private boolean blackListed;

	private List<Registration> studentList = null;
	private List<Registration> selectedStudentList = new ArrayList<Registration>();

	private List<String> companyList = new ArrayList<String>();
	private List<Company> companyObjList = null;

	private List<Result> resultList;

	private String clickedRollNumber;

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private FileUploadUtility fileUploadUtility;
	
	@Autowired
	private PDFGenerator pDFGenerator;

	@Autowired
	private CommonDBBean commonDBBean;

	@Autowired
	private Pagination pagination;

	public void setResult(AjaxActionEvent event) {
		try {
			if (selectedStudentList != null && selectedStudentList.isEmpty()) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error13));
			} else {
				if (selectedStudentList.size() > 1) {
					UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
				} else {
					Registration registration = selectedStudentList.get(0);
					if (registration != null) {
						clickedRollNumber = registration.getRollnumber();
						Session session = sessionFactory.getCurrentSession();
						Criteria criteria = session.createCriteria(Result.class);
						criteria.add(Restrictions.in("id.loginname", clickedRollNumber));
						resultList = criteria.list();
						if (resultList == null || resultList.isEmpty()) {
							UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("You_have_not_gave_any_exam"));
						}
					}
				}
			}
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

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSeletedRecord() {
		try {
			if (selectedStudentList != null && selectedStudentList.isEmpty()) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				EmailUtil emailUtill = getEmailInstance();
				List<String> recipients = null;

				for (Registration registration : selectedStudentList) {
					String collegeName = getCollege(session, registration);

					if (collegeName != null) {

						// Deleting all fee records
						NativeQuery<?> query = session.createSQLQuery("delete from studentfeedetails where rollNumber = '"
								+ registration.getRollnumber() + "'");
						query.executeUpdate();

						/*
						 * criteria = session.createCriteria(StudentFeeDetails.class);
						 * criteria.add(Restrictions.eq("rollNumber", registration.getRollnumber()));
						 * List<StudentFeeDetails> list = criteria.list(); if (list != null &&
						 * !list.isEmpty()) { for (StudentFeeDetails detail : list) {
						 * session.delete(detail); } }
						 */
						// Deleting all results
						query = session.createSQLQuery(
								"delete from result where loginname = '" + registration.getRollnumber() + "'");
						query.executeUpdate();

						session.delete(registration);
						fileUploadUtility.deleteFolder(getFileServiceUrl() + "/deleteFolder",
								IMAGECONS.student.toString()+registration.getRollnumber());
						
						if (emailUtill != null) {
							recipients = new ArrayList<String>(1);
							recipients.add(registration.getEmail());
							String subject = FbMessageUtil.getLabel("Your_Freshers_Buddy_information_deleted");
							StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear")
									+ registration.getFirstName() + " " + registration.getLastName());
							message.append(",<br><br>")
									.append("<font color=red size=5>"
											+ FbMessageUtil.getLabel(
													"Your_Freshers_Buddy_related_information_has_been_deleted")
											+ "</font><br>");
							message.append(TpoUtil.getMesageString());
							emailUtill.postMail(recipients, subject, message.toString(), TpoUtil.ADMIN_EMAIL,
									Message.RecipientType.TO);

						}
						UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success1));
						studentList = null;
					} else {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("You_cannot_Delete",
								registration.getFirstName() + " " + registration.getLastName()));
					}

				}

			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private String getCollege(Session session, Registration registration) {
		Criteria criteria = session.createCriteria(College.class)
				.setProjection(Projections.projectionList().add(Projections.property("collegeName"), "collegeName"));
		criteria.add(Restrictions.eq("collegeName", registration.getCollegeName()));
		criteria.add(Restrictions.eq("logindetails.userName", AdminUser.getUser().getUserName()));
		return (String) criteria.uniqueResult();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void approve() {
		try {
			if (selectedStudentList != null && selectedStudentList.isEmpty()) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_at_least_one_record_to_Approve"));
			} else {
				Session session = sessionFactory.getCurrentSession();
				EmailUtil emailUtill = getEmailInstance();
				List<String> recipients = null;

				for (Registration registration : selectedStudentList) {
					registration.setApproved(true);
					session.update(registration);
					if (emailUtill != null) {
						recipients = new ArrayList<String>(1);
						recipients.add(registration.getEmail());
						String subject = FbMessageUtil.getLabel("Your_Freshers_Buddy_information_has_been_Approved");
						StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear")
								+ registration.getFirstName() + " " + registration.getLastName());
						message.append(",<br><br>").append("<font color=green size=5>" + subject + "</font><br>");
						message.append(TpoUtil.getMesageString());
						emailUtill.postMail(recipients, subject, message.toString(), TpoUtil.ADMIN_EMAIL,
								Message.RecipientType.TO);

					}
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Successfully_Changed"));
				}
				studentList = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void block(boolean flag) {
		try {
			if (selectedStudentList != null && selectedStudentList.isEmpty()) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_at_least_one_record_to_Approve"));
			} else {
				Session session = sessionFactory.getCurrentSession();
				EmailUtil emailUtill = getEmailInstance();
				List<String> recipients = null;

				for (Registration registration : selectedStudentList) {
					registration.setStatus(flag);
					session.update(registration);
					if (emailUtill != null) {
						recipients = new ArrayList<String>(1);
						recipients.add(registration.getEmail());
						String subject = null;
						if (!flag) {
							subject = FbMessageUtil.getLabel("Your_Freshers_Buddy_information_has_been_Blocked");
						} else {
							subject = FbMessageUtil.getLabel("Your_Freshers_Buddy_information_has_been_UnBlocked");
						}
						StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear")
								+ registration.getFirstName() + " " + registration.getLastName());
						message.append(",<br><br>").append("<font color=green size=5>" + subject + "</font><br>");
						message.append(TpoUtil.getMesageString());
						emailUtill.postMail(recipients, subject, message.toString(), TpoUtil.ADMIN_EMAIL,
								Message.RecipientType.TO);

					}
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Successfully_Changed"));
				}
				studentList = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void pending() {
		try {
			if (selectedStudentList != null && selectedStudentList.isEmpty()) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_at_least_one_record_to_Approve"));
			} else {
				Session session = sessionFactory.getCurrentSession();
				EmailUtil emailUtill = getEmailInstance();
				List<String> recipients = null;

				for (Registration registration : selectedStudentList) {
					registration.setApproved(false);
					session.update(registration);
					if (emailUtill != null) {
						recipients = new ArrayList<String>(1);
						recipients.add(registration.getEmail());
						String subject = FbMessageUtil
								.getLabel("Your_Freshers_Buddy_information_has_been_changed_to_Pending");
						StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear")
								+ registration.getFirstName() + " " + registration.getLastName());
						message.append(",<br><br>").append("<font color=red size=5>" + subject + "</font><br>");
						message.append(TpoUtil.getMesageString());
						emailUtill.postMail(recipients, subject, message.toString(), TpoUtil.ADMIN_EMAIL,
								Message.RecipientType.TO);

					}
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Successfully_Changed"));
				}
				studentList = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void generateHallTicket() {
		try {
			if (selectedStudentList != null && selectedStudentList.isEmpty()) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_at_least_one_record_to_generate"));
			} else {
				if (selectedStudentList.size() > 1) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_generate"));
				} else {
					Session session = sessionFactory.getCurrentSession();
					Registration registration = selectedStudentList.get(0);
					@SuppressWarnings("unchecked")
					NativeQuery<HallTicket> query = session.createSQLQuery(
							"SELECT * FROM hallticket where hallticketId in (SELECT hallTicketId FROM hallticketconnect where rollnumber = '"
									+ registration.getRollnumber() + "') and isActive=true;");
					query.addEntity(HallTicket.class);
					List<HallTicket> hallTicketList = query.list();
					if (registration != null && hallTicketList.size() > 0) {
						for (HallTicket hallTicket : hallTicketList) {
							hallTicket.setCompany(commonDBBean.getCompnay(hallTicket.getCompanyID()));
						}
						TpoUtil.renderPDFFile(pDFGenerator.generateHallTicket(registration, hallTicketList,
								AdminUser.getUser().getUserName()), registration.getRollnumber());
					} else {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("No_Hall_Ticket_record_found_for",
								registration.getRollnumber()));
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

	public String updateRecord() {
		try {
			if (selectedStudentList != null && selectedStudentList.isEmpty()) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error13));
			} else {
				if (selectedStudentList.size() > 1) {
					UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
				} else {
					WebFlow webFlow = (WebFlow) TpoUtil.getManagedBean(WebFlow.class.getSimpleName());
					if (webFlow != null) {
						webFlow.setSelectedPage(WebFlowTabName.MI);
					}
					Registration registration = selectedStudentList.get(0);
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
						return webFlow.mainPage();
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public String goToStudentHomePage() {
		try {
			if (selectedStudentList != null && selectedStudentList.isEmpty()) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error13));
			} else {
				if (selectedStudentList.size() > 1) {
					UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
				} else {
					Registration registration = selectedStudentList.get(0);
					CCPLoginBean bean = (CCPLoginBean) TpoUtil.getManagedBean(CCPLoginBean.class.getSimpleName());
					if (bean != null) {
						Pagination pagination = Pagination.getPagination();
						pagination.resetCurrentPage();
						AdminUser user = AdminUser.getUser();
						user.clear();
						if (registration.getTheme() != null && !"T0".equals(registration.getTheme())) {
							return "studentHomePage" + registration.getTheme();
						} else {
							return "studentHomePage";
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public void searchRecord() {
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
		reset();
	}

	public void resetSearch() {
		setStatus(null);
		setSelected(false);
		setBlackListed(false);
		setRollNumber(null);
		companyName = null;
		setSelectedFlag(false);
	}

	public void reset() {
		setSelected(false);
		setSelectedFlag(false);
		setBlackListed(false);
	}

	public void generateXls() {
		try {
			if (selectedStudentList != null && selectedStudentList.size() > 0) {
				String reportName = "FB_" + "StudentList_" + AdminUser.getUser().getUserName() + "_"
						+ TpoUtil.getDateToStringYYYYMMdd(new Date()) + "_" + TpoUtil.get6DigitRandomNumber();
				TpoUtil.renderEXcelFile(ExcelHandler.generateStudentList(selectedStudentList, reportName), reportName);
			} else {
				Session session = sessionFactory.getCurrentSession();
				Criteria criteria = session.createCriteria(Registration.class);
				criteria.createAlias("percentageinfo", "percentageinfo");
				criteria.createAlias("personalinfo", "personalinfo");
				criteria.add(Restrictions.ne("percentageinfo.rollnumber", ""));
				setCriteria(criteria);
				AdminUser user = AdminUser.getUser();
				List<String> collegeList = null;
				if (user != null) {
					CommonDBBean bean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
					collegeList = AdminUser.getUser().getCollegeList();
					if (collegeList != null && collegeList.size() > 0) {
						criteria.add(Restrictions.in("collegeName", collegeList));
						studentList = criteria.list();
						String reportName = "FB_" + "StudentList_" + AdminUser.getUser().getUserName() + "_"
								+ TpoUtil.getDateToStringYYYYMMdd(new Date()) + "_" + TpoUtil.get6DigitRandomNumber();
						TpoUtil.renderEXcelFile(ExcelHandler.generateStudentList(studentList, reportName), reportName);

					} else {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error2));
					}
					studentList = null;
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

	public void generatePdfReport() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Registration.class);
			criteria.createAlias("percentageinfo", "percentageinfo");
			criteria.add(Restrictions.ne("percentageinfo.rollnumber", ""));
			setCriteria(criteria);
			AdminUser user = AdminUser.getUser();
			if (user != null) {
				List<String> collegeList = AdminUser.getUser().getCollegeList();
				criteria.add(Restrictions.in("collegeName", collegeList));
				studentList = criteria.list();
				if (studentList != null && studentList.size() > 0)
					pDFGenerator.generateStudentList(studentList);

				studentList = null;
			}

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	private void setCriteria(Criteria criteria) {
		if (rollNumber != null && !rollNumber.equals("")) {
			criteria.add(Restrictions.eq("rollnumber", rollNumber).ignoreCase());
		}
		if (collegeName != null && !collegeName.equals("")) {
			criteria.add(Restrictions.ilike("collegeName", "%" + collegeName + "%"));
		}
		if (status != null && !status.equals("")) {
			criteria.add(Restrictions.eq("approved", status.equals("A") ? true : false));

		}
		if (companyName != null && !companyName.equals("")) {
			criteria.add(Restrictions.ilike("personalinfo.companyName", "%" + companyName + "%"));
		}
		if (branchName != null && !branchName.equals("")) {
			criteria.add(Restrictions.ilike("personalinfo.branch", "%" + branchName + "%"));
		}
		if (semester != null && !semester.equals("")) {
			criteria.add(Restrictions.ilike("personalinfo.semester", "%" + semester + "%"));
		}
		if (course != null && !course.equals("")) {
			criteria.add(Restrictions.ilike("personalinfo.course", "%" + course + "%"));
		}
		if (pgcourse != null && !pgcourse.equals("")) {
			criteria.add(Restrictions.ilike("personalinfo.postGraduationCourse", "%" + pgcourse + "%"));
		}
		if (yearOfPassing != null && yearOfPassing != 0) {
			criteria.add(Restrictions.eq("personalinfo.yearOfPassing", +yearOfPassing));
		}
	}

	public void setCompanyList(AjaxActionEvent event) {
		UIComponent uIComponent = event.getComponent();
		if ("Add".equals(uIComponent.getId())) {
			addStudentFlag = true;
		} else {
			addStudentFlag = false;
		}
		companyList = commonDBBean.getCompanyListByUserName();
		Collections.sort(companyList);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addToCompany() {
		try {
			if (companyName != null && !companyName.equals("") && !companyName.equals("All")) {
				String[] companyArray = companyName.split("#");
				if (selectedStudentList == null || selectedStudentList.size() == 0) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error18));
				} else {
					Session session = sessionFactory.getCurrentSession();
					EmailUtil emailUtill = getEmailInstance();
					List<String> recipients = null;
					if (emailUtill != null) {
						recipients = new ArrayList<String>(selectedStudentList.size());
					}
					String companyID = null;
					for (Registration registration : selectedStudentList) {
						Personalinfo personalinfo = (Personalinfo) registration.getPersonalinfo();
						if (personalinfo != null) {
							companyID = personalinfo.getCompanyID();
							if (companyID != null && !companyID.equals("")) {
								String[] str = companyID.split(",");
								if (str.length > 0) {
									for (String cm : str) {
										if (companyArray[0].equals(cm)) {
											UIBackingBean.setErrorMessage(
													FbMessageUtil.getLabel("Company_Name_is_already_added"));
											return;
										}
									}

								}
								personalinfo.setCompanyID(personalinfo.getCompanyID() + "," + companyArray[0]);
								personalinfo.setCompanyName(personalinfo.getCompanyName() + "," + companyArray[1]);
							} else {
								personalinfo.setCompanyID(companyArray[0]);
								personalinfo.setCompanyName(companyArray[1]);
							}
							personalinfo.setLastUpdated(new Date());
							personalinfo.setLastUpdatedBy(AdminUser.getUser().getUserName());
							registration.setLastUpdated(new Date());
							registration.setLastUpdatedBy(AdminUser.getUser().getUserName());
							session.update(personalinfo);
							session.update(registration);
							PlacedStudentTableBean bean = (PlacedStudentTableBean) TpoUtil
									.getManagedBean(PlacedStudentTableBean.class.getSimpleName());
							if (bean != null) {
								bean.setSelectedStudenets(null);
								bean.setSelectedStudenetList(null);
							}

							if (commonDBBean != null) {
								commonDBBean.loadSelectedStudents();
							}
							if (emailUtill != null) {
								recipients.add(registration.getEmail());
							}
						}
					}
					UIBackingBean
							.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success3, selectedStudentList.size()));
					if (emailUtill != null) {
						String subject = FbMessageUtil.getLabel("Congratulation");
						StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear"));
						message.append(",<br><font color=green size = 5>" + subject + "</font>").append("<br>")
								.append("<font color=green size = 5>" + FbMessageUtil.getLabel("You_are_selected_in")
										+ companyName + ".</font><br>");
						message.append(TpoUtil.getMesageString());
						emailUtill.postMail(recipients, subject, message.toString(), TpoUtil.ADMIN_EMAIL,
								Message.RecipientType.TO);
					}
					studentList = null;
					companyName = null;
				}
			} else {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error3));
			}
			studentList = null;
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteForCompany() {
		try {
			if (companyName != null && !companyName.equals("")) {
				String[] companyArray = companyName.split("#");
				if (selectedStudentList == null || selectedStudentList.size() == 0) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error4));
				} else {
					Session session = sessionFactory.getCurrentSession();
					EmailUtil emailUtill = getEmailInstance();
					List<String> recipients = null;
					if (emailUtill != null) {
						recipients = new ArrayList<String>(selectedStudentList.size());
					}

					for (Registration registration : selectedStudentList) {
						Personalinfo personalinfo = (Personalinfo) registration.getPersonalinfo();

						if (personalinfo != null && (personalinfo.getCompanyName() != null
								&& !"".equals(personalinfo.getCompanyName()))) {
							if (companyName.equals("All")) {
								personalinfo.setCompanyName("");
								personalinfo.setCompanyID("");
								session.update(personalinfo);
							} else {
								String[] comapanys = personalinfo.getCompanyName().split(",");
								StringBuffer newCompanys = null;
								if (comapanys != null && comapanys.length == 1) {
									newCompanys = new StringBuffer();
								}

								for (String company : comapanys) {
									if (companyArray[1].equals(company)) {
										// do nothing
									} else {
										if (newCompanys == null) {
											newCompanys = new StringBuffer(company);
										} else {
											newCompanys.append(",").append(company);
										}
									}
								}
								personalinfo.setCompanyName(newCompanys.toString());

								String[] comapanyIds = personalinfo.getCompanyID().split(",");
								StringBuffer newCompanyIds = null;
								if (comapanyIds != null && comapanyIds.length == 1) {
									newCompanyIds = new StringBuffer();
								}

								for (String id : comapanyIds) {
									if (companyArray[0].equals(id)) {
										// do nothing
									} else {
										if (newCompanyIds == null) {
											newCompanyIds = new StringBuffer(id);
										} else {
											newCompanyIds.append(",").append(id);
										}
									}
								}
								personalinfo.setCompanyID(newCompanyIds.toString());

								session.update(personalinfo);
								PlacedStudentTableBean bean = (PlacedStudentTableBean) TpoUtil
										.getManagedBean(PlacedStudentTableBean.class.getSimpleName());
								if (bean != null) {
									bean.setSelectedStudenets(null);
									bean.setSelectedStudenetList(null);
								}
								if (emailUtill != null) {
									recipients.add(registration.getEmail());
								}
							}
						} else {
							Object param[] = new Object[2];
							param[0] = companyName;
							param[1] = personalinfo.getRollnumber();
							UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("No_Company", param));
							companyName = null;
							return;
						}
					}
					UIBackingBean
							.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success3, selectedStudentList.size()));
					if (emailUtill != null) {
						String subject = FbMessageUtil.getLabel("Sorry");
						StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear"));
						message.append(",<br>");
						message.append("<font color=green size = 5>Sorry</font>").append("<br>")
								.append("<font color=red size = 5>" + FbMessageUtil.getLabel("You_are_not_selected_in")
										+ " " + companyName + ".</font><br>");
						message.append(TpoUtil.getMesageString());
						emailUtill.postMail(recipients, subject, message.toString(), TpoUtil.ADMIN_EMAIL,
								Message.RecipientType.TO);
					}
					studentList = null;
					companyName = null;
				}
			} else {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error3));
			}
			studentList = null;

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addToBlackList() {
		try {
			if (selectedStudentList != null && selectedStudentList.isEmpty()) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else if (selectedStudentList.size() > 0 && selectedStudentList.get(0) != null
					&& selectedStudentList.get(0).getBackdetails().getBlackList()) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Selected_record_is_already_blacklisted"));
			} else {
				Session session = sessionFactory.getCurrentSession();
				EmailUtil emailUtill = getEmailInstance();
				List<String> recipients = null;

				for (Registration registration : selectedStudentList) {
					if (!registration.getBackdetails().getBlackList()) {
						String collegeName = getCollege(session, registration);

						if (collegeName != null) {
							Backdetails backdetails = registration.getBackdetails();
							backdetails.setBlackList(true);
							session.update(backdetails);
							if (emailUtill != null) {
								recipients = new ArrayList<String>(1);
								recipients.add(registration.getEmail());
								String subject;
								subject = FbMessageUtil.getLabel("You_are_blacklisted");
								StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear"));
								message.append(registration.getFirstName());
								message.append(" ");
								message.append(registration.getLastName());
								message.append(",");
								message.append("<br>").append("<font color=red size = 5>" + FbMessageUtil.getLabel(
										"Your_Freshers_Buddy_registration_related_information_has_been_updated_Now_you_are_blacklisted")
										+ "</font><br>");
								message.append("<br>").append(subject).append("<br>")
										.append(FbMessageUtil.getLabel("by")).append(" ")
										.append(AdminUser.getUser().getUserName()).append("<br>");
								message.append(backdetails.toString());
								message.append(TpoUtil.getMesageString());
								emailUtill.postMail(recipients, subject, message.toString(), TpoUtil.ADMIN_EMAIL,
										Message.RecipientType.TO);
							}
							UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success15));
						} else {
							UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("You_cannot_BlackList",
									registration.getFirstName() + " " + registration.getLastName()));
						}

					}
				}
				studentList = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeFromBlackList() {
		try {
			if (selectedStudentList != null && selectedStudentList.isEmpty()) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				EmailUtil emailUtill = getEmailInstance();
				List<String> recipients = null;

				for (Registration registration : selectedStudentList) {
					String collegeName = getCollege(session, registration);
					if (collegeName != null) {
						Backdetails backdetails = registration.getBackdetails();
						backdetails.setBlackList(false);
						session.update(backdetails);
						if (emailUtill != null) {
							recipients = new ArrayList<String>(1);
							recipients.add(registration.getEmail());
							String subject;
							subject = FbMessageUtil.getLabel("You_are_Removed_from_Blacklist");
							StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear"));
							message.append(registration.getFirstName());
							message.append(" ");
							message.append(registration.getLastName());
							message.append(",");
							message.append("<br>").append("<font color=green size = 5>" + FbMessageUtil.getLabel(
									"Your_Freshers_Buddy_registration_related_information_has_been_updated_Now_you_are_removed_from_Blacklist")
									+ "</font><br>");
							message.append("<br>").append(subject).append("<br>").append(FbMessageUtil.getLabel("by"))
									.append(" ").append(AdminUser.getUser().getUserName()).append("<br>");
							message.append(backdetails.toString());
							message.append(TpoUtil.getMesageString());
							emailUtill.postMail(recipients, subject, message.toString(), TpoUtil.ADMIN_EMAIL,
									Message.RecipientType.TO);
						}
						UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success16));
					} else {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("You_cannot_BlackList",
								registration.getFirstName() + " " + registration.getLastName()));
					}

				}
				studentList = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public List<Registration> getStudentList() {

		return studentList;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public String getRollNumber() {
		return rollNumber;
	}

	public void setRollNumber(String rollNumber) {
		this.rollNumber = rollNumber;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getPgcourse() {
		return pgcourse;
	}

	public void setPgcourse(String pgcourse) {
		this.pgcourse = pgcourse;
	}

	public String getCollegeName() {
		return collegeName;
	}

	public void setCollegeName(String collegeName) {
		this.collegeName = collegeName;
	}

	public Integer getYearOfPassing() {
		return yearOfPassing;
	}

	public void setYearOfPassing(Integer yearOfPassing) {
		this.yearOfPassing = yearOfPassing;
	}

	public List<Registration> getSelectedStudentList() {
		return selectedStudentList;
	}

	public void setSelectedStudentList(List<Registration> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}

	public void setStudentList(List<Registration> studentList) {
		this.studentList = studentList;
	}

	public void setAdvSearch(AjaxActionEvent event) {
		if (event != null) {
			Ajax ajax = (Ajax) event.getSource();
			if (ajax != null) {
				HtmlSelectBooleanCheckbox checkbox = (HtmlSelectBooleanCheckbox) ajax.getParent();
				if (checkbox != null && checkbox.getValue() != null && !(Boolean) checkbox.getValue()) {
					companyName = null;
					status = null;
					branchName = null;
					semester = null;
					course = null;
					pgcourse = null;
					collegeName = null;
					yearOfPassing = null;

				}
			}
		}
	}

	public void inIt() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Registration.class);
			AdminUser user = AdminUser.getUser();
			List<String> collegeList = null;
			StringBuilder collegeStr = null;
			if (user != null) {
				CommonDBBean bean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
				collegeList = AdminUser.getUser().getCollegeList();
				if (collegeList != null && collegeList.size() > 0) {
					criteria.add(Restrictions.in("collegeName", collegeList));
					for (String string : collegeList) {
						if (collegeStr == null) {
							collegeStr = new StringBuilder("'" + string + "'");
						} else {
							collegeStr.append(",'").append(string).append("'");
						}
					}
				} else {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error2));
					studentList = null;
					return;
				}
			}
			NativeQuery<?> query;
			StringBuffer quertStr = null;
			if ((companyName != null && !companyName.equals("")) || (branchName != null && !branchName.equals(""))
					|| (semester != null && !semester.equals("")) || (course != null && !course.equals(""))
					|| (yearOfPassing != null && yearOfPassing != 0) || (pgcourse != null && !pgcourse.equals(""))
					|| selected || blackListed) {
				quertStr = new StringBuffer(
						"select count(r.rollnumber) from registration r,personalinfo p,backdetails b where r.rollnumber = p.rollnumber and r.rollnumber = b.rollnumber and r.collegeName in ("
								+ collegeStr + ")");
				criteria.createAlias("personalinfo", "personalinfo");
				criteria.createAlias("backdetails", "backdetails");
				if (companyName != null && !companyName.equals("")) {
					criteria.add(Restrictions.ilike("personalinfo.companyName", "%" + companyName + "%"));
					quertStr.append(" and p.companyName like '%" + companyName + "%'");
				}

				if (branchName != null && !branchName.equals("")) {
					criteria.add(Restrictions.ilike("personalinfo.branch", "%" + branchName + "%"));
					quertStr.append(" and p.branch like '%" + branchName + "%'");
				}
				if (semester != null && !semester.equals("")) {
					criteria.add(Restrictions.eq("personalinfo.semester", semester));
					quertStr.append(" and p.semester = '" + semester + "'");
				}
				if (course != null && !course.equals("")) {
					criteria.add(Restrictions.eq("personalinfo.course", course));
					quertStr.append(" and p.course = '" + course + "'");
				}
				if (pgcourse != null && !pgcourse.equals("")) {
					criteria.add(Restrictions.eq("personalinfo.postGraduationCourse", pgcourse));
					quertStr.append(" and p.postGraduationCourse = '" + pgcourse + "'");
				}
				if (yearOfPassing != null && yearOfPassing != 0) {
					criteria.add(Restrictions.eq("personalinfo.yearOfPassing", +yearOfPassing));
					quertStr.append(" and p.yearOfPassing = " + yearOfPassing + "");
				}
				if (selected) {
					criteria.add(Restrictions.ne("personalinfo.companyName", ""));
					quertStr.append(" and p.companyName != ''");
				}
				if (blackListed) {
					criteria.add(Restrictions.eq("backdetails.blackList", true));
					quertStr.append(" and b.blackList = true");
				}
			} else {
				quertStr = new StringBuffer(
						"select count(r.rollnumber) from registration r,personalinfo p where 1=1 and r.collegeName in ("
								+ collegeStr + ")");
			}

			if (rollNumber != null && !rollNumber.equals("")) {
				criteria.add(Restrictions.eq("rollnumber", rollNumber).ignoreCase());
				quertStr.append(" and r.rollnumber = '" + rollNumber + "'");
			}

			if (collegeName != null && !collegeName.equals("")) {
				criteria.add(Restrictions.ilike("collegeName", "%" + collegeName + "%"));
				quertStr.append(" and r.collegeName like '%" + collegeName + "%'");
			}

			if (status != null && !status.equals("")) {
				criteria.add(Restrictions.eq("approved", status.equals("A") ? true : false));
				if (status.equals("A")) {
					quertStr.append(" and r.approved = true");
				} else {
					quertStr.append(" and r.approved = false");
				}
			}

			if (selectedFlag) {
				criteria.createAlias("personalinfo", "personalinfo");
				criteria.add(Restrictions.ne("personalinfo.companyName", ""));
				quertStr.append(" and p.companyName !='' ");
			}

			query = session.createNativeQuery(quertStr.toString());
			criteria.addOrder(Order.asc("rollnumber"));
			criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			int totalCount = ((BigInteger) query.getSingleResult()).intValue();
			pagination.setTotalDisplayRecords(totalCount);
			studentList = criteria.list();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public List<String> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<String> companyList) {
		this.companyList = companyList;
	}

	public void setCompany(AjaxActionEvent event) {
		try {
			if (event != null) {
				Session session = null;
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					session = sessionFactory.getCurrentSession();
					Criteria criteria = session.createCriteria(Company.class);
					String companyNames = (String) link.getValue();
					String[] cmpnyNames = companyNames.split(",");
					criteria.add(Restrictions.in("companyname", cmpnyNames));
					companyObjList = new ArrayList<Company>(criteria.list().size());
					companyObjList = criteria.list();
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

	public String showInfo() {
		try {
			if (selectedStudentList != null && selectedStudentList.size() == 0) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_select_at_least_one_record_to_see_full_information"));
			} else {
				if (selectedStudentList.size() > 1) {
					UIBackingBean.setInfoMessage(
							FbMessageUtil.getLabel("Please_select_only_one_record_to_see_full_information"));
				} else {
					WebFlow webFlow = (WebFlow) TpoUtil.getManagedBean(WebFlow.class.getSimpleName());
					if (webFlow != null) {
						webFlow.setSelectedPage(WebFlowTabName.MI);
					}
					Registration registration = selectedStudentList.get(0);
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
							return webFlow.mainPage();
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
		return null;
	}

	public boolean isAddStudentFlag() {
		return addStudentFlag;
	}

	public void setAddStudentFlag(boolean addStudentFlag) {
		this.addStudentFlag = addStudentFlag;
	}

	public List<Company> getCompanyObjList() {
		return companyObjList;
	}

	public void setCompanyObjList(List<Company> companyObjList) {
		this.companyObjList = companyObjList;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isBlackListed() {
		return blackListed;
	}

	public void setBlackListed(boolean blackListed) {
		this.blackListed = blackListed;
	}

	public List<Result> getResultList() {
		return resultList;
	}

	public void setResultList(List<Result> resultList) {
		this.resultList = resultList;
	}

	public String getClickedRollNumber() {
		return clickedRollNumber;
	}

	public void setClickedRollNumber(String clickedRollNumber) {
		this.clickedRollNumber = clickedRollNumber;
	}

	public Boolean getSelectedFlag() {
		return selectedFlag;
	}

	public void setSelectedFlag(Boolean selectedFlag) {
		this.selectedFlag = selectedFlag;
	}

}