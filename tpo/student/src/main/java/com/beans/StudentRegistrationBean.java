package com.beans;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.openfaces.component.command.CommandLink;
import org.openfaces.component.input.SuggestionField;
import org.openfaces.event.AjaxActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.annotation.ReferralHistory;
import com.dao.CommonDBBean;
import com.email.EmailUtil;
import com.hibernate.Achivements;
import com.hibernate.Backdetails;
import com.hibernate.Contactinfo;
import com.hibernate.Notice;
import com.hibernate.Percentageinfo;
import com.hibernate.Personalinfo;
import com.hibernate.Registration;
import com.pdf.generator.PDFGenerator;
import com.util.CCPConstant;
import com.util.Encryption;
import com.util.FbMessageUtil;
import com.util.IMAGECONS;
import com.util.ResourceID;
import com.util.SmsUtil;

import com.util.TpoUtil;
import com.util.WebFlowTabName;

@Repository("StudentRegistrationBean")
@Transactional(readOnly = true)
@Scope("session")
public class StudentRegistrationBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(StudentRegistrationBean.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private PDFGenerator pDFGenerator;

	@Autowired
	private FileUploadUtility fileUploadUtility;

	private String fileName;

	private Registration registration = new Registration();
	private Personalinfo personalinfo = new Personalinfo();
	private Percentageinfo percentageinfo = new Percentageinfo();
	private Contactinfo contactinfo = new Contactinfo();
	private Backdetails backdetails = new Backdetails();
	private Achivements achivements = new Achivements();

	private ReferralHistory referralHistory = new ReferralHistory();

	private Boolean referralFlag = "YES".equals(enableReferralReward) ? true : false;

	private Boolean isEditable;

	private String currentCourse;

	private String currentMode = CCPConstant.CREATE;

	private Notice notice;

	private String newEmail;

	private String newMobileNumber;

	private String verificationNumber;

	private String verificationCode;

	private String isSmsEnabled;

	private long pastTime;

	private Boolean allValidated;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void edit() {
		isEditable = true;
	}

	private void setLastUpDatedBy(Object object) {
		String updatedByName = null;
		Date time = Calendar.getInstance().getTime();
		if (Student.getStudent().getUserName() != null) {
			updatedByName = Student.getStudent().getUserName();
		} else {
			updatedByName = registration.getFirstName() + " " + registration.getLastName();
		}
		if (object instanceof Registration) {
			registration.setLastUpdated(time);
			registration.setLastUpdatedBy(updatedByName);
		} else if (object instanceof Achivements) {
			achivements.setLastUpdated(time);
			achivements.setLastUpdatedBy(updatedByName);
		} else if (object instanceof Contactinfo) {
			contactinfo.setLastUpdated(time);
			contactinfo.setLastUpdatedBy(updatedByName);
		} else if (object instanceof Percentageinfo) {
			percentageinfo.setLastUpdated(time);
			percentageinfo.setLastUpdatedBy(updatedByName);
		} else if (object instanceof Personalinfo) {
			personalinfo.setLastUpdated(time);
			personalinfo.setLastUpdatedBy(updatedByName);
		} else if (object instanceof Backdetails) {
			backdetails.setLastUpdated(time);
			backdetails.setLastUpdatedBy(updatedByName);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateMainSection() {
		Session session;
		try {
			session = sessionFactory.getCurrentSession();
			EmailUtil emailUtill = getEmailInstance();
			setLastUpDatedBy(registration);
			if (Student.getStudent().getUserName() != null) {
				registration.setApproved(false);
			}
			session.update(registration);
			if (emailUtill != null) {
				sendUpdateEmail(emailUtill);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success6, registration.getEmail()));
			} else {
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success6Noemail));
			}
			isEditable = false;

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updatePersonalSection() {
		Session session;
		try {
			session = sessionFactory.getCurrentSession();
			EmailUtil emailUtill = getEmailInstance();
			if ("".equals(personalinfo.getHandicapped())) {
				personalinfo.setHandicapped("NA");
			}
			if (Student.getStudent().getUserName() != null) {
				registration.setApproved(false);
			}
			setLastUpDatedBy(personalinfo);
			session.update(personalinfo);
			if (emailUtill != null) {
				sendUpdateEmail(emailUtill);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success6, registration.getEmail()));
			} else {
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success6Noemail));
			}
			isEditable = false;

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private void sendUpdateEmail(EmailUtil emailUtill) throws MessagingException {
		List<String> recipients = new ArrayList<String>(1);
		recipients.add(registration.getEmail());
		String subject = FbMessageUtil.getLabel("Your_Freshers_Buddy_information_updated");
		StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear"));
		message.append(registration.getFirstName()).append(" ").append(registration.getLastName()).append(",<br>")
				.append("" + FbMessageUtil.getLabel("Your_Freshers_Buddy_information_updated") + "<br>");
		message.append(registration.toString());
		message.append(personalinfo.toString());
		message.append(percentageinfo.toString());
		message.append(contactinfo.toString());
		message.append(backdetails.toString());
		message.append(achivements.toString());
		message.append(
				"<br><br><font size='5' color='green'><a href='https://android-fb-apps.s3.ap-south-1.amazonaws.com/FBN.apk'>"
						+ FbMessageUtil.getLabel("Please_click_on_this_link_to_downlaod_app") + "</a></font><br>");
		message.append(TpoUtil.getMesageString());
		emailUtill.postMail(recipients, subject, message.toString(), TpoUtil.ADMIN_EMAIL, Message.RecipientType.TO);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updatePercentageSection() {
		Session session;
		try {
			session = sessionFactory.getCurrentSession();
			EmailUtil emailUtill = getEmailInstance();
			if (Student.getStudent().getUserName() != null) {
				registration.setApproved(false);
			}
			setLastUpDatedBy(percentageinfo);
			if (percentageinfo.getHigherSecondaryPassing() != null && percentageinfo.getHigherSecondaryPassing() != 0) {
				if (null != percentageinfo
						&& percentageinfo.getHighSchoolPassing() > percentageinfo.getHigherSecondaryPassing()) {
					UIBackingBean.setInfoMessage(FbMessageUtil
							.getLabel("High_School_Passing_year_can_not_greater_then_Higher_Secondary_Passing_Year"));
					return;
				}
			}
			if (null != percentageinfo
					&& percentageinfo.getHighSchoolPassing().equals(percentageinfo.getHigherSecondaryPassing())) {
				UIBackingBean.setInfoMessage(FbMessageUtil
						.getLabel("High_School_Passing_year_can_not_greater_then_Higher_Secondary_Passing_Year"));
				return;
			}

			calculatePercent();
			session.update(percentageinfo);
			if (emailUtill != null) {
				sendUpdateEmail(emailUtill);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success6, registration.getEmail()));
			} else {
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success6Noemail));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateBackLogSection() {
		Session session;
		try {
			session = sessionFactory.getCurrentSession();
			EmailUtil emailUtill = getEmailInstance();
			setLastUpDatedBy(backdetails);
			if (backdetails.getBackLog() == 1 || backdetails.getNumberOfBacklogs() > 0 || backdetails.getBaGroup() == 1
					|| backdetails.getPassMoreThenOneAttempt() == 1) {
				if (backdetails.getBackDetails() == null || "".equals(backdetails.getBackDetails())) {
					UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Info5));
					return;
				}
			}
			if (backdetails.getBaGroup() == 1 && backdetails.getPassMoreThenOneAttempt() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Info6));
				return;
			}
			if (Student.getStudent().getUserName() != null) {
				registration.setApproved(false);
			}
			session.update(backdetails);
			if (emailUtill != null) {
				sendUpdateEmail(emailUtill);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success6, registration.getEmail()));
			} else {
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success6Noemail));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateAchivementsInformation() {
		Session session;
		try {
			session = sessionFactory.getCurrentSession();
			EmailUtil emailUtill = getEmailInstance();
			setLastUpDatedBy(achivements);
			if (Student.getStudent().getUserName() != null) {
				registration.setApproved(false);
			}
			session.update(achivements);
			if (emailUtill != null) {
				sendUpdateEmail(emailUtill);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success6, registration.getEmail()));
			} else {
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success6Noemail));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateContactInformation() {
		Session session;
		try {
			session = sessionFactory.getCurrentSession();
			EmailUtil emailUtill = getEmailInstance();
			setUnverifiedFlag();
			setLastUpDatedBy(contactinfo);
			if (Student.getStudent().getUserName() != null) {
				registration.setApproved(false);
			}
			session.update(registration);
			session.update(contactinfo);
			if (emailUtill != null) {
				sendUpdateEmail(emailUtill);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success6, registration.getEmail()));
			} else {
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success6Noemail));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public String beforeSave() {
		if (!isValidationSuccess()) {
			allValidated = false;
		} else {
			allValidated = true;
		}
		return "";
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String saveRecord() {
		WebFlow flow = (WebFlow) TpoUtil.getManagedBean(WebFlow.class.getSimpleName());
		Session session;
		try {
			if (!isValidationSuccess()) {
				return flow.mainPage();
			}
			session = sessionFactory.getCurrentSession();
			CommonDBBean bean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
			if (CCPConstant.CREATE.equals(currentMode) && bean != null
					&& bean.isRecordExist(registration.getRollnumber())) {
				UIBackingBean.setErrorMessage(
						FbMessageUtil.getLabel("Record_already_exist_for", registration.getRollnumber()));
				return flow.mainPage();
			}
			if (bean != null && !bean.isCollegeCodeCorrect(registration.getCollegeName())) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_enter_correct_College_Code"));
				return flow.mainPage();
			}

			if (!("10".equals(currentCourse) || "12".equals(currentCourse))) {
				calculatePercent();
			}
			if (("10".equals(currentCourse) || "12".equals(currentCourse))) {
				personalinfo.setSemester("NA");
			}

			setLastUpDatedBy(registration);
			setLastUpDatedBy(personalinfo);
			setLastUpDatedBy(percentageinfo);
			setLastUpDatedBy(backdetails);
			setLastUpDatedBy(contactinfo);
			setLastUpDatedBy(achivements);
			EmailUtil emailUtill = getEmailInstance();
			if (CCPConstant.CREATE.equalsIgnoreCase(currentMode)) {
				registration.setEmailVarified(false);
				registration.setApproved(false);
				registration.setStatus(true);
				registration.setTheme("T3");
				registration.setColor(1);
				registration.setPassword(Encryption.getEncryptedString(registration.getPassword()));
				session.saveOrUpdate(registration);
				personalinfo.setCompanyName("");
				if ("".equals(personalinfo.getHandicapped())) {
					personalinfo.setHandicapped("NA");
				}
				personalinfo.setRemark("NA");
				personalinfo.setRollnumber(registration.getRollnumber());
				personalinfo.setRegistration(registration);
				personalinfo.setCurrentCourse(currentCourse);
				registration.setPersonalinfo(personalinfo);
				session.save(personalinfo);

				percentageinfo.setRollnumber(registration.getRollnumber());
				percentageinfo.setRegistration(registration);
				registration.setPercentageinfo(percentageinfo);
				session.save(percentageinfo);

				backdetails.setRollnumber(registration.getRollnumber());
				backdetails.setRegistration(registration);
				backdetails.setBlackList(false);
				if (("10".equals(currentCourse) || "12".equals(currentCourse))) {
					backdetails.setBackLog(0);
					backdetails.setPassMoreThenOneAttempt(0);
					backdetails.setNumberOfBacklogs(0);
					backdetails.setBaGroup(0);
					backdetails.setEducationGap(Short.parseShort("0"));
					;
				}
				registration.setBackdetails(backdetails);
				session.save(backdetails);

				contactinfo.setRollnumber(registration.getRollnumber());
				contactinfo.setNumberVerified(false);
				contactinfo.setRegistration(registration);
				registration.setContactinfo(contactinfo);
				session.save(contactinfo);
				achivements.setRollnumber(registration.getRollnumber());
				achivements.setRegistration(registration);
				registration.setAchivements(achivements);
				session.save(achivements);
				if (referralFlag
						&& (referralHistory.getReferredBY() != null && !"".equals(referralHistory.getReferredBY()))) {
					referralHistory.setDate(TpoUtil.getFormatedDateInddMMyyyy(new Date()));
					referralHistory.setReferred(registration.getRollnumber());
					session.saveOrUpdate(referralHistory);
				}
				if (emailUtill != null) {
					if (referralFlag && referralHistory.getReferredBY() != null) {
						sendreferralEmail(session, emailUtill);

					}
					List<String> recipients = new ArrayList<String>(1);
					recipients.add(registration.getEmail());
					String subject = FbMessageUtil.getLabel("Your_Freshers_Buddy_registration_related_information");
					StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear"));
					message.append(registration.getFirstName()).append(" ").append(registration.getLastName())
							.append(",<br>").append(subject + "<br>");
					message.append(registration.toString());
					message.append(personalinfo.toString());
					message.append(percentageinfo.toString());
					message.append(contactinfo.toString());
					if (!("10".equals(currentCourse) || "12".equals(currentCourse))) {
						message.append(backdetails.toString());
					}
					message.append(achivements.toString());
					message.append("<br><br><br><br>"
							+ FbMessageUtil.getLabel("Please_click_on_this_link_to_verify_your_email") + "<br></font>");
					message.append(TpoUtil.getBasePath(null) + "servlet/AjaxServlet?dfdnmfbnndfn="
							+ registration.getRollnumber()
							+ "&fdfdfemailfdf=65980hgj5j56j4h6j456j78j3k77956b5n6b4j6b64746hgbhdppqhqqsssrrnvnhvgkgnghkggggngh@0146556"
							+ registration.getPassword()
							+ "46556@0145j5hk3k8j9k63nvnknrf35846guyofndgs46fh4h78394nhvjvh6vnvbgh7jgjggh0000h89g7f9b111bjhjghgydjfhgnd<br>");

					message.append(
							"<br><br><font size='5' color='green'><a href='https://android-fb-apps.s3.ap-south-1.amazonaws.com/FBN.apk'>"
									+ FbMessageUtil.getLabel("Please_click_on_this_link_to_downlaod_app")
									+ "</a></font><br>");
					message.append(TpoUtil.getMesageString());
					emailUtill.postMail(recipients, subject, message.toString(), TpoUtil.ADMIN_EMAIL,
							Message.RecipientType.TO);
					UIBackingBean
							.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success5, registration.getEmail()));

				} else {
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success5Noemail));
				}
				currentMode = CCPConstant.UPDATE;
				return redirectToStudentHome(session);
			} else if (CCPConstant.UPDATE.equalsIgnoreCase(currentMode)) {
				/*
				 * User user = User.getUser(); if (user != null && user.getRole() == null) {
				 * registration.setApproved(false); }
				 */
				setUnverifiedFlag();
				session.update(registration);
				session.update(personalinfo);
				session.update(percentageinfo);
				session.update(backdetails);
				session.update(contactinfo);
				session.update(achivements);
				if (emailUtill != null) {
					List<String> recipients = new ArrayList<String>(1);
					recipients.add(registration.getEmail());
					String subject = FbMessageUtil.getLabel("Your_Freshers_Buddy_information_updated");
					StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear"));
					message.append(registration.getFirstName()).append(" ").append(registration.getLastName())
							.append(",<br>").append(FbMessageUtil.getLabel("Your_Freshers_Buddy_information") + "<br>");
					message.append(registration.toString());
					message.append(personalinfo.toString());
					message.append(percentageinfo.toString());
					message.append(contactinfo.toString());
					message.append(backdetails.toString());
					message.append(achivements.toString());
					message.append(
							"<br><br><font size='5' color='green'><a href='https://android-fb-apps.s3.ap-south-1.amazonaws.com/FBN.apk'>"
									+ FbMessageUtil.getLabel("Please_click_on_this_link_to_downlaod_app")
									+ "</a></font><br>");

					message.append(TpoUtil.getMesageString());
					emailUtill.postMail(recipients, subject, message.toString(), TpoUtil.ADMIN_EMAIL,
							Message.RecipientType.TO);
					UIBackingBean
							.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success6, registration.getEmail()));
				} else {
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success6Noemail));
				}
				isEditable = false;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return flow.mainPageNew();
	}

	private void setUnverifiedFlag() {
		FacesContext context = FacesContext.getCurrentInstance();
		String oldMobileNumber = context.getExternalContext().getRequestParameterMap().get("oldMobileNumber");
		if (oldMobileNumber != null && !oldMobileNumber.equals(contactinfo.getMobileNumber())) {
			contactinfo.setNumberVerified(false);
		}
		String oldEmail = context.getExternalContext().getRequestParameterMap().get("oldEmail");
		if (oldEmail != null && !oldEmail.equals(registration.getEmail())) {
			registration.setEmailVarified(false);
		}

	}

	private void sendreferralEmail(Session session, EmailUtil emailUtill) throws MessagingException {
		int totalReferal = 0;
		Criteria criteria = session.createCriteria(ReferralHistory.class);
		criteria.add(Restrictions.eq("referredBY", referralHistory.getReferredBY()));
		List<ReferralHistory> list = criteria.list();
		if (list != null && !list.isEmpty()) {
			totalReferal = list.size();
		}
		Registration regi = session.get(Registration.class, referralHistory.getReferredBY());
		if (regi != null && !regi.getEmailVarified()) {
			List<String> recipient = new ArrayList<String>(1);
			recipient.add(regi.getEmail());
			String subject = FbMessageUtil.getLabel("Your_Freshers_Buddy_registration_related_information");
			StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear"));
			message.append(regi.getFirstName()).append(" ").append(regi.getLastName()).append(",<br>")
					.append(subject + "<br>");
			Object param[] = new Object[1];
			param[0] = registration.getFirstName() + " " + registration.getLastName();
			message.append(FbMessageUtil.getLabel("Thanks_for_referring", param));
			message.append("<br>");
			Object param1[] = new Object[1];
			param1[0] = totalReferal;
			message.append(FbMessageUtil.getLabel("Your_total_referral_count_is", param1));
			message.append("<br>");
			Object param2[] = new Object[1];
			param2[0] = TpoUtil.ADMIN_EMAIL;
			message.append(FbMessageUtil.getLabel("Please_contact_your_administrator_at", param2));
			message.append("<br>");
			message.append(TpoUtil.getMesageString());
			emailUtill.postMail(recipient, subject, message.toString(), TpoUtil.ADMIN_EMAIL, Message.RecipientType.TO);
		}
	}

	private String redirectToStudentHome(Session session) {
		Student student = Student.getStudent();
		if (student != null) {
			student.setUserName(registration.getFirstName() + " " + registration.getLastName());
			student.setRollNumber(registration.getRollnumber());
			NativeQuery<String> query = session.createSQLQuery(
					"select userName from college where collegeName = '" + registration.getCollegeName() + "'");
			student.setCreateBy((String) query.uniqueResult());
			student.setTheme(registration.getTheme());
			student.setColorCode(registration.getColor());
			return "studentHomePage" + Student.getStudent().getTheme();
		}
		return "studentHomePageNew";
	}

	public void calculateBEPercent(AjaxActionEvent actionEvent) {
		double total = 0d;
		int i = 0;
		percentageinfo.setBeAverege(total);
		if (percentageinfo.getBe1sem() != null && percentageinfo.getBe1sem() != 0.0) {
			i++;
			total = total + percentageinfo.getBe1sem();
		}
		if (percentageinfo.getBe2sem() != null && percentageinfo.getBe2sem() != 0.0) {
			i++;
			total = total + percentageinfo.getBe2sem();
		}
		if (percentageinfo.getBe3sem() != null && percentageinfo.getBe3sem() != 0.0) {
			i++;
			total = total + percentageinfo.getBe3sem();
		}
		if (percentageinfo.getBe4sem() != null && percentageinfo.getBe4sem() != 0.0) {
			i++;
			total = total + percentageinfo.getBe4sem();
		}
		if (percentageinfo.getBe5sem() != null && percentageinfo.getBe5sem() != 0.0) {
			i++;
			total = total + percentageinfo.getBe5sem();
		}
		if (percentageinfo.getBe6sem() != null && percentageinfo.getBe6sem() != 0.0) {
			i++;
			total = total + percentageinfo.getBe6sem();
		}
		if (percentageinfo.getBe7sem() != null && percentageinfo.getBe7sem() != 0.0) {
			i++;
			total = total + percentageinfo.getBe7sem();
		}
		if (percentageinfo.getBe8sem() != null && percentageinfo.getBe8sem() != 0.0) {
			i++;
			total = total + percentageinfo.getBe8sem();
		}

		if (total != 0 && i != 0) {
			percentageinfo.setBeAverege((double) Math.round((total / i) * 100) / 100);
		}

	}

	public void calculatePGPercent(AjaxActionEvent actionEvent) {
		double total = 0d;
		int i = 0;
		percentageinfo.setMeAverage(total);
		if (percentageinfo.getMeBsc1sem() != null && percentageinfo.getMeBsc1sem() != 0.0) {
			i++;
			total = total + percentageinfo.getMeBsc1sem();
		}
		if (percentageinfo.getMeBsc2sem() != null && percentageinfo.getMeBsc2sem() != 0.0) {
			i++;
			total = total + percentageinfo.getMeBsc2sem();
		}
		if (percentageinfo.getMeBsc3sem() != null && percentageinfo.getMeBsc3sem() != 0.0) {
			i++;
			total = total + percentageinfo.getMeBsc3sem();
		}
		if (percentageinfo.getMeBsc4sem() != null && percentageinfo.getMeBsc4sem() != 0.0) {
			i++;
			total = total + percentageinfo.getMeBsc4sem();
		}
		if (total != 0 && i != 0) {
			percentageinfo.setMeAverage((double) Math.round((total / i) * 100) / 100);
		}

	}

	public void calculateDiplomaPercent(AjaxActionEvent actionEvent) {
		double total = 0d;
		int i = 0;
		percentageinfo.setDiplomaOthers(total);
		if (percentageinfo.getDiploma1sem() != null && percentageinfo.getDiploma1sem() != 0.0) {
			i++;
			total = total + percentageinfo.getDiploma1sem();
		}
		if (percentageinfo.getDiploma2sem() != null && percentageinfo.getDiploma2sem() != 0.0) {
			i++;
			total = total + percentageinfo.getDiploma2sem();
		}
		if (percentageinfo.getDiploma3sem() != null && percentageinfo.getDiploma3sem() != 0.0) {
			i++;
			total = total + percentageinfo.getDiploma3sem();
		}
		if (percentageinfo.getDiploma4sem() != null && percentageinfo.getDiploma4sem() != 0.0) {
			i++;
			total = total + percentageinfo.getDiploma4sem();
		}
		if (percentageinfo.getDiploma5sem() != null && percentageinfo.getDiploma5sem() != 0.0) {
			i++;
			total = total + percentageinfo.getDiploma5sem();
		}
		if (percentageinfo.getDiploma6sem() != null && percentageinfo.getDiploma6sem() != 0.0) {
			i++;
			total = total + percentageinfo.getDiploma6sem();
		}
		if (total != 0 && i != 0) {
			percentageinfo.setDiplomaOthers((double) Math.round((total / i) * 100) / 100);
		}
	}

	private void calculatePercent() {
		if ((percentageinfo.getBe1sem() != null && percentageinfo.getBe1sem() != 0.0)
				&& (percentageinfo.getBe2sem() != null && percentageinfo.getBe2sem() != 0.0)) {
			percentageinfo.setAvgbe1year((percentageinfo.getBe1sem() + percentageinfo.getBe2sem()) / 2);
		}
		if ((percentageinfo.getBe3sem() != null && percentageinfo.getBe3sem() != 0.0)
				&& (percentageinfo.getBe4sem() != null && percentageinfo.getBe4sem() != 0.0)) {
			percentageinfo.setAvgbe2year((percentageinfo.getBe3sem() + percentageinfo.getBe4sem()) / 2);
		}
		if ((percentageinfo.getBe5sem() != null && percentageinfo.getBe5sem() != 0.0)
				&& (percentageinfo.getBe6sem() != null && percentageinfo.getBe6sem() != 0.0)) {
			percentageinfo.setAvgbe3year((percentageinfo.getBe5sem() + percentageinfo.getBe6sem()) / 2);
		}
		if ((percentageinfo.getBe7sem() != null && percentageinfo.getBe7sem() != 0.0)
				&& (percentageinfo.getBe8sem() != null && percentageinfo.getBe8sem() != 0.0)) {
			percentageinfo.setAvgbe4year((percentageinfo.getBe7sem() + percentageinfo.getBe8sem()) / 2);
		}

		calculateBEPercent(null);
		calculatePGPercent(null);
		calculateDiplomaPercent(null);
	}

	private boolean isValidationSuccess() {
		if (registration != null) {
			if (null == personalinfo || null == personalinfo.getGender() || null == personalinfo.getDob()) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_enter_all_the_mandatory_fields_in", WebFlowTabName.PI));
				return false;
			}
			if (null == percentageinfo || null == percentageinfo.getHighSchoolPercent()
					|| null == percentageinfo.getHighSchoolBoard() || null == percentageinfo.getHighSchoolPassing()) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_enter_all_the_mandatory_fields_in", WebFlowTabName.AI));
				return false;
			} else if (!"10".equals(currentCourse)) {
				if (percentageinfo.getHigherSecondaryPassing() != null
						&& percentageinfo.getHigherSecondaryPassing() != 0) {
					if (null != percentageinfo
							&& percentageinfo.getHighSchoolPassing() > percentageinfo.getHigherSecondaryPassing()) {
						UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(
								"High_School_Passing_year_can_not_greater_then_Higher_Secondary_Passing_Year"));
						return false;
					}
				}
				if (null != percentageinfo
						&& percentageinfo.getHighSchoolPassing().equals(percentageinfo.getHigherSecondaryPassing())) {
					UIBackingBean.setInfoMessage(
							FbMessageUtil.getLabel("High_School_and_Higher_Secondary_can_not_passed_in_same_Year"));
					return false;
				}
			}
			if (!("10".equals(currentCourse) || "12".equals(currentCourse))) {
				if (null == backdetails || null == backdetails.getBackLog() || null == backdetails.getBaGroup()
						|| null == backdetails.getNumberOfBacklogs()
						|| null == backdetails.getPassMoreThenOneAttempt()) {
					UIBackingBean.setInfoMessage(
							FbMessageUtil.getLabel("Please_enter_all_the_mandatory_fields_in", WebFlowTabName.BI));
					return false;
				} else {
					if (backdetails.getBackLog() == 1 || backdetails.getNumberOfBacklogs() > 0
							|| backdetails.getBaGroup() == 1 || backdetails.getPassMoreThenOneAttempt() == 1) {
						if (backdetails.getBackDetails() == null || "".equals(backdetails.getBackDetails())) {
							UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Info5));
							return false;
						}
					}
					if (backdetails.getBaGroup() == 1 && backdetails.getPassMoreThenOneAttempt() == 0) {
						UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Info6));
						return false;
					}
				}
			}
			if (null == contactinfo || null == contactinfo.getGlassPowerLeft() || null == registration.getEmail()
					|| null == contactinfo.getGlassPowerRight() || null == contactinfo.getHieght()
					|| null == contactinfo.getWeight() || null == contactinfo.getMobileNumber()
					|| null == contactinfo.getPermanentAddress() || null == contactinfo.getPermanentCity()
					|| null == contactinfo.getPermanentState() || null == contactinfo.getPresentAddress()
					|| null == contactinfo.getPresentCity() || null == contactinfo.getPresentState()) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_enter_all_the_mandatory_fields_in", WebFlowTabName.AI));
				return false;
			}

		}
		return true;
	}

	public void renders(AjaxBehaviorEvent event) {
		HtmlSelectOneMenu menu = (HtmlSelectOneMenu) event.getComponent();
		currentCourse = (String) menu.getValue();
		System.out.println(currentCourse);
		System.out.println(personalinfo.getDiploma());
	}

	public void diploamListener(AjaxBehaviorEvent event) {
		SuggestionField field = (SuggestionField) event.getComponent();
		System.out.println(personalinfo.getDiploma());
		personalinfo.setDiploma((String) field.getValue());
		System.out.println(personalinfo.getDiploma());
	}

	public Registration getRegistration() {
		return registration;
	}

	public void setRegistration(Registration registration) {
		this.registration = registration;
	}

	public String getCurrentCourse() {
		return currentCourse;
	}

	public void setCurrentCourse(String currentCourse) {
		this.currentCourse = currentCourse;
	}

	public Personalinfo getPersonalinfo() {
		return personalinfo;
	}

	public void setPersonalinfo(Personalinfo personalinfo) {
		this.personalinfo = personalinfo;
	}

	public Contactinfo getContactinfo() {
		return contactinfo;
	}

	public void setContactinfo(Contactinfo contactinfo) {
		this.contactinfo = contactinfo;
	}

	public Percentageinfo getPercentageinfo() {
		return percentageinfo;
	}

	public void setPercentageinfo(Percentageinfo percentageinfo) {
		this.percentageinfo = percentageinfo;
	}

	public Achivements getAchivements() {
		return achivements;
	}

	public void setAchivements(Achivements achivements) {
		this.achivements = achivements;
	}

	public Backdetails getBackdetails() {
		return backdetails;
	}

	public void setBackdetails(Backdetails backdetails) {
		this.backdetails = backdetails;
	}

	public String getCurrentMode() {
		return currentMode;
	}

	public void setCurrentMode(String currentMode) {
		this.currentMode = currentMode;
	}

	public void renderResume(ActionEvent actionEvent) {
		byte[] buf = null;
		String fileName = registration.getPersonalinfo().getResume();
		String resumeType = registration.getPersonalinfo().getResumeType();
		if (fileName != null && resumeType != null) {
			buf = fileUploadUtility.downloadFileWithParam(getFileServiceUrl() + "/download", fileName,
					IMAGECONS.student.toString() + registration.getRollnumber() + "/" + IMAGECONS.resume.toString());
			if (buf != null) {
				if ("pdf".equalsIgnoreCase(resumeType)) {
					TpoUtil.renderPDFFile(buf, fileName);
				} else {
					TpoUtil.renderWordFile(buf, fileName);
				}
			}
		}
	}

	public String printResume() {
		try {
			if (registration != null) {
				CommonDBBean commonDBBean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
				byte[] bytes = pDFGenerator.generateRegistrationForm(registration, commonDBBean);
				TpoUtil.renderPDFFile(bytes, registration.getRollnumber());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return "";
	}

	public Boolean getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}

	public void setNotice(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					Session session = sessionFactory.getCurrentSession();
					String noticeName = (String) link.getValue();
					Student student = Student.getStudent();

					List<String> createdByList = new ArrayList<>(2);
					createdByList.add(student.getCreateBy());
					createdByList.add(student.getRollNumber());

					Criteria criteria = session.createCriteria(Notice.class);
					criteria.add(Restrictions.eq("noticeName", noticeName));
					criteria.add(Restrictions.in("createdBy", createdByList));
					notice = (Notice) criteria.uniqueResult();
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

	public void setNoticeObj(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					notice = (Notice) parameter.getValue();

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

	public void setFileName(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					fileName = (String) parameter.getValue();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setFileNameAndNotice(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					fileName = (String) parameter.getValue();
					parameter = (UIParameter) list.get(1);
					notice = (Notice) parameter.getValue();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public Notice getNotice() {
		return notice;
	}

	public void setNotice(Notice notice) {
		this.notice = notice;
	}

	public void sendEmail() {
		try {
			EmailUtil emailUtill = getEmailInstance();
			if (emailUtill != null) {
				List<String> recipients = new ArrayList<String>(1);
				recipients.add(registration.getEmail());
				String subject = FbMessageUtil.getLabel("E_mail_Verification");
				StringBuffer message = new StringBuffer("<font color=green size=5>" + FbMessageUtil.getLabel("Dear")
						+ registration.getFirstName() + " " + registration.getLastName() + ",<br>"
						+ FbMessageUtil.getLabel("Your_Enrollment_No_is", registration.getRollnumber()));
				message.append("<br><a href='" + TpoUtil.getBasePath(null) + "servlet/AjaxServlet?dfdnmfbnndfn="
						+ registration.getRollnumber()
						+ "&fdfdfemailfdf=65980hgj5j56j4h6j456j78j3k77956b5n6b4j6b64746hgbhdppqhqqsssrrnvnhvgkgnghkggggngh@0146556"
						+ registration.getPassword()
						+ "46556@0145j5hk3k8j9k63nvnknrf35846guyofndgs46fh4h78394nhvjvh6vnvbgh7jgjggh0000h89g7f9b111bjhjghgydjfhgnd'>Please click here to verify your email.</a><br>");
				message.append(TpoUtil.getMesageString());
				emailUtill.postMail(recipients, subject, message.toString(), TpoUtil.ADMIN_EMAIL,
						Message.RecipientType.TO);
				UIBackingBean.setSuccessMessage(
						FbMessageUtil.getLabel("Email_verification_email_sent", registration.getEmail()));

			}
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void refreshImage() {
		verificationNumber = null;
	}

	public void sendVerificationSms() {
		try {
			resendSmsAfter5min();
			if (verificationCode == null && verificationNumber == null) {
				pastTime = System.currentTimeMillis();
				verificationNumber = TpoUtil.get6DigitRandomNumber().toString();
				Object param[] = new Object[2];
				param[0] = verificationNumber;
				param[1] = TpoUtil.getBasePath(null);

				if ("YES".equalsIgnoreCase(TpoUtil.SMS_FLAG_FB)
						&& "YES".equalsIgnoreCase(SmsUtil.getLabel("SmsThroughMsg91"))) {
					TpoUtil.sendTextSmsThroughMsg91(contactinfo.getMobileNumber(),
							FbMessageUtil.getLabel("Your_verification_Code", param));
				}
				if ("YES".equalsIgnoreCase(TpoUtil.SMS_FLAG_FB)
						&& "YES".equalsIgnoreCase(SmsUtil.getLabel("SmsThroughBulkSmsGateway"))) {
					TpoUtil.sendTextSmsThroughBulkSmsGateway(contactinfo.getMobileNumber(),
							FbMessageUtil.getLabel("Your_verification_Code", param));
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEmail() {
		try {
			if (registration.getEmail() != null && !registration.getEmail().equals(newEmail)) {
				Session session = sessionFactory.getCurrentSession();
				registration.setEmail(newEmail);
				registration.setEmailVarified(false);
				session.update(registration);
				UIBackingBean.setSuccessMessage(
						FbMessageUtil.getLabel("Email_is_updated_successfully_Please_verify_new_email_address"));
				sendEmail();
			} else {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Old_and_New_email_is_same"));
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
	public void updateNumber() {
		try {
			if (contactinfo.getMobileNumber() != null && !contactinfo.getMobileNumber().equals(newMobileNumber)) {
				Session session = sessionFactory.getCurrentSession();
				contactinfo.setMobileNumber(newMobileNumber);
				contactinfo.setNumberVerified(false);
				session.update(contactinfo);
				UIBackingBean.setSuccessMessage(
						FbMessageUtil.getLabel("Number_is_updated_successfully_Please_verify_new_number"));
				// sendEmail();
			} else {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Old_and_New_number_is_same"));
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
	public void verifieNumber() {
		try {
			if (verificationCode != null && verificationCode.equals(verificationNumber)) {
				Session session = sessionFactory.getCurrentSession();
				contactinfo.setNumberVerified(true);
				session.update(contactinfo);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Your_number_is_verified_successfully"));
				// sendEmail();
				verificationCode = null;
			} else {
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Incorrect_verification_code"));
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
	public void verifyNumberFromAdmin() {
		try {
			Session session = sessionFactory.getCurrentSession();
			contactinfo.setNumberVerified(true);
			session.update(contactinfo);
			UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Your_number_is_verified_successfully"));
			// sendEmail();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public String getNewEmail() {
		return newEmail;
	}

	public void setNewEmail(String newEmail) {
		this.newEmail = newEmail;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public String getNewMobileNumber() {
		return newMobileNumber;
	}

	public void setNewMobileNumber(String newMobileNumber) {
		this.newMobileNumber = newMobileNumber;
	}

	public RenderedImage getTextAsImage() {
		if (verificationNumber == null) {
			verificationNumber = TpoUtil.get6DigitRandomNumber().toString();
		}
		Color textColor = Color.DARK_GRAY;
		Color glowColor = Color.ORANGE;
		int fontSize = 15;
		String fontName = "Tahoma";
		int fontStyle = Font.PLAIN;
		int glowWidth = 1;
		float paddingSize = 2;
		Font font = new Font(fontName, fontStyle, fontSize);

		AffineTransform transform = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(transform, true, true);
		GlyphVector glyphVector = font.createGlyphVector(frc, verificationNumber);
		LineMetrics lineMetrics = font.getLineMetrics(verificationNumber, frc);
		Rectangle2D logicalBounds = glyphVector.getLogicalBounds();

		int imageWidth = (int) Math.ceil(logicalBounds.getWidth() + glowWidth * 2 + paddingSize * 2);
		int imageHeight = (int) Math.ceil(logicalBounds.getHeight() + glowWidth * 2 + paddingSize * 2);
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		graphics.setPaint(Color.WHITE);
		graphics.fill(new Rectangle2D.Float(0, 0, imageWidth, imageHeight));

		float x = glowWidth + paddingSize;
		float y = glowWidth + paddingSize + lineMetrics.getAscent();
		Shape textShape = glyphVector.getOutline(x, y);

		float glowR = 255 - glowColor.getRed();
		float glowG = 255 - glowColor.getGreen();
		float glowB = 255 - glowColor.getBlue();

		float maxStrokeWidth = glowWidth * 2;
		for (int gradationCount = (int) Math.ceil(glowWidth / 1.5), i = 1; i <= gradationCount; i++) {
			float saturation = ((float) i) / gradationCount;
			float r = 255 - saturation * glowR;
			float g = 255 - saturation * glowG;
			float b = 255 - saturation * glowB;
			Color currentGlowColor = new Color((int) r, (int) g, (int) b);
			float strokeWidth = maxStrokeWidth - maxStrokeWidth / gradationCount * (i - 1);
			Stroke stroke = new BasicStroke(strokeWidth, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_ROUND);
			Shape textOutlineShape = stroke.createStrokedShape(textShape);
			graphics.setStroke(stroke);
			graphics.setPaint(currentGlowColor);
			graphics.fill(textOutlineShape);
		}

		graphics.setPaint(textColor);
		graphics.fill(textShape);

		return image;
	}

	public String getVerificationNumber() {
		return verificationNumber;
	}

	public void setVerificationNumber(String verificationNumber) {
		this.verificationNumber = verificationNumber;
	}

	public ReferralHistory getReferralHistory() {
		return referralHistory;
	}

	public void setReferralHistory(ReferralHistory referralHistory) {
		this.referralHistory = referralHistory;
	}

	public Boolean getReferralFlag() {
		return referralFlag;
	}

	public void setReferralFlag(Boolean referralFlag) {
		this.referralFlag = referralFlag;
	}

	private void resendSmsAfter5min() {
		long test = System.currentTimeMillis();
		if (pastTime > 0) {
			// 300 Seconds
			if (test >= (pastTime + 300 * 1000)) { // multiply by 1000 to get
													// milliseconds
				verificationCode = null;
				verificationNumber = null;
			}
		}
	}

	public String getIsSmsEnabled() {
		isSmsEnabled = TpoUtil.SMS_FLAG_FB;
		return isSmsEnabled;
	}

	public void setIsSmsEnabled(String isSmsEnabled) {
		this.isSmsEnabled = isSmsEnabled;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getImageBytesAsImage() {
		byte[] buf = null;
		if (fileName != null && notice != null) {
			if (fileName.equals(notice.getFileName1()))
				buf = fileUploadUtility.downloadFile(getImageServiceUrl() + "/download",
						notice.getNoticeName() + "_" + notice.getFileName1(), IMAGECONS.notice);
			else if (fileName.equals(notice.getFileName2()))
				buf = fileUploadUtility.downloadFile(getImageServiceUrl() + "/download",
						notice.getNoticeName() + "_" + notice.getFileName2(), IMAGECONS.notice);
			else if (fileName.equals(notice.getFileName3()))
				buf = fileUploadUtility.downloadFile(getImageServiceUrl() + "/download",
						notice.getNoticeName() + "_" + notice.getFileName3(), IMAGECONS.notice);
			else if (fileName.equals(notice.getFileName4()))
				buf = fileUploadUtility.downloadFile(getImageServiceUrl() + "/download",
						notice.getNoticeName() + "_" + notice.getFileName4(), IMAGECONS.notice);
			else if (fileName.equals(notice.getFileName5()))
				buf = fileUploadUtility.downloadFile(getImageServiceUrl() + "/download",
						notice.getNoticeName() + "_" + notice.getFileName5(), IMAGECONS.notice);
			return buf;
		} else {
			UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Error9"));
			return null;
		}
	}

	public void downloadDocument() {
		OutputStream servletOutputStream = null;
		HttpServletResponse response = null;
		FacesContext facesContext = null;
		try {
			if (fileName != null) {
				facesContext = FacesContext.getCurrentInstance();
				response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
				servletOutputStream = response.getOutputStream();
				response.setContentType("application/jpeg");
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
				servletOutputStream.write(getImageBytesAsImage());
			}
		} catch (IOException e) {
		} finally {
			try {
				if (servletOutputStream != null) {
					servletOutputStream.close();
					facesContext.responseComplete();
				}
			} catch (IOException e) {
			}
		}
	}

	public Boolean getAllValidated() {
		return allValidated;
	}

	public void setAllValidated(Boolean allValidated) {
		this.allValidated = allValidated;
	}

}
