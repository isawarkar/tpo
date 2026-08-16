package com.dao;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

import com.annotation.CommonData;
import com.annotation.DocumentList;
import com.annotation.StudentFeeDetails;
import com.beans.FileUploadUtility;
import com.beans.Parent;
import com.beans.Student;
import com.beans.UIBackingBean;
import com.email.EmailUtil;
import com.hibernate.College;
import com.hibernate.Company;
import com.hibernate.Contactinfo;
import com.hibernate.Exam;
import com.hibernate.HallTicket;
import com.hibernate.HallTicketConnect;
import com.hibernate.HallTicketConnectID;
import com.hibernate.Notice;
import com.hibernate.Personalinfo;
import com.hibernate.Registration;
import com.hibernate.Result;
import com.hibernate.ResultId;
import com.hibernate.Userdetails;
import com.util.CCPConstant;
import com.util.Encryption;
import com.util.FbMessageUtil;
import com.util.IMAGECONS;
import com.util.TpoUtil;

@Repository("CommonDBBean")
@Transactional(readOnly = true)
public class CommonDBBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(CommonDBBean.class);

	private String oldPassword;
	private String confirmPassword;
	
	private List<Company> cList;


	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	FileUploadUtility fileUploadUtility;


	public String getUserNameByCollegeName(String collegeName) {
		Session session = sessionFactory.getCurrentSession();
		NativeQuery<?> collegeQ = session
				.createSQLQuery("SELECT userName FROM college  where CollegeName = '" + collegeName + "'");
		String userName = (String) collegeQ.uniqueResult();
		return userName;
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

	public Registration getRegistration(String enrollmentNo) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Registration.class);
		criteria.add(Restrictions.eq("rollnumber", enrollmentNo));
		return (Registration) criteria.uniqueResult();
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

	
	public byte[] getStudentProfilePic(String enrollmentNo) {
		byte[] buf = null;
		String fileName = enrollmentNo + ".png";
		if (fileName != null) {
			buf = fileUploadUtility.downloadFileWithParam(getImageServiceUrl() + "/downloadImage", fileName,
					IMAGECONS.student.toString() + enrollmentNo);
		}
		return buf;
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean uploadStudentProfilePic(String envNO, byte[] image) {
		try {
			if (image != null && envNO != null) {
				
				String response = fileUploadUtility.uploadFileWithByteArrayWithExt(getImageServiceUrl() + "/uploadWithExt", envNO + ".png",
						image, IMAGECONS.student.toString() + envNO);
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
				
				if(personalinfo.getResume() != null && !"".equals(personalinfo.getResume()) ) {
				fileUploadUtility.deleteFileWithParam(getFileServiceUrl() + "/delete",
						IMAGECONS.student.toString() + registration.getRollnumber() +"/"+ IMAGECONS.resume.toString(), personalinfo.getResume());
				}
				personalinfo.setResume(fileName);
				personalinfo.setResumeType(contentType);
				fileUploadUtility.uploadFileWithByteArrayWithExt(getFileServiceUrl() + "/upload", fileName, resume,
						IMAGECONS.student.toString() + registration.getRollnumber() +"/"+ IMAGECONS.resume.toString());
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
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updatePassword() {
		try {
			String encryptedPassword = Encryption.getEncryptedString(oldPassword);
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Registration.class);
			criteria.add(Restrictions.eq("rollnumber", Student.getStudent().getRollNumber()));
			criteria.add(Restrictions.eq("password", encryptedPassword));
			Registration registration = (Registration) criteria.uniqueResult();
			if (registration == null) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_enter_valid_old_Password"));
			} else {
				registration.setPassword(Encryption.getEncryptedString(confirmPassword));
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
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
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
	public void updateCertStatus(Result result) {
		if (!CCPConstant.Disqualified.equals(result.getResult())) {
			
			String certFileName = "Certificate_" + result.getTestName() + "_" + result.getId().getLoginname() + "_"
					+ result.getTotalnumbers() + ".pdf";
			if (fileUploadUtility.isFileExist(getFileServiceUrl() + "/fileExist", certFileName,
					IMAGECONS.student.toString() + result.getId().getLoginname()+"/" + IMAGECONS.certificate.toString())) {
				result.setCertificateAvialable(true);
			} else {
				result.setCertificateAvialable(false);
			}
		}
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

	public List<Company> getcList() {
		return cList;
	}

	public void setcList(List<Company> cList) {
		this.cList = cList;
	}
	
	public byte[] getCompanyePic(String companyId) {
		byte[] buf = null;
		companyId = companyId + ".png";
		if (companyId != null) {
			
			buf = fileUploadUtility.downloadFileWithParam(getImageServiceUrl() + "/downloadImage", companyId, IMAGECONS.company.toString());
		}
		return buf;
	}
	
	
	// FresherBuddyService code start
	
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
	
	public List<HallTicket> getOpeningList(String enrollmentNo) {
		List<HallTicket> list = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(HallTicket.class);
			NativeQuery<?> query = getCreatedBy(enrollmentNo, session);
			String createdBy = (String) query.uniqueResult();
			List<String> createdByList = new ArrayList<String>(2);
			createdByList.add(createdBy);
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
	
	private NativeQuery<?> getCreatedBy(String enNo, Session session) {
		NativeQuery<?> query = session.createSQLQuery(
				"select userName from college where collegeName = '" + getRegistration(enNo).getCollegeName() + "'");
		return query;
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
			createdByList.add(supperUser);
			createdByList.add(enNo);
		}
		criteria.add(Restrictions.in("createdBy", createdByList));
		criteria.add(Restrictions.eq("active", true));
		List<Notice> nList = criteria.list();
		return nList;
	}
	
	public List<HallTicketConnect> getHallTicketList(String rollNumber) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(HallTicketConnect.class);
		criteria.add(Restrictions.eq("id.rollnumber", rollNumber));
	    List<HallTicketConnect> hallTicketList = criteria.list();
		return hallTicketList;
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
	
	public HallTicket getHallTicket(Integer hallTicketId) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(HallTicket.class);
		criteria.add(Restrictions.eq("hallTicketId", hallTicketId));
		return (HallTicket) criteria.uniqueResult();
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
}
