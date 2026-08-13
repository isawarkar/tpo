package tpo.dao;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.openfaces.component.command.CommandLink;
import org.openfaces.event.AjaxActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.beans.Parent;
import tpo.beans.StudentRegistrationBean;
import tpo.beans.UIBackingBean;
import tpo.email.EmailUtil;
import tpo.hibernate.College;
import tpo.hibernate.Company;
import tpo.hibernate.Contactinfo;
import tpo.hibernate.Exam;
import tpo.hibernate.HallTicket;
import tpo.hibernate.HallTicketConnect;
import tpo.hibernate.HallTicketConnectID;
import tpo.hibernate.Logindetails;
import tpo.hibernate.Notice;
import tpo.hibernate.Personalinfo;
import tpo.hibernate.Registration;
import tpo.hibernate.Result;
import tpo.hibernate.ResultId;
import tpo.hibernate.Userdetails;
import tpo.hibernate.annotation.Bookmark;
import tpo.hibernate.annotation.BookmarkID;
import tpo.hibernate.annotation.BroadCastMessage;
import tpo.hibernate.annotation.CommonData;
import tpo.hibernate.annotation.CustomerReview;
import tpo.hibernate.annotation.DocumentList;
import tpo.hibernate.annotation.NewsLetter;
import tpo.hibernate.annotation.SessionData;
import tpo.hibernate.annotation.StudentFeeDetails;
import tpo.imageservice.client.FileUploadUtility;
import tpo.util.AES;
import tpo.util.CCPConstant;
import tpo.util.Encryption;
import tpo.util.FbMessageUtil;
import tpo.util.IMAGECONS;
import tpo.util.SystemUtil;
import tpo.util.TpoUtil;

