package tpo.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.mail.Message;
import javax.mail.MessagingException;

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

import tpo.admin.backup.CollegeConnectBackUp;
import tpo.admin.beans.AdminUser;
import tpo.dao.CommonDBBean;
import tpo.email.EmailUtil;
import tpo.hibernate.Logindetails;
import tpo.hibernate.Userdetails;
import tpo.hibernate.annotation.BroadCastMessage;
import tpo.util.CCPConstant;
import tpo.util.Encryption;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

@Repository("CCPLoginBean")
@Transactional(readOnly = true)
@Scope("request")
public class CCPLoginBean extends Parent {

	@Autowired
	private SessionFactory sessionFactory;

	private Logger logger = LoggerFactory.getLogger(CCPLoginBean.class);

	private String userName = "";
	private String enrollmentNo;
	private String password;
	private String email;
	private Logindetails logindetails;
	private boolean showClient;
	private boolean showStudent;
	private Object studentCount;

	@Autowired
	private CommonDBBean commonDBBean;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void validateLogin() {
		try {
			if (TpoUtil.isApplicationExpired()) {
				return;
			}
			String encryptedPassword = Encryption.getEncryptedString(password);
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Logindetails.class);
			criteria.add(Restrictions.eq("userName", userName));
			criteria.add(Restrictions.eq("password", encryptedPassword));
			Logindetails userinfo = (Logindetails) criteria.uniqueResult();
			if (userinfo != null) {
				setUserProfile(session, userinfo);
			} else {
				criteria = session.createCriteria(Logindetails.class);
				criteria.add(Restrictions.eq("userName", userName));
				userinfo = (Logindetails) criteria.uniqueResult();
				if (userinfo != null) {
					if (userinfo.getActive()) {
						if (userinfo.getLoginAttempt() >= 4) {
							userinfo.setActive(false);
						} else {
							userinfo.setLoginAttempt(userinfo.getLoginAttempt() + 1);
						}
						session.saveOrUpdate(userinfo);
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error8));
					} else {
						UIBackingBean.setErrorMessage(
								FbMessageUtil.getLabel("Your_account_is_not_active_Please_contact_admin"));
					}
				} else {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error8));
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

	public void setUserProfile(Session session, Logindetails userinfo) {
		if (userinfo.getActive()) {
			AdminUser user = AdminUser.getUser();
			Date date = new Date();
			Date expDate = userinfo.getValidTill();
			if (date.after(userinfo.getValidTill())) {
				String str[] = new String[1];
				str[0] = TpoUtil.getDateToStringInddmmyyyy(userinfo.getValidTill());
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("user_validity_expired", str));
				return;
			} else {
				setExpiryStr(user, date, expDate);
			}
			user.setUserName(userinfo.getUserName());
			user.setFullName(userinfo.getUserdetails().getFirstName() + " " + userinfo.getUserdetails().getLastName());
			user.setUserList(TpoUtil.getUserList(user.getUserName()));
			StringBuffer lastLogin = new StringBuffer("");
			lastLogin.append(TpoUtil.getDateToString(CCPConstant.DATE_FORMAT_ddMMyyyyhhmm_24, userinfo.getLastLogin()))
					.append(" ").append(FbMessageUtil.getLabel("IST"));
			user.setLastLogin(lastLogin.toString());
			user.setEtsLogin(false);
			user.setParent(userinfo.getCreatedBy());
			user.setEmail(userinfo.getUserdetails().getEmail());
			if (userinfo.getRole().equals("A")) {
				user.setRole(CCPConstant.ADMIN);
			} else if (userinfo.getRole().equals("U")) {
				user.setUserName(userinfo.getCreatedBy());
				user.setChildUserName(userinfo.getUserName());
				user.setRole(CCPConstant.USER);
			} else if (userinfo.getRole().equals("C")) {
				user.setRole(CCPConstant.COLLEGE);
			} // N is used for Company
			else if (userinfo.getRole().equals("N")) {
				user.setRole(CCPConstant.COMPANY);
			} else if (userinfo.getRole().equals("S")) {
				user.setRole(CCPConstant.SUPERUSER);
			}
			user.setCollegeList(TpoUtil.getAllCollegeList(user.getUserName(),user.getRole()));
			
			if (!CCPConstant.SUPERUSER.equals(user.getRole())) {
				BroadCastMessage broadCastMessage = commonDBBean.getCurrentBroadCastMessage(user.getParent(), true);
				if (broadCastMessage != null) {
					user.setNoOfNotification(user.getNoOfNotification() + 1);
				}
				if (user.getExpString() != null) {
					user.setNoOfNotification(user.getNoOfNotification() + 1);
				}
				List<String> list = commonDBBean.getAdminNoticeList();
				if ( list != null && list.size() > 0) {
					user.setNoOfNotification(user.getNoOfNotification() + list.size());
				}
			}
			user.setProfilePic(null);
			user.setLogo(null);
			Calendar cal = Calendar.getInstance();
			userinfo.setLastLogin(cal.getTime());
			userinfo.setLoginAttempt(0);
			if (session != null) {
				session.saveOrUpdate(userinfo);
			}
		} else {
			UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error14));
		}
	}

	private void setExpiryStr(AdminUser user, Date date, Date expDate) {
		user.setExpString(null);
		int ONE_DAY_MILLIS = 86400 * 1000;
		// ....
		long deltaMillis = expDate.getTime() - date.getTime();
		if (deltaMillis < 7 * ONE_DAY_MILLIS) {
			user.setExpString(FbMessageUtil.getLabel("Your_account_will_expire_in", "7"));
		}
		if (deltaMillis < 6 * ONE_DAY_MILLIS) {
			user.setExpString(FbMessageUtil.getLabel("Your_account_will_expire_in", "6"));
		}
		if (deltaMillis < 5 * ONE_DAY_MILLIS) {
			user.setExpString(FbMessageUtil.getLabel("Your_account_will_expire_in", "5"));
		}
		if (deltaMillis < 4 * ONE_DAY_MILLIS) {
			user.setExpString(FbMessageUtil.getLabel("Your_account_will_expire_in", "4"));
		}
		if (deltaMillis < 3 * ONE_DAY_MILLIS) {
			user.setExpString(FbMessageUtil.getLabel("Your_account_will_expire_in", "3"));
		}
		if (deltaMillis < 2 * ONE_DAY_MILLIS) {
			user.setExpString(FbMessageUtil.getLabel("Your_account_will_expire_in", "2"));
		}
		if (deltaMillis < 1 * ONE_DAY_MILLIS) {
			user.setExpString(FbMessageUtil.getLabel("Your_account_will_expire_in", "1"));
		}
	}

	public String goToDashBoard() {
			return "adminDashboardNew";
	}

	public String goToStudentHomePage() {
			return "studentHomePageT2";
	}

	
	public void sendUserPassword() {
		try {
			EmailUtil emailUtill = getEmailInstance();
			if (emailUtill != null) {
				Session session = sessionFactory.getCurrentSession();
				Criteria criteria = session.createCriteria(Logindetails.class);
				criteria.createAlias("userdetails", "userdetails");
				criteria.add(Restrictions.eq("userName", userName));
				criteria.add(Restrictions.eq("userdetails.email", email));
				Logindetails userInfo = (Logindetails) criteria.uniqueResult();
				if (userInfo == null) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Incorrect_User_Name_or_Email"));
				} else {
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
					message.append("<br><br><a href='" + TpoUtil.getBasePath(null)
							+ "xhtml/ui/resetUserPassword.faces?pnsgffmffbhvgkbf=" + userInfo.getPassword()
							+ "&dfdnmfbnndfn=" + userdetails.getUserName() + "'>"
							+ FbMessageUtil.getLabel("Please_click_on_this_link_to_reset_your_password") + "</a><br>");
					message.append(TpoUtil.getMesageString());
					emailUtill.postMail(recipients, subject.toString(), message.toString(), TpoUtil.ADMIN_EMAIL,
							Message.RecipientType.TO);
					UIBackingBean
							.setSuccessMessage(FbMessageUtil.getLabel("Password_reset_Email_has_been_sent_on", email));
					userName = null;
					email = null;
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
		CollegeConnectBackUp backUp = (CollegeConnectBackUp) TpoUtil.getManagedBean("collegeConnectBackUp");
		if (backUp != null) {
			backUp.setDateAndTime(null);
		}

		return logout;

	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public void showClints(AjaxActionEvent event) {
		showClient = true;
	}

	public void showStudents(AjaxActionEvent event) {
		showStudent = true;
	}

	@SuppressWarnings("unchecked")
	public void showStudentCount(AjaxActionEvent event) {
		Session session = sessionFactory.getCurrentSession();
		NativeQuery<Object> query = session.createSQLQuery("select count(rollnumber) from registration");
		studentCount = query.uniqueResult();
	}

	public void setClientName(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					logindetails = (Logindetails) sessionFactory.getCurrentSession().get(Logindetails.class,
							(String) parameter.getValue());
					if (logindetails != null) {
						logindetails.setPassword(null);
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

	public Logindetails getLogindetails() {
		return logindetails;
	}

	public void setLogindetails(Logindetails logindetails) {
		this.logindetails = logindetails;
	}

	public boolean isShowClient() {
		return showClient;
	}

	public void setShowClient(boolean showClient) {
		this.showClient = showClient;
	}

	public boolean isShowStudent() {
		return showStudent;
	}

	public void setShowStudent(boolean showStudent) {
		this.showStudent = showStudent;
	}

	public Object getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(Object studentCount) {
		this.studentCount = studentCount;
	}

}
