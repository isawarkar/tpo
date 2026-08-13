package com.beans;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;

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

import com.dao.CommonDBBean;
import com.dao.StudentNoticeBean;
import com.email.EmailUtil;
import com.hibernate.Notice;
import com.hibernate.Registration;
import com.hibernate.Userdetails;
import com.util.CCPConstant;
import com.util.Encryption;
import com.util.FbMessageUtil;
import com.util.TpoUtil;



@Repository("StudentLogin")
@Transactional(readOnly = true)
@Scope("request")
public class StudentLogin extends Parent {

	@Autowired
	private SessionFactory sessionFactory;

	private Logger logger = LoggerFactory.getLogger(StudentLogin.class);

	private String enrollmentNo;
	private String password;
	private String email;
	
	private String oldPassword;
	private String confirmPassword;
	
	private boolean passwordchanged;
	
	@Autowired
	private CommonDBBean commonDBBean;

	public String goToStudentHomePage() {
		if (Student.getStudent().getTheme() != null) {
			return "studentHomePage"+Student.getStudent().getTheme();
		} else {
			//Default
			return "studentHomePageT2";
		}
	}

	

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void validateStudentLogin() {
		try {
			if (TpoUtil.isApplicationExpired()) {
				return;
			}
			String encryptedPassword = Encryption.getEncryptedString(password);
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Registration.class);
			criteria.add(Restrictions.eq("rollnumber", enrollmentNo));
			criteria.add(Restrictions.eq("password", encryptedPassword));
			Registration registration = (Registration) criteria.uniqueResult();
			if (registration == null) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Incorrect_Enrollment_No_or_Password"));
			} else {
				if (!registration.getStatus()) {
					String userName = commonDBBean.getUserNameByCollegeName(registration.getCollegeName());
					Userdetails userinfo = commonDBBean.getUserInfo(userName);
					Object param[] = new Object[2];
					if (userinfo != null) {
						param[0] = userinfo.getMobleNo();
						param[1] = userinfo.getEmail();
					} else {
						param[0] = "Admin";
						param[1] = TpoUtil.ADMIN_EMAIL;
					}
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("account_blocked", param));
					return;
				}
				Student student = Student.getStudent();
				if (student != null) {
					student.setUserName(registration.getFirstName() + " " + registration.getLastName());
					student.setRollNumber(registration.getRollnumber());
					student.setTheme(registration.getTheme());
					student.setColorCode(registration.getColor());
					NativeQuery<String> query = session.createSQLQuery(
							"select userName from college where collegeName = '" + registration.getCollegeName() + "'");
					student.setCreateBy((String) query.uniqueResult());
					StudentNoticeBean noticeBean = (StudentNoticeBean) TpoUtil
							.getManagedBean(StudentNoticeBean.class.getSimpleName());
					if (noticeBean != null) {
						query = session.createSQLQuery(
								"SELECT CONCAT('Company Name=',companyName,'        Date=',DATE(date),'     Package=',package,'     Time=',time) FROM hallticket where hallticketId in (SELECT hallTicketId FROM hallticketconnect where rollnumber = '"
										+ student.getRollNumber() + "') and isActive=true;");
						List<String> campusList = query.list();
						if (campusList != null && campusList.size() > 0) {
							Collections.sort(campusList);
							noticeBean.setCampusList(campusList);
						}

						criteria = session.createCriteria(Notice.class);
						List<String> createdByList = new ArrayList<>(2);
						createdByList.add(student.getCreateBy());
						createdByList.add(student.getRollNumber());
						criteria.add(Restrictions.eq("active", true));
						criteria.add(Restrictions.in("createdBy", createdByList));
						List<Notice> nList = criteria.list();
						List<String> noticeList = new ArrayList<String>(nList.size());
						for (Notice notice : nList) {
							if (notice.getClassName().isEmpty()) {
								noticeList.add(notice.getNoticeName());
							}
						}
						if (noticeList != null && noticeList.size() > 0) {
							Collections.sort(noticeList);
							noticeBean.setNoticeList(noticeList);
						}
						nList = null;
					}
				}
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



	public String logout() {
		String logout = "logout";
		TpoUtil.getHttpSession().invalidate();
		return logout;

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
				passwordchanged = false;
			} else {
				String encryptedPass = Encryption.getEncryptedString(confirmPassword);
				registration.setPassword(encryptedPass);
				session.update(registration);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Password_changed_successfully"));
				passwordchanged = true ;
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



	public boolean isPasswordchanged() {
		return passwordchanged;
	}



	public void setPasswordchanged(boolean passwordchanged) {
		this.passwordchanged = passwordchanged;
	}
	
	
	
}