@Repository("CommonDBBean")
@Transactional(readOnly = true)
public class CommonDBBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(CommonDBBean.class);

	private List<String> college = new ArrayList<String>();
	private List<String> clienList = new ArrayList<String>();
	private List<String> testList = new ArrayList<String>();
	private List<String> adminMessageList = null;
	private List<String> campusList = null;

	private String oldPassword;
	private String confirmPassword;

	private String envirnment;

	private String emailAddress;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	FileUploadUtility fileUploadUtility;

	private List<Company> cList;

	private List<String> clientLogoList;

	List<Notice> noticeList = null;

	List<HallTicket> oppeninglist = null;

	List<CustomerReview> customerReviewList = null;

	List<Registration> selectedStudentList = null;

	public void loadNotice() {
		this.noticeList = null;
		getNoticeList();
	}

	public void loadOpennings() {
		this.oppeninglist = null;
		getOpeningList();
	}

	public void loadReviews() {
		this.customerReviewList = null;
		getReviewList();
	}

	public void loadSelectedStudents() {
		this.selectedStudentList = null;
		getSelectedStudenets();
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public List<String> getClientList() {
		try {
			if (clienList.size() == 0) {
				Session session = sessionFactory.getCurrentSession();
				NativeQuery<String> userQuery = session
						.createSQLQuery("SELECT userName FROM logindetails where logodisplay=true and active=true");
				clienList = userQuery.list();
				Collections.sort(clienList);
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return clienList;
	}

	public List<String> getClientLogoList() {
		try {
			if (clientLogoList == null) {
				Session session = sessionFactory.getCurrentSession();
				Criteria criteria = session.createCriteria(Userdetails.class)
						.createAlias("logindetails", "logindetails").setProjection(Projections.property("userName"));
				criteria.add(Restrictions.eq("logindetails.logoDisplay", true));
				clientLogoList = criteria.list();
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return clientLogoList;
	}

	public List<String> getCollegeList() {
		try {
			if (college.size() == 0) {
				Session session = sessionFactory.getCurrentSession();
				NativeQuery<String> query = session.createSQLQuery("select collegeName from college");
				college = query.list();
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return college;
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

	public Boolean isRecordExist(String rollNumber) {
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Registration.class)
					.setProjection(Projections.property("rollnumber"));
			criteria.add(Restrictions.eq("rollnumber", rollNumber));
			String rollNum = (String) criteria.uniqueResult();
			if (rollNum == null) {
				return false;
			} else {
				rollNum = null;
				return true;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public Boolean isCollegeCodeCorrect(String collegeCode) {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(College.class)
					.setProjection(Projections.property("collegeName"));
			criteria.add(Restrictions.eq("collegeName", collegeCode));
			String collegeName = (String) criteria.uniqueResult();
			if (collegeName == null) {
				return false;
			} else {
				collegeName = null;
				return true;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void update(Object obj) {
		try {
			Session session = sessionFactory.getCurrentSession();
			session.update(obj);
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void setPassword() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Registration.class);
			StudentRegistrationBean registrationBean = (StudentRegistrationBean) TpoUtil
					.getManagedBean(StudentRegistrationBean.class.getSimpleName());
			criteria.add(Restrictions.eq("rollnumber", registrationBean.getRegistration().getRollnumber()));
			Registration registration = (Registration) criteria.uniqueResult();
			if (registration == null) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_enter_valid_old_Password"));
			} else {
				registration.setPassword(Encryption.getEncryptedString(confirmPassword));
				session.update(registration);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Password_changed_successfully"));
				EmailUtil emailUtill = getEmailInstance();
				if (emailUtill != null) {
					StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear"));
					message.append(registration.getFirstName()).append(" ").append(registration.getLastName());
					message.append(",<br><br>");
					message.append(FbMessageUtil.getLabel("Password_reset_successfully"));
					message.append("<br>");
					message.append("<br>");
					message.append(FbMessageUtil.getLabel("Your_New_password_is", confirmPassword));
					message.append("<br><br>");
					message.append(TpoUtil.getMesageString());
					List<String> to = new ArrayList<String>(1);
					to.add(registrationBean.getRegistration().getEmail());
					emailUtill.postMail(to, FbMessageUtil.getLabel("Password_reset_successfully"), message.toString(),
							TpoUtil.ADMIN_EMAIL, Message.RecipientType.BCC);
				}
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
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void resetDefaultPassword() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Registration.class);
			StudentRegistrationBean registrationBean = (StudentRegistrationBean) TpoUtil
					.getManagedBean(StudentRegistrationBean.class.getSimpleName());
			criteria.add(Restrictions.eq("rollnumber", registrationBean.getRegistration().getRollnumber()));
			Registration registration = (Registration) criteria.uniqueResult();
			if (registration == null) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_enter_valid_old_Password"));
			} else {
				String pass = AES.symmetricDecrypt(SystemUtil.getLabel("defaultPass"), TpoUtil.geyKeyInfo());
				registration.setPassword(Encryption.getEncryptedString(pass));
				session.update(registration);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Password_changed_successfully"));
				EmailUtil emailUtill = getEmailInstance();
				if (emailUtill != null) {
					StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear"));
					message.append(registration.getFirstName()).append(" ").append(registration.getLastName());
					message.append(",<br><br>");
					message.append(FbMessageUtil.getLabel("Password_reset_successfully"));
					message.append("<br>");
					message.append("<br>");
					message.append(FbMessageUtil.getLabel("Your_New_password_is", pass));
					message.append("<br><br>");
					message.append(TpoUtil.getMesageString());
					List<String> to = new ArrayList<String>(1);
					to.add(registrationBean.getRegistration().getEmail());
					emailUtill.postMail(to, "Password reset sucessfully", message.toString(), TpoUtil.ADMIN_EMAIL,
							Message.RecipientType.BCC);
				}
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
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String resetPassword() {
		/*
		 * try { FacesContext context = FacesContext.getCurrentInstance(); oldPassword =
		 * context.getExternalContext().getRequestParameterMap().get("oldpassword");
		 * String rollnumber =
		 * context.getExternalContext().getRequestParameterMap().get("rollnumber");
		 * Session session = sessionFactory.getCurrentSession(); Criteria criteria =
		 * session.createCriteria(Registration.class);
		 * criteria.add(Restrictions.eq("rollnumber", rollnumber));
		 * criteria.add(Restrictions.eq("password", oldPassword)); Registration
		 * registration = (Registration) criteria.uniqueResult(); if (registration ==
		 * null) { UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(
		 * "Something_is_wrong_is_with_this_URL")); TpoUtil.getRespose().sendRedirect(
		 * TpoUtil.getBasePath(null) +
		 * "xhtml/ui/resetStudentPassword.faces?pnsgffmffbhvgkbf=" + oldPassword +
		 * "&dfdnmfbnndfn=" + rollnumber); } else { String encryptedPass =
		 * Encryption.getEncryptedString(confirmPassword);
		 * registration.setPassword(encryptedPass); session.update(registration);
		 * UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(
		 * "Password_changed_successfully"));
		 * 
		 * StudentRegistrationBean bean = (StudentRegistrationBean) TpoUtil
		 * .getManagedBean(StudentRegistrationBean.class.getSimpleName()); if (bean !=
		 * null) { bean.setCurrentMode(CCPConstant.UPDATE);
		 * bean.setCurrentCourse(registration.getPersonalinfo().getCurrentCourse());
		 * bean.setRegistration(registration);
		 * bean.setPersonalinfo(registration.getPersonalinfo());
		 * bean.setPercentageinfo(registration.getPercentageinfo());
		 * bean.setBackdetails(registration.getBackdetails());
		 * bean.setContactinfo(registration.getContactinfo());
		 * bean.setAchivements(registration.getAchivements()); }
		 * 
		 * Student student = Student.getStudent(); if (student != null) {
		 * student.setUserName(registration.getFirstName() + " " +
		 * registration.getLastName());
		 * student.setRollNumber(registration.getRollnumber()); NativeQuery<?> query =
		 * session.createSQLQuery( "select userName from college where collegeName = '"
		 * + registration.getCollegeName() + "'"); student.setCreateBy((String)
		 * query.uniqueResult()); student.setTheme(registration.getTheme());
		 * student.setColorCode(registration.getColor()); return "studentHomePage" +
		 * Student.getStudent().getTheme(); } } } catch (NoSuchAlgorithmException e) {
		 * logger.error(e.getMessage()); e.printStackTrace(); } catch
		 * (UnsupportedEncodingException e) { logger.error(e.getMessage());
		 * e.printStackTrace(); } catch (HibernateException e) {
		 * logger.error(e.getMessage()); e.printStackTrace(); } catch (IOException e) {
		 * logger.error(e.getMessage()); logger.error(e.getMessage());
		 * e.printStackTrace(); } catch (Exception e) { logger.error(e.getMessage());
		 * e.printStackTrace(); }
		 * 
		 * return "";
		 */ return "";
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String resetUserPassword() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			oldPassword = context.getExternalContext().getRequestParameterMap().get("oldpassword");
			String userName = context.getExternalContext().getRequestParameterMap().get("rollnumber");
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Logindetails.class);
			criteria.add(Restrictions.eq("userName", userName));
			criteria.add(Restrictions.eq("password", oldPassword));
			Logindetails userinfo = (Logindetails) criteria.uniqueResult();
			if (userinfo != null) {
				if (userinfo.getActive()) {
					String encryptedPass = Encryption.getEncryptedString(confirmPassword);
					userinfo.setPassword(encryptedPass);
					AdminUser user = AdminUser.getUser();
					userinfo.setLastLogin(new Date());
					user.setUserName(userinfo.getUserName());
					StringBuffer lastLogin = new StringBuffer("");
					lastLogin.append(
							TpoUtil.getDateToString(CCPConstant.DATE_FORMAT_ddMMyyyyhhmm, userinfo.getLastLogin()))
							.append(" IST");
					user.setLastLogin(lastLogin.toString());
					user.setEtsLogin(false);
					user.setEmail(userinfo.getUserdetails().getEmail());
					if (userinfo.getRole().equals("A")) {
						user.setRole(CCPConstant.ADMIN);
					} else if (userinfo.getRole().equals("U")) {
						user.setRole(CCPConstant.USER);
					} else if (userinfo.getRole().equals("C")) {
						user.setRole(CCPConstant.COLLEGE);
					} // N is used for Company
					else if (userinfo.getRole().equals("N")) {
						user.setRole(CCPConstant.COMPANY);
					} else if (userinfo.getRole().equals("S")) {
						user.setRole(CCPConstant.SUPERUSER);
					}
					session.update(userinfo);
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Password_changed_successfully"));
					userName = "";

				} else {
					UIBackingBean.setSuccessMessage(
							FbMessageUtil.getLabel("Your_account_is_not_active_Please_contact_admin"));
				}
			} else {
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Please_try_again_after_some_time"));

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
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return "";
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String collegeGroupRequest() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			String collegeName = context.getExternalContext().getRequestParameterMap().get("collegeName");
			String userName = context.getExternalContext().getRequestParameterMap().get("userName");
			String action = context.getExternalContext().getRequestParameterMap().get("action");
			String id = context.getExternalContext().getRequestParameterMap().get("id");
			Session session = sessionFactory.getCurrentSession();
			if ((collegeName != null && !collegeName.isEmpty()) && (userName != null && !userName.isEmpty())
					&& (action != null && !action.isEmpty()) && (id != null && !id.isEmpty())) {
				NativeQuery<?> query;
				String quertStr = null;
				if ("Approve".equals(action)) {
					quertStr = "update collegegroup set status='A' where id=" + id + " and collegeName='" + collegeName
							+ "' and userName='" + userName + "'";
				} else if ("Reject".equals(action)) {
					quertStr = "update collegegroup set status='R' where id=" + id + " and collegeName='" + collegeName
							+ "' and userName='" + userName + "'";
				}
				query = session.createNativeQuery(quertStr);
				int count = query.executeUpdate();
				if (count > 0) {
					if ("Approve".equals(action)) {
						UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Sucessfully_Approved"));
					} else if ("Reject".equals(action)) {
						UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Sucessfully_Rejected"));

					}
				}
			} else {
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Please_try_again_after_some_time"));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return "";
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String unsubscribeFromNews() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			String emailId = context.getExternalContext().getRequestParameterMap().get("emailId");
			Session session = sessionFactory.getCurrentSession();
			NewsLetter letter = session.get(NewsLetter.class, emailId);
			if (letter != null) {
				session.delete(letter);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("You_are_unsubscribe_successfully"));
			} else {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("No_Record_Found"));
			}
		} catch (HibernateException e) {

			e.printStackTrace();
		}
		return "";
	}

	public List<String> getCollege() {
		return college;
	}

	public void setCollege(List<String> college) {
		this.college = college;
	}

	public List<String> getAdminNoticeList() {
		try {
			/* if (adminMessageList == null) { */
			AdminUser user = AdminUser.getUser();
			if (user != null && user.getRole() != null
					&& (CCPConstant.ADMIN.equals(user.getRole()) || CCPConstant.USER.equals(user.getRole())
							|| CCPConstant.COLLEGE.equals(user.getRole())
							|| CCPConstant.COMPANY.equals(user.getRole()))) {
				Session session = sessionFactory.getCurrentSession();
				String pattern = "yyyy-MM-dd hh:mm:ss";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
				String date = simpleDateFormat.format(new Date());
				String supperUser = SystemUtil.getLabel("supperUser");
				NativeQuery<String> query = session.createSQLQuery("SELECT noticeName FROM notice where createdBy='"
						+ supperUser + "' and active = true and expiry >= '" + date + "'");
				adminMessageList = query.list();

			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return adminMessageList;

	}

	public List<String> getCampusListByUserName(String userName) {
		campusList = getCompanyListByUserName();
		return campusList;
	}

	public List<String> getCompanyListByUserName() {
		try {
			Session session = sessionFactory.getCurrentSession();
			List<String> userNames = AdminUser.getUser().getUserList();
			String queryStr = "select CONCAT(companyID,'#',companyName) from company where createdBy in("
					+ TpoUtil.getComaSeprateValue(userNames) + ")";
			NativeQuery<String> query = session.createSQLQuery(queryStr);
			campusList = query.list();
			if (campusList != null)
				Collections.sort(campusList);
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return campusList;
	}

	public List<String> getCompanyList() {
		try {
			Session session = sessionFactory.getCurrentSession();
			List<String> userNames = AdminUser.getUser().getUserList();
			String queryStr = "select companyName from company where createdBy in("
					+ TpoUtil.getComaSeprateValue(userNames) + ")";
			NativeQuery<String> query = session.createSQLQuery(queryStr);
			campusList = query.list();
			if (campusList != null)
				Collections.sort(campusList);
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return campusList;
	}

	public List<String> getCompanyIDList() {
		try {
			Session session = sessionFactory.getCurrentSession();
			List<String> userNames = AdminUser.getUser().getUserList();
			String queryStr = "select companyID from company where createdBy in("
					+ TpoUtil.getComaSeprateValue(userNames) + ")";
			NativeQuery<String> query = session.createSQLQuery(queryStr);
			campusList = query.list();
			if (campusList != null)
				Collections.sort(campusList);
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return campusList;
	}

	public List<String> getCompanyListWithIDByUserName() {
		try {
			Session session = sessionFactory.getCurrentSession();
			List<String> userNames = AdminUser.getUser().getUserList();
			String queryStr = "select CONCAT(companyName, '-', hallTicketId) from hallticket where userName in("
					+ TpoUtil.getComaSeprateValue(userNames) + ")";
			NativeQuery<String> query = session.createSQLQuery(queryStr);
			campusList = query.list();
			if (campusList != null) {
				campusList.add("All");
				Collections.sort(campusList);
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return campusList;
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

	public void sendBirthDayEmail() {
		EmailUtil emailUtill = getEmailInstance();
		if (emailUtill != null) {
			try {
				Session session = sessionFactory.getCurrentSession();
				Calendar cal = new GregorianCalendar();
				cal.add(Calendar.DAY_OF_YEAR, 1);
				String date = TpoUtil.getDateToString("MM-dd", cal.getTime());
				NativeQuery<String> query = session.createSQLQuery(
						"select r.email from registration r,personalinfo p where r.rollnumber = p.rollnumber"
								+ " and p.dob like '%" + date + "'");
				List<String> emailList = query.list();
				if (emailList != null && emailList.size() > 0) {
					StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear"));
					message.append("<br>");
					message.append("<br>");
					message.append(FbMessageUtil.getLabel("Wish_you_very_Happy_Birthday"));
					message.append("<br><br>");
					message.append(TpoUtil.getMesageString());
					emailUtill.postMail(emailList, FbMessageUtil.getLabel("Wish_you_very_Happy_Birthday"),
							message.toString(), TpoUtil.ADMIN_EMAIL, Message.RecipientType.BCC);
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
	}

	public void freeSessionData() {
		try {
			Session session = sessionFactory.getCurrentSession();
			NativeQuery<SessionData> query = session.createNativeQuery(
					"delete from sessionData where createdDate < " + Calendar.getInstance().getTime() + "");
			query.executeUpdate();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void sendFeeReminder() {
		EmailUtil emailUtill = getEmailInstance();
		if (emailUtill != null) {
			try {
				Session session = sessionFactory.getCurrentSession();
				Criteria criteria = session.createCriteria(StudentFeeDetails.class);
				criteria.add(Restrictions.eq("reminderOn", true));
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, 7);
				criteria.add(Restrictions.lt("dueOn", cal.getTime()));
				List<StudentFeeDetails> feeDetails = criteria.list();
				if (feeDetails != null) {
					List<String> emailList;
					for (StudentFeeDetails detail : feeDetails) {
						Registration registration = (Registration) session.get(Registration.class,
								detail.getRollNumber());
						if (registration != null) {
							if (registration.getEmailVarified()) {
								emailList = new ArrayList<String>(1);
								emailList.add(registration.getEmail());
								StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear"));
								message.append(registration.getFirstName());
								message.append(" ");
								message.append(registration.getLastName());
								message.append(",<br>");
								message.append("<br>");
								message.append(FbMessageUtil.getLabel("Reminder_for_fee_Payment"));
								message.append("<br>");
								message.append(FbMessageUtil.getLabel("Your_fee_was_due_on"));
								message.append(" ");
								message.append(detail.getDueOn());
								message.append("<br>");
								message.append(FbMessageUtil.getLabel("Please_pay_as_soon_as_possible"));
								message.append("<br><br>");
								message.append(TpoUtil.getMesageString());
								emailUtill.postMail(emailList, FbMessageUtil.getLabel("Reminder_for_fee_Payment"),
										message.toString(), TpoUtil.ADMIN_EMAIL, Message.RecipientType.BCC);
							}
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
	}

	/*
	 * public void sendBBBBirthDayEmail() { EmailUtil uddandaUtil =
	 * getEmailInstance(); if (uddandaUtil != null) { try { Calendar cal = new
	 * GregorianCalendar(); cal.add(Calendar.DAY_OF_YEAR, 1); String date =
	 * TpoUtil.getDateToString("dd/MM/", cal.getTime()); Session session =
	 * sessionFactory.getCurrentSession(); SQLQuery query = session
	 * .createSQLQuery("select email from bbb_userinfo where dateofbirth like '" +
	 * date + "%'"); List<String> emailList = query.list(); if (emailList != null &&
	 * !emailList.isEmpty()) { StringBuffer message = new
	 * StringBuffer(FbMessageUtil.getLabel("Dear")); message.append("<br>");
	 * message.append(BBBResourceUtil.getLabel("happyBirthday"));
	 * message.append("<br>");
	 * message.append(BBBResourceUtil.getLabel("Donate_Blood_Donate_Life"));
	 * message.append(TpoUtil.getMesageString()); uddandaUtil.postMail(emailList,
	 * FbMessageUtil.getLabel("Wish_you_very_Happy_Birthday"), message.toString(),
	 * TpoUtil.ADMIN_EMAIL, RecipientType.BCC); } } catch (HibernateException e) {
	 * logger.error(e.getMessage()); e.printStackTrace(); } catch
	 * (MessagingException e) { logger.error(e.getMessage()); e.printStackTrace(); }
	 * catch (Exception e) { logger.error(e.getMessage()); e.printStackTrace(); } }
	 * }
	 */

	private void sendReminder() {
		EmailUtil emailUtill = getEmailInstance();
		if (emailUtill != null) {
			try {
				Session session = sessionFactory.getCurrentSession();
				Calendar cal = new GregorianCalendar();
				cal.add(Calendar.DAY_OF_YEAR, 7);
				/*
				 * String date = TpoUtil.getDateToString(TpoUtil.DATE_FORMAT, cal.getTime());
				 */Criteria criteria = session.createCriteria(Logindetails.class)
						.createAlias("userdetails", "userdetails")
						.setProjection(Projections.property("userdetails.email"));
				criteria.add(Restrictions.eq("validTill", cal.getTime()));
				List<String> recipients = criteria.list();
				if (recipients != null && recipients.size() > 0) {
					StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear_Customer"));
					message.append("<br>");
					message.append(FbMessageUtil.getLabel("Your_login_details_will_be_expire_soon_in_one_week"));
					message.append(TpoUtil.getMesageString());
					emailUtill.postMail(recipients,
							FbMessageUtil.getLabel("Your_login_details_will_be_expire_soon_in_one_week"),
							message.toString(), TpoUtil.ADMIN_EMAIL, Message.RecipientType.BCC);
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

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void checkUserLicence() {
		EmailUtil emailUtill = getEmailInstance();
		if (emailUtill != null) {
			try {
				Session session = sessionFactory.getCurrentSession();
				Calendar cal = new GregorianCalendar();
				cal.add(Calendar.DAY_OF_YEAR, -1);
				/*
				 * String date = TpoUtil.getDateToString(TpoUtil.DATE_FORMAT, cal.getTime());
				 */Criteria criteria = session.createCriteria(Logindetails.class);
				criteria.add(Restrictions.le("validTill", cal.getTime()));
				criteria.add(Restrictions.eq("active", true));
				List<Logindetails> logindetailsList = criteria.list();
				if (logindetailsList != null && logindetailsList.size() > 0) {
					StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear_Customer"));
					message.append("<br>");
					List<String> recipients = new ArrayList<String>(logindetailsList.size());
					for (Logindetails logindetails : logindetailsList) {
						recipients.add(logindetails.getUserdetails().getEmail());
						if (!CCPConstant.SUPERUSER.equals(logindetails.getRole())) {
							Criteria criteria1 = session.createCriteria(Logindetails.class);
							criteria1.add(Restrictions.eq("createdBy", logindetails.getUserName()));
							List<Logindetails> list = criteria1.list();
							for (Logindetails logindetailsObj : list) {
								logindetailsObj.setActive(false);
								session.update(logindetailsObj);
							}
						}
						logindetails.setActive(false);
						session.update(logindetails);
					}
					message.append(FbMessageUtil.getLabel("Your_login_details_has_been_expired_Please_contact_admin"));
					message.append(TpoUtil.getMesageString());
					emailUtill.postMail(recipients,
							FbMessageUtil.getLabel("Your_login_details_has_been_expired_Please_contact_admin"),
							message.toString(), TpoUtil.ADMIN_EMAIL, Message.RecipientType.BCC);
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
		sendReminder();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Boolean verifyEmail(String rollnumber, String password) {
		Boolean flag = false;
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Registration.class);
			criteria.add(Restrictions.eq("rollnumber", rollnumber));
			criteria.add(Restrictions.eq("password", password));
			Registration registration = (Registration) criteria.uniqueResult();
			if (registration != null) {
				registration.setEmailVarified(true);
				session.update(registration);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Email_verified_successfully"));
				flag = true;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return flag;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Boolean verifyUserEmail(String userName, String password) {
		Boolean flag = false;
		try {

			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Logindetails.class);
			criteria.add(Restrictions.eq("userName", userName));
			criteria.add(Restrictions.eq("password", password));
			Logindetails loginDetails = (Logindetails) criteria.uniqueResult();
			if (loginDetails != null) {
				Userdetails userdetails = loginDetails.getUserdetails();
				userdetails.setEmailVarified(true);
				session.update(userdetails);
				flag = true;
			} else {
				flag = false;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return flag;
	}

	public void setAdminMessageList(List<String> adminMessageList) {
		this.adminMessageList = adminMessageList;
	}

	private List<String> getDonorsEmailList(String bloodGroup) {
		List<String> list = null;
		try {
			EntityManager entityManager = sessionFactory.createEntityManager();

			String queryStr = "select email from Userinfo where bloodGroup='" + bloodGroup + "' and email != 'NA'";

			list = entityManager.createQuery(queryStr, String.class).getResultList();

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	private List<String> getDonorsEmailList() {
		List<String> list = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			NativeQuery<String> query;
			String queryStr = "select email from bbb_userinfo where email != 'NA';";

			query = session.createSQLQuery(queryStr);
			list = query.list();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean changeStudentPassword(String userName, String password, String newPass) {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Registration.class);
			criteria.add(Restrictions.eq("rollnumber", userName));
			criteria.add(Restrictions.eq("password", password));
			Registration registration = (Registration) criteria.uniqueResult();
			if (registration != null) {
				registration.setPassword(newPass);
				session.update(registration);
				return true;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean changeAdminPassword(String userName, String password, String newPass) {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Logindetails.class);
			criteria.add(Restrictions.eq("userName", userName));
			criteria.add(Restrictions.eq("password", password));
			Logindetails loginDetails = (Logindetails) criteria.uniqueResult();
			if (loginDetails != null) {
				loginDetails.setPassword(newPass);
				session.update(loginDetails);
				return true;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean verifyMobileNumber(String enrollmetNo) {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Contactinfo.class);
			criteria.add(Restrictions.eq("rollnumber", enrollmetNo));
			Contactinfo contactinfo = (Contactinfo) criteria.uniqueResult();
			if (contactinfo != null) {
				contactinfo.setNumberVerified(true);
				session.update(contactinfo);
				return true;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public BigInteger getQuestionCount(Exam exam) {

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

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public List<HallTicketConnect> getHallTicketList(String rollNumber) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(HallTicketConnect.class);
		criteria.add(Restrictions.eq("id.rollnumber", rollNumber));
		List<HallTicketConnect> hallTicketList = criteria.list();
		return hallTicketList;
	}

	public List<HallTicket> getOppeningList(String userName) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(HallTicket.class);
		criteria.add(Restrictions.eq("userName", userName));
		/*
		 * Date date
		 * =TpoUtil.getFormatedDateInyyyyMMddHHMMss(Calendar.getInstance().getTime());
		 * criteria.add(Restrictions.ge("id.hallTicket.lastDateToApply", date));
		 */
		List<HallTicket> hallTicketList = criteria.list();
		return hallTicketList;
	}

	public List<HallTicket> aplliedListForCompany(String companyID) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(HallTicket.class);
		criteria.add(Restrictions.eq("companyID", Integer.parseInt(companyID)));
		/*
		 * Date date
		 * =TpoUtil.getFormatedDateInyyyyMMddHHMMss(Calendar.getInstance().getTime());
		 * criteria.add(Restrictions.ge("id.hallTicket.lastDateToApply", date));
		 */
		List<HallTicket> hallTicketList = criteria.list();
		return hallTicketList;
	}

	public List<Company> getCompanyListForAdmin(String userName) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Company.class);
		criteria.add(Restrictions.eq("createdBy", userName));
		List<Company> companies = criteria.list();
		return companies;
	}

	public List<HallTicketConnect> getHallTicketListByID(String hallticketId) {
		Session session = sessionFactory.getCurrentSession();
		String mainStr = "SELECT * FROM hallticketconnect where hallTicketId = " + hallticketId + " and isApplied=true";
		NativeQuery<HallTicketConnect> query = session.createSQLQuery(mainStr);
		query.addEntity(HallTicketConnect.class);
		List<HallTicketConnect> hallTicketList = query.list();
		return hallTicketList;
	}

	public BigInteger getHallTicketAppliedCount(int hallticketId) {
		Session session = sessionFactory.getCurrentSession();
		String mainStr = "SELECT count(isApplied) FROM hallticketconnect where hallTicketId = " + hallticketId
				+ " and isApplied=true";
		NativeQuery<?> query = session.createSQLQuery(mainStr);
		BigInteger count = (BigInteger) query.uniqueResult();
		return count;
	}

	public BigInteger getHallTicketApprovedCount(int hallticketId) {
		Session session = sessionFactory.getCurrentSession();
		String mainStr = "SELECT count(isApproved) FROM hallticketconnect where hallTicketId = " + hallticketId
				+ " and isApproved=true";
		NativeQuery<?> query = session.createSQLQuery(mainStr);
		BigInteger count = (BigInteger) query.uniqueResult();
		return count;
	}

	public BigInteger getHallTicketArrivedCount(int hallticketId) {
		Session session = sessionFactory.getCurrentSession();
		String mainStr = "SELECT count(arrived) FROM hallticketconnect where hallTicketId = " + hallticketId
				+ " and arrived=true";
		NativeQuery<?> query = session.createSQLQuery(mainStr);
		BigInteger count = (BigInteger) query.uniqueResult();
		return count;
	}

	public BigInteger getHallTicketShortListedCount(int hallticketId) {
		Session session = sessionFactory.getCurrentSession();
		String mainStr = "SELECT count(hallTicketId) FROM hallticketconnect where hallTicketId = " + hallticketId;
		NativeQuery<?> query = session.createSQLQuery(mainStr);
		BigInteger count = (BigInteger) query.uniqueResult();
		return count;
	}

	public List<HallTicket> getOpeningList(String enrollmentNo) {
		List<HallTicket> list = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(HallTicket.class);
			NativeQuery<?> query = getCreatedBy(enrollmentNo, session);
			String createdBy = (String) query.uniqueResult();
			List<String> createdByList = new ArrayList<String>(2);
			createdByList.add(createdBy);
			String supperUser = SystemUtil.getLabel("supperUser");
			createdByList.add(supperUser);
			criteria.add(Restrictions.in("userName", createdByList));
			criteria.add(Restrictions.eq("isActive", true));
			Date date = TpoUtil.getFormatedDateInyyyyMMddHHMMss(Calendar.getInstance().getTime());
			criteria.add(Restrictions.ge("lastDateToApply", date));
			list = criteria.list();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	public char validateStudentLogin(String enrollmentNo, String encryptedPassword) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Registration.class).setProjection(Projections.property("status"));
		criteria.add(Restrictions.eq("rollnumber", enrollmentNo));
		criteria.add(Restrictions.eq("password", encryptedPassword));
		Boolean status = (Boolean) criteria.uniqueResult();
		if (status != null) {
			if (!status) {
				return 'B';
			}
			return 'T';
		}
		return 'F';
	}

	public String validateAdminLogin(String userName, String encryptedPassword) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Logindetails.class).setProjection(Projections.property("role"));
		criteria.add(Restrictions.eq("userName", userName));
		criteria.add(Restrictions.eq("password", encryptedPassword));
		String role = (String) criteria.uniqueResult();
		if (role != null) {
			return role;
		}
		return null;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void changeHallTicketStatus(String enrollmentNo, Integer hallticketid, boolean isApplied) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(HallTicket.class);
		criteria.add(Restrictions.eq("hallTicketId", hallticketid));
		HallTicket hallTicket = (HallTicket) criteria.uniqueResult();
		if (hallTicket != null) {
			HallTicketConnectID connectID = new HallTicketConnectID();
			connectID.setHallTicket(hallTicket);
			connectID.setRollnumber(enrollmentNo);
			criteria = session.createCriteria(HallTicketConnect.class);
			criteria.add(Restrictions.eq("id", connectID));
			HallTicketConnect hallTicketConnect = (HallTicketConnect) criteria.uniqueResult();
			if (hallTicketConnect != null) {
				hallTicketConnect.setIsApplied(isApplied);
				hallTicketConnect.setAppliedOn(Calendar.getInstance().getTime());
				if (!isApplied) {
					hallTicketConnect.setIsApproved(false);
					hallTicketConnect.setAppliedOn(null);
					hallTicketConnect.setApprovedOn(null);
				}
				session.update(hallTicketConnect);
			}
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String verifyHallTicketStatus(String enrollmentNo, Integer hallticketid, String digitalSignature) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(HallTicket.class);
		criteria.add(Restrictions.eq("hallTicketId", hallticketid));
		criteria.add(Restrictions.eq("digitalSignature", digitalSignature));
		HallTicket hallTicket = (HallTicket) criteria.uniqueResult();
		if (hallTicket != null) {
			HallTicketConnectID connectID = new HallTicketConnectID();
			connectID.setHallTicket(hallTicket);
			connectID.setRollnumber(enrollmentNo);
			criteria = session.createCriteria(HallTicketConnect.class);
			criteria.add(Restrictions.eq("id", connectID));
			HallTicketConnect hallTicketConnect = (HallTicketConnect) criteria.uniqueResult();
			if (hallTicketConnect != null
					&& (hallTicketConnect.getArrived() == null || !hallTicketConnect.getArrived())) {
				hallTicketConnect.setArrived(true);
				return "V";
			} else {
				return "A";
			}
		} else {
			return "E";
		}
	}

	public HallTicket getHallTicket(Integer hallTicketId) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(HallTicket.class);
		criteria.add(Restrictions.eq("hallTicketId", hallTicketId));
		return (HallTicket) criteria.uniqueResult();
	}

	public Registration getRegistration(String enrollmentNo) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Registration.class);
		criteria.add(Restrictions.eq("rollnumber", enrollmentNo));
		return (Registration) criteria.uniqueResult();
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
		return userNames;
	}

	public List<String> getAllCollegeList(String userName, String role) {
		String sub_collegeName = null;
		if (CCPConstant.COMPANY.equals(role)) {
			sub_collegeName = "select distinct cg.collegeName from collegegroup cg where cg.status='A' and cg.userName='"
					+ userName + "'";
		} else {
			sub_collegeName = "select distinct t.collegeName from (select distinct c.collegeName from college c where c.userName='"
					+ userName
					+ "' UNION ALL select distinct cg.collegeName from college c,collegegroup cg where cg.userName=c.userName and cg.status='A' and c.userName='"
					+ userName + "') as t";
		}
		Session session = sessionFactory.getCurrentSession();
		NativeQuery<String> query = session.createSQLQuery(sub_collegeName);
		List<String> collegeList = query.getResultList();
		return collegeList;
	}

	public BroadCastMessage getCurrentBroadCastMessage(String userName, boolean dateFlag) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(BroadCastMessage.class);
		if (userName != null)
			criteria.add(Restrictions.eq("userName", userName));
		else {
			criteria.add(Restrictions.eq("userName", AdminUser.getUser().getUserName()));
		}
		if (dateFlag) {
			Calendar calendar = Calendar.getInstance();
			Date date = TpoUtil.getFormatedDateInyyyyMMddHHMMss(calendar.getTime());
			criteria.add(Restrictions.le("validFrom", date));
			criteria.add(Restrictions.ge("validTill", date));
		}
		BroadCastMessage broadCastMessage = (BroadCastMessage) criteria.uniqueResult();
		return broadCastMessage;
	}

	public List<Notice> getNoticForStudent(String enNo, boolean studentSpecific) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Notice.class);
		NativeQuery<?> query = getCreatedBy(enNo, session);
		String createdBy = (String) query.uniqueResult();
		List<String> createdByList = new ArrayList<String>(3);
		if (studentSpecific) {
			createdByList.add(enNo);
		} else {
			createdByList.add(createdBy);
			String supperUser = SystemUtil.getLabel("supperUser");
			createdByList.add(supperUser);
			createdByList.add(enNo);
		}
		criteria.add(Restrictions.in("createdBy", createdByList));
		criteria.add(Restrictions.eq("active", true));
		List<Notice> nList = criteria.list();
		return nList;
	}

	public List<DocumentList> getDocumentListForStudent(String enNo) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(DocumentList.class);
		criteria.add(Restrictions.eq("documentID.rollnumber", enNo));
		List<DocumentList> nList = criteria.list();
		return nList;
	}

	public DocumentList getDocumentForStudent(String enNo, String documentName) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(DocumentList.class);
		criteria.add(Restrictions.eq("documentID.rollnumber", enNo));
		criteria.add(Restrictions.eq("documentID.documentName", documentName));
		DocumentList nList = (DocumentList) criteria.uniqueResult();
		return nList;
	}

	public List<Result> getResultForStudent(String enNo) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Result.class);
		/*
		 * SQLQuery query = getCreatedBy(enNo, session); String createdBy =
		 * (String)query.uniqueResult(); criteria.add(Restrictions.eq("createdBy",
		 * createdBy));
		 */criteria.add(Restrictions.eq("id.loginname", enNo));
		List<Result> resultList = criteria.list();
		List<Result> resultListNew = new ArrayList<Result>();

		Set<String> examNames = new HashSet<String>();
		for (Result result : resultList) {
			examNames.add(result.getTestName());
		}
		criteria = session.createCriteria(Exam.class);
		criteria.add(Restrictions.in("testname", examNames));
		List<Exam> examList = criteria.list();
		File file;
		for (Result result : resultList) {
			for (Exam exam : examList) {
				if (exam.getTestname().equals(result.getTestName())) {
					if (exam.getShowResult()) {
						resultListNew.add(result);
					}
					updateCertStatus(result);
					break;

				}
			}

		}
		return resultListNew;
	}

	public List<StudentFeeDetails> getStudentFeeList(String enNo) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(StudentFeeDetails.class);
		criteria.add(Restrictions.eq("rollNumber", enNo));
		criteria.add(Restrictions.eq("reminderOn", true));
		criteria.add(Restrictions.isNotNull("amountDue"));
		criteria.add(Restrictions.isNotNull("dueOn"));
		List<StudentFeeDetails> resultList = criteria.list();
		return resultList;
	}

	public void updateCertStatus(Result result) {
		if (!CCPConstant.Disqualified.equals(result.getResult())) {
			String certFileName = "Certificate_" + result.getTestName() + "_" + result.getId().getLoginname() + "_"
					+ result.getTotalnumbers() + ".pdf";
			if (fileUploadUtility.isFileExist(getFileServiceUrl() + "/fileExist", certFileName,
					IMAGECONS.student.toString() + result.getId().getLoginname() + "/"
							+ IMAGECONS.certificate.toString())) {
				result.setCertificateAvialable(true);
			} else {
				result.setCertificateAvialable(false);
			}
		}
	}

	private NativeQuery<?> getCreatedBy(String enNo, Session session) {
		NativeQuery<?> query = session.createSQLQuery(
				"select userName from college where collegeName = '" + getRegistration(enNo).getCollegeName() + "'");
		return query;
	}

	public boolean sendStudentPassword(String enrollmentNo, String email, String basePath) {
		try {
			EmailUtil emailUtill = getEmailInstance();
			if (emailUtill != null) {
				Session session = sessionFactory.getCurrentSession();
				Criteria criteria = session.createCriteria(Registration.class);
				criteria.add(Restrictions.eq("rollnumber", enrollmentNo));
				criteria.add(Restrictions.eq("email", email));
				Registration registration = (Registration) criteria.uniqueResult();
				if (registration != null) {
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
					message.append("<br><a href='" + basePath + "xhtml/resetStudentPassword.faces?pnsgffmffbhvgkbf="
							+ registration.getPassword() + "&dfdnmfbnndfn=" + registration.getRollnumber() + "'>"
							+ FbMessageUtil.getLabel("Please_click_on_this_link_to_reset_your_password") + "</a><br>");
					message.append(TpoUtil.getMesageString());
					emailUtill.postMail(recipients, subject.toString(), message.toString(), TpoUtil.ADMIN_EMAIL,
							Message.RecipientType.TO);
					UIBackingBean
							.setSuccessMessage(FbMessageUtil.getLabel("Password_reset_Email_has_been_sent_on", email));
					return true;
				}
			}
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public boolean sendAdminPassword(String userName, String email, String basePath) {
		try {
			EmailUtil emailUtill = getEmailInstance();
			if (emailUtill != null) {
				Session session = sessionFactory.getCurrentSession();
				Criteria criteria = session.createCriteria(Logindetails.class);
				criteria.createAlias("userdetails", "userdetails");
				criteria.add(Restrictions.eq("userName", userName));
				criteria.add(Restrictions.eq("userdetails.email", email));
				Logindetails userInfo = (Logindetails) criteria.uniqueResult();
				if (userInfo != null) {
					Userdetails userdetails = userInfo.getUserdetails();
					List<String> recipients = new ArrayList<String>(1);
					recipients.add(userdetails.getEmail());
					StringBuffer subject = new StringBuffer("Hi ");
					subject.append(userdetails.getUserName());
					subject.append(",Password forgot link");
					StringBuffer message = new StringBuffer("Hi ");
					message.append(userdetails.getUserName());
					message.append(",<br>");
					message.append(FbMessageUtil.getLabel("reset_your_password"));
					message.append(" <br><br><font color=green size=5>");
					message.append(FbMessageUtil.getLabel("Your_User_Name_is"));
					message.append(" " + userdetails.getUserName());
					message.append("</font>");
					message.append("<br><br><a href='" + basePath + "xhtml/resetUserPassword.faces?pnsgffmffbhvgkbf="
							+ userInfo.getPassword() + "&dfdnmfbnndfn=" + userdetails.getUserName() + "'>"
							+ FbMessageUtil.getLabel("Please_click_on_this_link_to_reset_your_password") + "</a><br>");
					message.append(TpoUtil.getMesageString());
					emailUtill.postMail(recipients, subject.toString(), message.toString(), TpoUtil.ADMIN_EMAIL,
							Message.RecipientType.TO);
					return true;
				}
			}
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public List<BigInteger> getCountList(int hallticketID) {
		List<BigInteger> list = new ArrayList<BigInteger>(4);
		Session session = sessionFactory.getCurrentSession();
		String queryStr = "SELECT count(hallTicketId) FROM hallticketconnect where hallTicketId = " + hallticketID + "";
		NativeQuery<?> query = session.createSQLQuery(queryStr);
		list.add((BigInteger) query.uniqueResult());
		queryStr = "SELECT count(hallTicketId) FROM hallticketconnect where hallTicketId = " + hallticketID
				+ " and isApplied=1";
		query = session.createSQLQuery(queryStr);
		list.add((BigInteger) query.uniqueResult());
		queryStr = "SELECT count(hallTicketId) FROM hallticketconnect where hallTicketId = " + hallticketID
				+ " and isApproved=1";
		query = session.createSQLQuery(queryStr);
		list.add((BigInteger) query.uniqueResult());
		queryStr = "SELECT count(hallTicketId) FROM hallticketconnect where hallTicketId = " + hallticketID
				+ " and arrived=1";
		query = session.createSQLQuery(queryStr);
		list.add((BigInteger) query.uniqueResult());
		return list;
	}

	public List<String> getCommonData(String name) {
		EmailUtil emailUtill = getEmailInstance();
		if (emailUtill != null) {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(CommonData.class).setProjection(Projections.property("value"));
			criteria.add(Restrictions.eq("name", name).ignoreCase());
			List<String> list = criteria.list();
			if (list != null) {
				return list;
			} else {
				return null;
			}
		}
		return null;
	}

	public byte[] getStudentProfilePic(String enrollmentNo) {
		byte[] buf = null;
		String fileName = enrollmentNo + ".png";
		if (fileName != null) {
			buf = fileUploadUtility.downloadFileWithParam(getImageServiceUrl() + "/downloadImage", fileName,
					IMAGECONS.student.toString() + enrollmentNo);
		}
		return buf;
	}

	public byte[] getStudentProfilePic(Registration registration) {
		return getStudentProfilePic(registration.getRollnumber());
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean uploadStudentProfilePic(String envNO, byte[] image) {
		try {
			if (image != null && envNO != null) {
				fileUploadUtility.uploadFileWithByteArrayWithExt(getImageServiceUrl() + "/uploadWithExt",
						envNO + ".png", image, IMAGECONS.student.toString() + envNO);
				return true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean uploadStudentProfilePic(Registration registration, byte[] image) {
		try {
			if (image != null && registration != null) {
				String response = fileUploadUtility.uploadFileWithByteArrayWithExt(
						getImageServiceUrl() + "/uploadWithExt", registration.getRollnumber() + ".png", image,
						IMAGECONS.student.toString() + registration.getRollnumber());
				if (response != null && "File Uploaded".equals(response)) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean uploadStudentResume(Registration registration, byte[] resume, String fileName, String contentType) {
		try {
			if (registration != null && fileName != null && resume != null) {
				Personalinfo personalinfo = registration.getPersonalinfo();
				if (personalinfo.getResume() != null && !"".equals(personalinfo.getResume())) {
					fileUploadUtility
							.deleteFileWithParam(
									getFileServiceUrl() + "/delete", IMAGECONS.student.toString()
											+ registration.getRollnumber() + "/" + IMAGECONS.resume.toString(),
									personalinfo.getResume());
				}
				personalinfo.setResume(fileName);
				personalinfo.setResumeType(contentType);
				fileUploadUtility.uploadFileWithByteArrayWithExt(getFileServiceUrl() + "/upload", fileName, resume,
						IMAGECONS.student.toString() + registration.getRollnumber() + "/"
								+ IMAGECONS.resume.toString());
				Session session = sessionFactory.getCurrentSession();
				session.update(personalinfo);
				return true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public String getUserNameByCollegeName(String collegeName) {
		Session session = sessionFactory.getCurrentSession();
		NativeQuery<?> collegeQ = session
				.createSQLQuery("SELECT userName FROM college  where CollegeName = '" + collegeName + "'");
		String userName = (String) collegeQ.uniqueResult();
		return userName;
	}

	public Company getCompnay(Integer companyID) {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Company.class);
			criteria.add(Restrictions.eq("companyID", companyID));
			Company company = (Company) criteria.uniqueResult();
			if (company != null) {
				return company;
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

	public List<Company> getCompanyList(String companyID) {
		try {
			String[] s = companyID.split(",");
			List<Integer> ids = new ArrayList<Integer>(s.length);
			for (String id : s) {
				ids.add(Integer.valueOf(id));
			}
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Company.class);
			criteria.add(Restrictions.in("companyID", ids));
			return criteria.list();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public byte[] getCompnayLogo(Integer companyID) {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Company.class).setProjection(Projections.property("logo"));
			criteria.add(Restrictions.eq("companyID", companyID));
			Blob logo = (Blob) criteria.uniqueResult();
			if (logo != null) {
				return TpoUtil.convertInputStreamToBytesArray(logo.getBinaryStream());
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

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateTheme(String enrollmentNo, String theme) {
		Registration registration = getRegistration(enrollmentNo);
		if (registration != null) {
			registration.setTheme(theme);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateColor(String enrollmentNo, Integer color) {
		Registration registration = getRegistration(enrollmentNo);
		if (registration != null) {
			registration.setColor(color);
		}
	}

	public Userdetails getUserInfo(String userName) {
		Userdetails userInfo = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Userdetails.class);
			criteria.add(Restrictions.eq("username", userName));
			userInfo = (Userdetails) criteria.uniqueResult();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return userInfo;
	}

	public List<HallTicket> getOpeningList() {
		if (oppeninglist == null) {
			try {
				Session session = sessionFactory.getCurrentSession();
				Criteria criteria = session.createCriteria(HallTicket.class);
				criteria.add(Restrictions.eq("isActive", true));
				Date date = TpoUtil.getFormatedDateInyyyyMMddHHMMss(Calendar.getInstance().getTime());
				criteria.add(Restrictions.ge("lastDateToApply", date));
				oppeninglist = criteria.list();
				if (oppeninglist != null) {
					for (HallTicket hallTicket : oppeninglist) {
						Company company = getCompnay(hallTicket.getCompanyID());
						hallTicket.setCompany(company);
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
		return oppeninglist;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addBookMark(String name) {
		try {
			Bookmark bookmark = null;
			Session session = sessionFactory.getCurrentSession();
			BookmarkID id = new BookmarkID();
			id.setUserName(AdminUser.getUser().getUserName());
			id.setBookMark(name);
			Criteria criteria = session.createCriteria(Bookmark.class);
			criteria.add(Restrictions.eq("id", id));
			bookmark = (Bookmark) criteria.uniqueResult();
			if (bookmark == null) {
				bookmark = new Bookmark();
				bookmark.setId(id);
				session.save(bookmark);
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
	public void deleteBookMark(String name) {
		try {
			Bookmark bookmark = null;
			Session session = sessionFactory.getCurrentSession();
			BookmarkID id = new BookmarkID();
			id.setUserName(AdminUser.getUser().getUserName());
			id.setBookMark(name);
			Criteria criteria = session.createCriteria(Bookmark.class);
			criteria.add(Restrictions.eq("id", id));
			bookmark = (Bookmark) criteria.uniqueResult();
			if (bookmark != null) {
				session.delete(bookmark);
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public Bookmark getBookmark(String name) {
		try {
			Bookmark bookmark = null;
			Session session = sessionFactory.getCurrentSession();
			BookmarkID id = new BookmarkID();
			id.setUserName(AdminUser.getUser().getUserName());
			id.setBookMark(name);
			Criteria criteria = session.createCriteria(Bookmark.class);
			criteria.add(Restrictions.eq("id", id));
			return bookmark = (Bookmark) criteria.uniqueResult();

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public List<Bookmark> getBookmarkList() {
		List<Bookmark> list = null;
		try {
			Bookmark bookmark = null;
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Bookmark.class);
			criteria.add(Restrictions.eq("id.userName", AdminUser.getUser().getUserName()));
			list = criteria.list();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	public Integer getStudentCount() {
		int countInt = 10000;
		Session session = sessionFactory.getCurrentSession();
		NativeQuery<?> query = session.createSQLQuery("select count(rollnumber) from registration");
		BigInteger count = (BigInteger) query.uniqueResult();
		if (count != null) {
			countInt += count.intValue();
		}
		return countInt;
	}

	public Integer getStudentSelection() {
		int countInt = 0;
		Session session = sessionFactory.getCurrentSession();
		String queryStr = "select count(r.rollnumber) from personalinfo pi,registration r where pi.rollnumber = r.rollnumber and pi.companyName !=''";
		NativeQuery<?> query = session.createSQLQuery(queryStr);
		BigInteger count = (BigInteger) query.uniqueResult();
		if (count != null) {
			countInt += count.intValue();
		}
		return countInt;
	}

	public Integer getExamCount() {
		int countInt = 500;
		Session session = sessionFactory.getCurrentSession();
		NativeQuery<?> query = session.createSQLQuery("select count(testname) from exam");
		BigInteger count = (BigInteger) query.uniqueResult();
		if (count != null) {
			countInt += count.intValue();
		}
		return countInt;
	}

	public Integer getCompanyCount() {
		int countInt = 100;
		Session session = sessionFactory.getCurrentSession();
		NativeQuery<?> query = session.createSQLQuery("select count(companyID) from company");
		BigInteger count = (BigInteger) query.uniqueResult();
		if (count != null) {
			countInt += count.intValue();
		}
		return countInt;
	}

	public Integer getCollegeCount() {
		int countInt = 200;
		Session session = sessionFactory.getCurrentSession();
		NativeQuery<?> query = session.createSQLQuery("select count(collegeName) from college");
		BigInteger count = (BigInteger) query.uniqueResult();
		if (count != null) {
			countInt += count.intValue();
		}
		return countInt;
	}

	public void setCompany(AjaxActionEvent event) {
		try {
			if (event != null) {
				Session session = null;
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					session = sessionFactory.getCurrentSession();
					Criteria criteria = session.createCriteria(Company.class);
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					String companyNames = (String) parameter.getValue();
					String[] cmpnyNames = companyNames.split(",");
					criteria.add(Restrictions.in("companyname", cmpnyNames));
					cList = criteria.list();
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

	public void setCompanyById(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					Integer companyID = (Integer) parameter.getValue();
					if (companyID != null) {
						cList = new ArrayList<>(1);
						cList.add(getCompnay(companyID));
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

	public void setCompanyByCompany(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					Company company = (Company) parameter.getValue();
					cList = new ArrayList<Company>(1);
					cList.add(company);
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

	public void setCompanyByMultipleCompanyID(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					String companyId = (String) parameter.getValue();
					cList = getCompanyList(companyId);
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

	public List<Company> getcList() {
		return cList;
	}

	public void setcList(List<Company> cList) {
		this.cList = cList;
	}

	public List<Notice> getNoticeList() {
		if (noticeList == null) {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Notice.class);
			criteria.add(Restrictions.eq("active", true));
			criteria.add(Restrictions.isNull("studentSpecific"));
			criteria.addOrder(Order.asc("expiryDate"));
			criteria.setMaxResults(10);
			int index = 200;
			noticeList = criteria.list();
			for (Notice notice : noticeList) {
				notice.setIndex(index);
				index += 100;
			}
		}
		return noticeList;
	}

	public boolean isUserExist(String userName) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Logindetails.class).setProjection(Projections.property("userName"));
		criteria.add(Restrictions.eq("userName", userName));
		String userNameObj = (String) criteria.uniqueResult();
		if (userNameObj != null) {
			userNameObj = null;
			return true;
		} else {
			return false;
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Boolean sendEmail(boolean status, String rollNumber, Integer hallTicketId) throws MessagingException {
		EmailUtil emailUtill = getEmailInstance();
		StringBuffer emailMessage;
		String subject;
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(HallTicketConnect.class);
		criteria.add(Restrictions.eq("id.rollnumber", rollNumber));
		criteria.add(Restrictions.eq("id.hallTicket.hallTicketId", hallTicketId));
		HallTicketConnect hallTicketConnect = (HallTicketConnect) criteria.uniqueResult();
		if (hallTicketConnect != null) {
			subject = "Your request for " + hallTicketConnect.getId().getHallTicket().getCompanyName();
			Registration registration = session.get(Registration.class, hallTicketConnect.getId().getRollnumber());
			if (registration != null) {
				emailMessage = new StringBuffer("Dear ").append(registration.getFirstName()).append(" ")
						.append(registration.getLastName());
				emailMessage.append(",<br>").append(subject);
				emailMessage.append(" has been ")
						.append(status ? "<font color=green>Approved<font/>" : "<font color=red>Rejected<font/>");
				hallTicketConnect.setIsApproved(status);
				if (status) {
					hallTicketConnect.setApprovedOn(Calendar.getInstance().getTime());
				} else {
					hallTicketConnect.setApprovedOn(null);
				}
				session.update(hallTicketConnect);
				if (emailUtill != null) {
					List<String> address = new ArrayList<String>(1);
					address.add(registration.getEmail());
					emailMessage.append(TpoUtil.getMesageString());
					emailUtill.postMail(address, subject, emailMessage.toString(), TpoUtil.ADMIN_EMAIL,
							Message.RecipientType.TO);
				}
				return true;
			}
		}
		return false;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean deleteCompany(String companyId) {
		Session session = sessionFactory.getCurrentSession();
		Company company = (Company) session.get(Company.class, Integer.valueOf(companyId));
		if (company != null) {
			session.delete(company);
			return true;
		} else {
			return false;
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean deleteOpening(String hallticketId) {
		Session session = sessionFactory.getCurrentSession();
		HallTicket hall = (HallTicket) session.get(HallTicket.class, Integer.valueOf(hallticketId));
		if (hall != null) {
			session.delete(hall);
			return true;
		} else {
			return false;
		}
	}

	public List<CustomerReview> getReviewList() {
		if (customerReviewList == null) {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(CustomerReview.class);
			criteria.add(Restrictions.ge("starRating", 4));
			customerReviewList = criteria.list();
		}
		return customerReviewList;
	}

	public List<Registration> getSelectedStudenets() {
		if (selectedStudentList == null) {
			try {
				Session session = sessionFactory.getCurrentSession();
				Criteria criteria = session.createCriteria(Registration.class).createAlias("personalinfo", "p");
				criteria.add(Restrictions.ne("p.companyName", ""));
				criteria.addOrder(Order.desc("lastUpdated"));
				selectedStudentList = criteria.list();
			} catch (HibernateException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return selectedStudentList;
	}

	public byte[] getCompanyePic(String companyId) {
		byte[] buf = null;
		companyId = companyId + ".png";
		if (companyId != null) {
			buf = fileUploadUtility.downloadFileWithParam(getImageServiceUrl() + "/downloadImage", companyId,
					IMAGECONS.company.toString());
		}
		return buf;
	}

	public byte[] getClientLogo(String userName) {
		byte[] buf = null;
		userName = userName + ".png";
		if (userName != null) {
			buf = fileUploadUtility.downloadFileWithParam(getImageServiceUrl() + "/downloadImage", userName,
					IMAGECONS.userlogo.toString());
		}
		return buf;
	}

	public byte[] getUserProfilePic(String userName) {
		byte[] buf = null;
		userName = userName + ".png";
		if (userName != null) {
			buf = fileUploadUtility.downloadFileWithParam(getImageServiceUrl() + "/downloadImage", userName,
					IMAGECONS.userprofilepics.toString());
		}
		return buf;
	}

	public String getEnvirnment() {
		if (envirnment == null) {
			envirnment = SystemUtil.getLabel("envirnment");
		}
		return envirnment;
	}

}
