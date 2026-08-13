package com.beans;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.email.EmailUtil;
import com.hibernate.Exam;
import com.hibernate.Registration;
import com.hibernate.Result;
import com.hibernate.ResultId;
import com.util.CCPConstant;
import com.util.Encryption;
import com.util.FbMessageUtil;
import com.util.TpoUtil;

@Repository("LoginBean")
@Transactional(readOnly = true)
@Scope("request")
public class LoginBean extends Parent {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private ImageBean imageBean;

	private Logger logger = LoggerFactory.getLogger(LoginBean.class);

	private String enrollmentNo;
	private String password;
	private String email;

	private String testName;

	private String firstName;

	private String lastName;

	private List<String> testList = new ArrayList<String>();
	
	private String oldPassword;
	private String confirmPassword;

	

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String validateStudentLogin() {
		String action = "";
		try {
			if (TpoUtil.isApplicationExpired()) {
				return action;
			}
			if (imageBean.isImageCorrect()) {
				String encryptedPassword = Encryption.getEncryptedString(password);
				Session session = sessionFactory.getCurrentSession();
				Criteria criteria = session.createCriteria(Registration.class);
				criteria.add(Restrictions.eq("rollnumber", enrollmentNo).ignoreCase());
				criteria.add(Restrictions.eq("password", encryptedPassword));
				Registration registration = (Registration) criteria.uniqueResult();
				if (registration == null) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Incorrect_Enrollment_No_or_Password"));
				} else {

					if (testName != null) {
						Exam exam = (Exam) session.get(Exam.class, testName);
						if (exam != null) {

							Date date = new Date();
							if (exam.getValidFrom() != null && exam.getValidTo() != null) {
								if (!(date.after(exam.getValidFrom()) && date.before(exam.getValidTo()))) {
									UIBackingBean.setErrorMessage(
											FbMessageUtil.getLabel("Exam_Expired", exam.getTestname()));
									return action;
								}
							}

							BigInteger count = getQuestionCount(exam);
							if (count != null && count.intValue() < (exam.getEndrange() - exam.getStartrange())) {
								UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(
										"Your_can_not_give_this_exam_because_question_set_is_not_complete_for_exam",
										exam.getTestname()));
								return "";
							}

							if (!CCPConstant.ADMIN.equals(exam.getCreatedBy())) {
								String userNameQuery = "select userName from college where collegeName='"+registration.getCollegeName()+"'";
								NativeQuery<String> query = session.createSQLQuery(userNameQuery);
								String userName = (String)query.uniqueResult();
								if (userName == null) {
									UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(
											"Your_can_not_give_this_exam_because_your_college_has_not_created_this_exam"));
									return "";
								} else {
									List<String> userList = getUserList(userName);
									criteria = session.createCriteria(Exam.class)
											.setProjection(Projections.property("testname"));
									criteria.add(Restrictions.in("createdBy", userList));
									criteria.add(Restrictions.eq("testname", testName).ignoreCase());
									String examName = (String) criteria.uniqueResult();
									if (examName == null) {
										UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(
												"Your_can_not_give_this_exam_because_your_college_has_not_created_this_exam"));
										return "";
									}
								}
							}

							QuestionBean questionBean = (QuestionBean) TpoUtil
									.getManagedBean(QuestionBean.class.getSimpleName());
							questionBean.setLoginName(registration.getRollnumber());
							questionBean.setTest(exam.getTestname());
							questionBean.setStartNumber(exam.getStartrange());
							questionBean.setEndNumber(exam.getEndrange());
							questionBean.setNoOfQuestions(exam.getNoOfQuestions());
							questionBean.setMinute(String.valueOf(exam.getMinute() - 1));
							Double time = new Double(questionBean.getMinute().concat(".60"));
							NativeQuery<String> collegeQ = session
									.createSQLQuery("SELECT userName FROM college  where CollegeName = '"
											+ registration.getCollegeName() + "'");
							String userName = (String) collegeQ.uniqueResult();
							insertIntoResult(session, enrollmentNo, testName, exam.getNoOfQuestions(), time, userName);
							if (firstName != null && !firstName.isEmpty())
								registration.setFirstName(firstName);
							if (lastName != null && !lastName.isEmpty())
								registration.setLastName(lastName);
							if (email != null && !email.isEmpty()) {
								registration.setEmail(email);
								registration.setEmailVarified(false);
							}
							if ((firstName != null && !firstName.isEmpty()) || (lastName != null && !lastName.isEmpty())
									|| (email != null && !email.isEmpty())) {
								session.update(registration);
							}
							questionBean.setFirstName(registration.getFirstName());
							questionBean.setLastName(registration.getLastName());
							questionBean.setEmail(registration.getEmail());
							action = "mainTestNewWindow";
						} else {
							UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Error27"));
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
		return action;
	}

	public void sendStudentPassword() {
		try {
			EmailUtil emailUtill = getEmailInstance();
			if (emailUtill != null) {
				Session session = sessionFactory.getCurrentSession();
				Criteria criteria = session.createCriteria(Registration.class);
				criteria.add(Restrictions.eq("rollnumber", enrollmentNo));
				criteria.add(Restrictions.eq("email", email));
				Registration registration = (Registration) criteria.uniqueResult();
				if (registration == null) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Incorrect_Enrollment_No_or_E_mail"));
				} else {

					List<String> recipients = new ArrayList<String>(1);
					recipients.add(registration.getEmail());
					StringBuffer subject = new StringBuffer(FbMessageUtil.getLabel("Dear"));
					;
					subject.append(registration.getFirstName());
					subject.append(" ");
					subject.append(registration.getLastName());
					subject.append(",Password forgot link");
					StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear"));
					message.append(registration.getFirstName());
					message.append(" ");
					message.append(registration.getLastName());
					message.append(",<br>");
					message.append(FbMessageUtil.getLabel("reset_your_password"));
					message.append(" <br><font color=green size=5>"
							+ FbMessageUtil.getLabel("Your_Enrollment_No_is", registration.getRollnumber()));
					message.append("</font>");
					message.append("<br><a href='" + TpoUtil.getBasePath(null)
							+ "login/resetPassword.faces?pnsgffmffbhvgkbf=" + registration.getPassword()
							+ "&dfdnmfbnndfn=" + registration.getRollnumber() + "'>"
							+ FbMessageUtil.getLabel("Please_click_on_this_link_to_reset_your_password") + "</a><br>");
					message.append(TpoUtil.getMesageString());
					emailUtill.postMail(recipients, subject.toString(), message.toString(), TpoUtil.ADMIN_EMAIL,
							Message.RecipientType.TO);
					UIBackingBean
							.setSuccessMessage(FbMessageUtil.getLabel("Password_reset_Email_has_been_sent_on", email));
				}
			} else {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("In_Local_System_Mode_email_can_not_be_sent"));
			}
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public List<String> getTestList() {
		try {
			if (testList.size() == 0) {
				Session session = sessionFactory.getCurrentSession();
				NativeQuery<String> query = session.createSQLQuery("select testname from exam");
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEnrollmentNo() {
		return enrollmentNo;
	}

	public void setEnrollmentNo(String enrollmentNo) {
		this.enrollmentNo = enrollmentNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	private BigInteger getQuestionCount(Exam exam) {

		BigInteger totalCount = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			NativeQuery<?> query = session
					.createSQLQuery("select count(qno) from questions where qtype = '" + exam.getTestname() + "'");
			totalCount = (BigInteger) query.uniqueResult();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return totalCount;
	}

	public void insertIntoResult(Session session, String loginName, String testName, Integer noOfQuestions, Double time,
			String createdBy) {

		Criteria criteria = session.createCriteria(Result.class);
		ResultId id = new ResultId(loginName, 1);
		criteria.add(Restrictions.eq("id", id));
		Result resultObj = (Result) criteria.uniqueResult();
		Result result = new Result();
		if (resultObj == null) {
			result.setId(id);
			TpoUtil.getSession().setAttribute("loginAttempt", 1);
		} else {
			NativeQuery<?> query = session
					.createSQLQuery("select max(attempt)+1 from result where loginname = '" + loginName + "'");
			BigInteger loginAttempt = (BigInteger) query.uniqueResult();
			TpoUtil.getSession().setAttribute("loginAttempt", loginAttempt.intValue());
			id.setattempt(loginAttempt.intValue());
			result.setId(id);
		}
		result.setResult(CCPConstant.TEST_STARTED);
		result.setTotalnumbers(new Double(0));
		result.setTestName(testName);
		result.setNumberOfQuestion(noOfQuestions);
		result.setDateTaken(new Date());
		result.setTotalTimeTaken(time);
		result.setTotalTime(time);
		result.setCreatedBy(createdBy);
		session.save(result);
	}

	public List<String> getUserList(String userName) {
		String sub_collegeName = "select distinct t.collegeName from (select distinct c.collegeName from college c where c.userName='"
				+ userName
				+ "' UNION ALL select distinct cg.collegeName from college c,collegegroup cg where cg.userName=c.userName and cg.status='A' and c.userName='"
				+ userName + "') as t";
		String sub_userName = "select distinct c.userName from college c where c.collegeName in(" + sub_collegeName
				+ ")";
		Session session = sessionFactory.getCurrentSession();
		NativeQuery<String> query = session.createSQLQuery(sub_userName);
		List<String> userNames = query.getResultList();
		if (userNames == null) {
			userNames = new ArrayList<String>(1);
			userNames.add(userName);
		} else {
			if (!userNames.contains(userName)) {
				userNames.add(userName);
			}
		}
		return userNames;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String resetPassword() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			oldPassword = context.getExternalContext().getRequestParameterMap().get("oldpassword");
			String rollnumber = context.getExternalContext().getRequestParameterMap().get("rollnumber");
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Registration.class);
			criteria.add(Restrictions.eq("rollnumber", rollnumber));
			criteria.add(Restrictions.eq("password", oldPassword));
			Registration registration = (Registration) criteria.uniqueResult();
			if (registration == null) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Something_is_wrong_is_with_this_URL"));
				TpoUtil.getRespose().sendRedirect(
						TpoUtil.getBasePath(null) + "login/resetPassword.faces?pnsgffmffbhvgkbf="
								+ oldPassword + "&dfdnmfbnndfn=" + rollnumber);
			} else {
				String encryptedPass = Encryption.getEncryptedString(confirmPassword);
				registration.setPassword(encryptedPass);
				session.update(registration);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Password_changed_successfully"));
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return "";
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	

}
