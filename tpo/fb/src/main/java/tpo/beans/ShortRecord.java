/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.validator.ValidatorException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.admin.beans.EmailAttachmentBean;
import tpo.admin.excel.ExcelHandler;
import tpo.dao.CommonDBBean;
import tpo.email.EmailUtil;
import tpo.hibernate.Company;
import tpo.hibernate.HallTicket;
import tpo.hibernate.HallTicketConnect;
import tpo.hibernate.HallTicketConnectID;
import tpo.hibernate.Registration;
import tpo.hibernate.Shortlist;
import tpo.imageservice.client.FileUploadUtility;
import tpo.util.FbMessageUtil;
import tpo.util.FbResourceUtil;
import tpo.util.IMAGECONS;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("ShortRecord")
@Transactional(readOnly = true)
@Scope("request")
public class ShortRecord extends Parent {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private CommonDBBean commonDBBean;
	
	@Autowired
	private FileUploadUtility fileUploadUtility;

	private Logger logger = LoggerFactory.getLogger(ShortRecord.class);

	private List<String> companyList;

	private List<String> adminFromList;

	private List<String> adminToList;

	private List<String> selectedBranchList;

	private List<String> selectedCompanyList;

	private List<String> selectedAdminFromList;

	private List<String> selectedAdminToList;

	private String graduation;

	private String postGradustion;

	private Boolean excelReport = false;
	private Boolean excelReportEmail = false;

	private Boolean emailNotification = false;

	private Boolean allowSelected = false;

	private boolean checkBox;

	private String highSchool;

	private String higherSecondary;

	private String semester;

	private String firstSem;

	private String secondSem;

	private String thirdSem;

	private String fourthSem;

	private String fifthSem;

	private String sixthSem;

	private String seventhSem;

	private String eightSem;

	private String firstYear;

	private String secondYear;

	private String thirdYear;

	private String fourthYear;

	private String height;

	private String weight;

	private String glassPowerL;

	private String glassPowerR;

	private String yearOfPassing;

	private String collegeName;

	private String presentBacklog;

	private Date dateOfBirth;

	private String passMoreThenOne;

	private String pgFirst;

	private String pgSecond;

	private String pgThird;

	private String pgFourth;

	private String diplomaI;

	private String diplomaII;

	private String diplomaIII;

	private String diplomaIV;

	private String diplomaV;

	private String diplomaVI;

	private String diploma;

	private String currentCourse;

	private String course;

	private Short educationGap = 0;

	private String gender;

	private String handicapped;

	private String isBackListed;

	private Date dateOfVisit;

	private Date lastDateToApply;
	
	private boolean allowDigitalSignature;
	
	private String interviewLocation;
	
	private String postingLocation;
	
	private String role;
	

	private String time;

	private String packageOffering;

	private String studentMessage;

	@Autowired
	private EmailAttachmentBean emailAttachmentBean;

	private void sendXlsToEmail(byte[] b,String fileName) throws MessagingException {
		List<File> list = new ArrayList<>(1);
		File file = new File(fileName);
		try {

			OutputStream os = new FileOutputStream(file);
			os.write(b);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		list.add(file);
		/*
		 * try { List<File> fileList = TpoUtil.createTempFileList(emailAttachmentBean);
		 * if(fileList != null && fileList.size() >0){ list.addAll(fileList); } } catch
		 * (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */
		EmailUtil emailUtill = getEmailInstance();
		if (emailUtill != null) {
			String adminEmail = selectedAdminFromList.get(0);
			StringBuffer adminEmailTo = null;
			for (String item : selectedAdminToList) {
				if (adminEmailTo == null) {
					adminEmailTo = new StringBuffer();
					adminEmailTo.append(item);
				} else {
					adminEmailTo.append(",").append(item);
				}
			}
			emailUtill.sendEmailWithAttachment(adminEmail, adminEmailTo.toString(),
					"Please find attached shorted excel",
					FbMessageUtil.getLabel("Message_is_from_your_TPO_Admin_College"), list);
		}
	}

	private void sendEmailNotification(List<Registration> list, String subject)
			throws AddressException, MessagingException {
		EmailUtil emailUtill = getEmailInstance();
		if (emailUtill != null) {
			String adminEmail = null;
			if (selectedAdminFromList != null && selectedAdminFromList.size() > 0) {
				adminEmail = selectedAdminFromList.get(0);
			} else {
				adminEmail = TpoUtil.ADMIN_EMAIL;
			}
			StringBuffer comapanyEmail = null;
			if (selectedCompanyList != null && selectedCompanyList.size() > 0) {
				for (String item : selectedCompanyList) {
					if (comapanyEmail == null) {
						comapanyEmail = new StringBuffer();
						comapanyEmail.append(item);
					} else {
						comapanyEmail.append(",").append(item);
					}
				}
			} else {
				comapanyEmail = new StringBuffer(FbResourceUtil.getLabel("NA"));
			}

			StringBuffer buffer = new StringBuffer();
			for (Registration registration : list) {
				if (registration.getEmailVarified()) {
					if (registration != null && registration.getEmail() != null) {
						buffer.append(registration.getEmail()).append(",");
					}
				}
			}
			if (selectedAdminToList != null && selectedAdminToList.size() > 0) {
				for (String item : selectedAdminToList) {
					buffer.append(item).append(",");
				}
			}
			StringBuffer shortedInfo = new StringBuffer("<font color =green size= 4>" + FbMessageUtil.getLabel("Dear")
					+ "" + FbMessageUtil.getLabel("Congratulation") + ",<br></font>");
			CreateOpeninngBean createOpeninngBean = (CreateOpeninngBean) TpoUtil
					.getManagedBean(CreateOpeninngBean.class.getSimpleName());
			if (createOpeninngBean != null && !createOpeninngBean.getCreateOpeninngBool()) {
				subject = subject + " for " + comapanyEmail.toString() + ".";
			}
			shortedInfo.append(subject);
			shortedInfo.append("<br>");
			shortedInfo.append(FbMessageUtil.getLabel("Please_see_below_shorting_criteria"));
			shortedInfo.append(getCriteriaInfo());
			shortedInfo.append("</font>");
			shortedInfo.append(TpoUtil.getMesageString());
			if (emailAttachmentBean.getFileList() != null && emailAttachmentBean.getFileList().size() > 0) {
				shortedInfo.append(FbMessageUtil.getLabel("please_find_attachment"));
			}
			emailUtill.sendEmailWithAttachment(adminEmail, buffer.toString(), shortedInfo.toString(),
					FbMessageUtil.getLabel("Message_is_from_your_TPO_Admin_College"),
					emailAttachmentBean.getFileList());
			emailAttachmentBean.setFileList(null);
			/*
			 * emailUtill.sendEmail(adminEmail, buffer.toString(), shortedInfo.toString(),
			 * subject, Message.RecipientType.BCC);
			 */
		}

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void shortAllRecord() {
		try {
			CreateOpeninngBean createOpeninngBean = (CreateOpeninngBean) TpoUtil
					.getManagedBean(CreateOpeninngBean.class.getSimpleName());
			String companyName = null;
			int companyID = 0;
			if (createOpeninngBean != null && createOpeninngBean.getCreateOpeninngBool()) {
				if (selectedCompanyList != null && selectedCompanyList.size() == 0) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_at_least_one_company"));
					return;
				}

				if (selectedCompanyList != null && selectedCompanyList.size() == 1) {
					String[] ar= selectedCompanyList.get(0).split("#");
					companyID =Integer.valueOf(ar[0]);
					companyName =ar[1];
				} else {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_only_one_company"));
					return;
				}
			}

			if (excelReportEmail && !excelReport) {
				UIBackingBean.setErrorMessage(FbMessageUtil
						.getLabel("Please_select_Generate_Excel_Check_box_if_you_want_to_send_xls_as_an_email"));
				return;
			}

			if (emailNotification || excelReportEmail) {
				if (selectedAdminFromList != null && selectedAdminFromList.size() == 0) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error17));
					return;
				}
				if (selectedAdminFromList != null && selectedAdminFromList.size() > 1) {
					UIBackingBean
							.setErrorMessage(FbMessageUtil.getLabel("Please_select_only_one_email_in_from_email_list"));
					return;
				}
				if (selectedAdminToList != null && selectedAdminToList.size() == 0) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error16));
					return;
				}
			}

			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Registration.class);
			List<String> collegeList = null;
			AdminUser user = AdminUser.getUser();
			if (user != null) {
				collegeList = AdminUser.getUser().getCollegeList();
				if (collegeList != null && collegeList.size() > 0) {
					criteria.add(Restrictions.in("collegeName", collegeList));
				} else {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error2));
					return;
				}
			}
			addCriteria(criteria);
			List<Registration> list = criteria.list();
			if (list != null && list.size() != 0) {
				byte[] b = null;
				String reportName = null;
				String message = "";
				if (createOpeninngBean != null && createOpeninngBean.getCreateOpeninngBool()) {
					HallTicket hallticket = null;
					criteria = session.createCriteria(HallTicket.class);
					criteria.add(Restrictions.eq("companyID", companyID));
					criteria.add(Restrictions.eq("userName", AdminUser.getUser().getUserName()));
					criteria.add(Restrictions.eq("companyName", companyName));
					criteria.add(Restrictions.eq("date", dateOfVisit));
					hallticket = (HallTicket) criteria.uniqueResult();
					if (hallticket == null) {
						hallticket = new HallTicket();
						hallticket.setDate(dateOfVisit);
						hallticket.setLastDateToApply(lastDateToApply);
						hallticket.setInterviewLocation(interviewLocation);
						hallticket.setPostingLocation(postingLocation);
						hallticket.setRole(role);
						hallticket.setTime(time);
						hallticket.setCriteria(getCriteriaInfo());
						hallticket.setPackageOffering(packageOffering);
						hallticket.setIsActive(false);
						hallticket.setUserName(AdminUser.getUser().getUserName());
						hallticket.setCompanyID(companyID);
						hallticket.setCompanyName(companyName);
						hallticket.setHallTicketId(TpoUtil.getRandomNumber());
						hallticket.setAllowDigitalSignature(allowDigitalSignature);
						if(allowDigitalSignature) {
							hallticket.setDigitalSignature(TpoUtil.getDigitalSignature(hallticket.getHallTicketId(),companyName));
						}else {
							hallticket.setDigitalSignature(null);
						}
						HallTicketConnect hallTicketConnect = null;
						HallTicketConnectID id;
						session.save(hallticket);
						for (Registration registration : list) {
							hallTicketConnect = new HallTicketConnect();
							id = new HallTicketConnectID();
							id.setHallTicket(hallticket);
							id.setRollnumber(registration.getRollnumber());
							hallTicketConnect.setId(id);
							session.save(hallTicketConnect);
						}
						if (excelReport) {
							reportName = hallticket.getHallTicketId()+".xls";
							b = ExcelHandler.generateStudentList(list, reportName);
							createOpeninngBean.setXlsFileName(reportName);
							createOpeninngBean.setOpeningXls(true);
							fileUploadUtility.uploadFileWithByteArray(getFileServiceUrl() + "/upload", reportName,b,IMAGECONS.openingXls);
							message = message + FbMessageUtil.getLabel(ResourceID.Success4);
						}
						String param[] = new String[2];
						param[0] = String.valueOf(list.size());
						param[1] = message;
						UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Opening_has_been_created_and", param));
						if (emailNotification) {
							String param1[] = new String[4];
							param1[0] = packageOffering;
							param1[1] = String.valueOf(dateOfVisit);
							param1[2] = companyName;
							param1[3] = String.valueOf(lastDateToApply);
							;

							sendEmailNotification(list,
									FbMessageUtil.getLabel("Your_Record_has_been_shorted_Please_apply", param1));
						}
						// Sending Attachement
						if (excelReportEmail && b !=null) {
							sendXlsToEmail(b,reportName);
						}
						if(commonDBBean != null) {
							commonDBBean.loadOpennings();
						}
					} else {
						UIBackingBean
								.setErrorMessage(FbMessageUtil.getLabel("Opening_is_already_exist_for", companyName));
					}
				} else {
					if (excelReport) {
						reportName = "StudentList_" + AdminUser.getUser().getUserName();
						SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
						reportName = "FB_" + reportName + "_" + dateFormat.format(new Date());
						reportName += "_" + TpoUtil.get6DigitRandomNumber();
						b =ExcelHandler.generateStudentList(list, reportName);
						Shortlist shortlist = new Shortlist();
						shortlist.setFileName(reportName+".xls");
						createOpeninngBean.setXlsFileName(shortlist.getFileName());
						createOpeninngBean.setOpeningXls(false);
						fileUploadUtility.uploadFileWithByteArray(getFileServiceUrl() + "/upload", shortlist.getFileName(),b,IMAGECONS.shortlistedxls);
						
						shortlist.setCreatedBy(AdminUser.getUser().getUserName());
						Calendar calendar = Calendar.getInstance();
						shortlist.setDateOfShort(calendar.getTime());
						if (selectedCompanyList != null && selectedCompanyList.size() > 0) {
							StringBuffer comapanyName = null;
							for (String item : selectedCompanyList) {
								if (comapanyName == null) {
									comapanyName = new StringBuffer();
									comapanyName.append(item);
								} else {
									comapanyName.append(",").append(item);
								}
							}
							shortlist.setCompanyName(comapanyName.toString());
						}
						session.save(shortlist);
						message = message + FbMessageUtil.getLabel(ResourceID.Success4);
					}
					String param[] = new String[2];
					param[0] = String.valueOf(list.size());
					param[1] = message;
					UIBackingBean.setSuccessMessage(
							FbMessageUtil.getLabel("records_found_according_to_the_criteria", param));
					if (emailNotification) {
						sendEmailNotification(list, FbMessageUtil.getLabel("Your_Record_has_been_shorted"));
					}
					// Sending Attachement
					if (excelReportEmail) {
						sendXlsToEmail(b,reportName);
					}
				}
				list = null;
				//TpoUtil.renderEXcelFile(b, reportName);
				
			} else {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error2));
			}

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private String getCriteriaInfo() {
		StringBuffer shortedInfo = new StringBuffer();
		if (graduation != null && !"".equals(graduation)) {
			shortedInfo.append(FbResourceUtil.getLabel("Graduation_percent")).append("=").append(graduation)
					.append("<br>");
		}

		if (postGradustion != null && !"".equals(postGradustion)) {
			shortedInfo.append(FbResourceUtil.getLabel("Post_Graduation_percent")).append("=").append(postGradustion)
					.append("<br>");
		}

		if (highSchool != null && !"".equals(highSchool)) {
			shortedInfo.append(FbResourceUtil.getLabel("High_School")).append("=").append(highSchool).append("<br>");
		}

		if (higherSecondary != null && !"".equals(higherSecondary)) {
			shortedInfo.append(FbResourceUtil.getLabel("Higher_Secondary")).append("=").append(higherSecondary)
					.append("<br>");
		}

		if (semester != null && !"".equals(semester)) {
			shortedInfo.append(FbResourceUtil.getLabel("Semester")).append("=").append(semester)
					.append("<br>");
		}
		
		if (yearOfPassing != null && !"".equals(yearOfPassing)) {
			shortedInfo.append(FbResourceUtil.getLabel("Year_of_Passing")).append("=").append(yearOfPassing)
					.append("<br>");
		}

		if (glassPowerL != null && !"".equals(glassPowerL)) {
			shortedInfo.append(FbResourceUtil.getLabel("Glass_Power_L")).append("=").append(glassPowerL).append("<br>");
		}

		if (glassPowerR != null && !"".equals(glassPowerR)) {
			shortedInfo.append(FbResourceUtil.getLabel("Glass_Power_R")).append("=").append(glassPowerR).append("<br>");
		}
		
		if (dateOfBirth != null && !"".equals(dateOfBirth)) {
			shortedInfo.append(FbResourceUtil.getLabel("Date_of_Birth")).append("=").append(dateOfBirth).append("<br>")
					.append("<br>");
		}

		if (collegeName != null && !"".equals(collegeName)) {
			shortedInfo.append(FbResourceUtil.getLabel("College_Name")).append("=").append(collegeName).append("<br>");
		}
		
		if (diploma != null && !"".equals(diploma)) {
			shortedInfo.append(FbResourceUtil.getLabel("Diploma_percent")).append("=").append(diploma).append("<br>");
		}
		
		if (currentCourse != null && !"".equals(currentCourse)) {
			shortedInfo.append(FbResourceUtil.getLabel("Highest_Education")).append("=").append(currentCourse).append("<br>");
		}
		
		if (course != null && !"".equals(course)) {
			shortedInfo.append(FbResourceUtil.getLabel("Current_Course")).append("=").append(course).append("<br>");
		}
		
		if (isBackListed != null && !"".equals(isBackListed)) {
			shortedInfo.append(FbResourceUtil.getLabel("Is_Black_Listed_allowed")).append("=").append(isBackListed).append("<br>");
		}
		
		
		if (height != null && !"".equals(height)) {
			shortedInfo.append(FbResourceUtil.getLabel("Height")).append("=").append(height).append("<br>");
		}

		if (weight != null && !"".equals(weight)) {
			shortedInfo.append(FbResourceUtil.getLabel("Weight")).append("=").append(weight).append("<br>");
		}
		
		if (gender != null && !"".equals(gender)) {
			shortedInfo.append(FbResourceUtil.getLabel("Gender")).append("=").append(gender).append("<br>");
		}
		
		if (handicapped != null && !"".equals(handicapped)) {
			shortedInfo.append(FbResourceUtil.getLabel("Is_Handicapped_allowed")).append("=").append(handicapped).append("<br>");
		}

		if (presentBacklog != null && !"".equals(presentBacklog)) {
			shortedInfo.append(FbResourceUtil.getLabel("Present_Backlog")).append("=").append(presentBacklog)
					.append("<br>");
		}

		if (educationGap != null && !"".equals(educationGap)) {
			shortedInfo.append(FbResourceUtil.getLabel("Education_Gap_not_allowed_Years")).append(educationGap)
					.append("<br>");
		}

		if (passMoreThenOne != null && !"".equals(passMoreThenOne)) {
			shortedInfo.append(FbResourceUtil.getLabel("Any_semester_passed_in_more_than_one_attempt")).append("=")
					.append(passMoreThenOne).append("<br>");
		}

		
		if (diplomaI != null && !"".equals(diplomaI)) {
			shortedInfo.append(FbResourceUtil.getLabel("Diploma_I_Sem")).append("=").append(diplomaI)
					.append("<br>");
		}
		
		if (diplomaII != null && !"".equals(diplomaII)) {
			shortedInfo.append(FbResourceUtil.getLabel("Diploma_II_Sem")).append("=").append(diplomaII)
					.append("<br>");
		}
		
		if (diplomaIII != null && !"".equals(diplomaIII)) {
			shortedInfo.append(FbResourceUtil.getLabel("Diploma_III_Sem")).append("=").append(diplomaIII)
					.append("<br>");
		}
		
		if (diplomaIV != null && !"".equals(diplomaIV)) {
			shortedInfo.append(FbResourceUtil.getLabel("Diploma_IV_Sem")).append("=").append(diplomaIV)
					.append("<br>");
		}
		
		if (diplomaV != null && !"".equals(diplomaV)) {
			shortedInfo.append(FbResourceUtil.getLabel("Diploma_V_Sem")).append("=").append(diplomaV)
					.append("<br>");
		}
		
		if (diplomaVI != null && !"".equals(diplomaVI)) {
			shortedInfo.append(FbResourceUtil.getLabel("Diploma_VI_Sem")).append("=").append(diplomaVI)
					.append("<br>");
		}
		
		
		if (firstSem != null && !"".equals(firstSem)) {
			shortedInfo.append(FbResourceUtil.getLabel("Graduation_1_Semester")).append("=").append(firstSem)
					.append("<br>");
		}

		if (secondSem != null && !"".equals(secondSem)) {
			shortedInfo.append(FbResourceUtil.getLabel("Graduation_2_Semester")).append("=").append(secondSem)
					.append("<br>");
		}

		if (thirdSem != null && !"".equals(thirdSem)) {
			shortedInfo.append(FbResourceUtil.getLabel("Graduation_3_Semester")).append("=").append(thirdSem)
					.append("<br>");
		}

		if (fourthSem != null && !"".equals(fourthSem)) {
			shortedInfo.append(FbResourceUtil.getLabel("Graduation_4_Semester")).append("=").append(fourthSem)
					.append("<br>");
		}

		if (fifthSem != null && !"".equals(fifthSem)) {
			shortedInfo.append(FbResourceUtil.getLabel("Graduation_5_Semester")).append("=").append(fifthSem)
					.append("<br>");
		}

		if (sixthSem != null && !"".equals(sixthSem)) {
			shortedInfo.append(FbResourceUtil.getLabel("Graduation_6_Semester")).append("=").append(sixthSem)
					.append("<br>");
		}

		if (seventhSem != null && !"".equals(seventhSem)) {
			shortedInfo.append(FbResourceUtil.getLabel("Graduation_7_Semester")).append("=").append(seventhSem)
					.append("<br>");
		}

		if (eightSem != null && !"".equals(eightSem)) {
			shortedInfo.append(FbResourceUtil.getLabel("Graduation_8_Semester")).append("=").append(eightSem)
					.append("<br>");
		}

		if (firstYear != null && !"".equals(firstYear)) {
			shortedInfo.append(FbResourceUtil.getLabel("Graduation_First_Year")).append("=").append(firstYear)
					.append("<br>");
		}

		if (secondYear != null && !"".equals(secondYear)) {
			shortedInfo.append(FbResourceUtil.getLabel("Graduation_Second_Year")).append("=").append(secondYear)
					.append("<br>");
		}

		if (thirdYear != null && !"".equals(thirdYear)) {
			shortedInfo.append(FbResourceUtil.getLabel("Graduation_Third_Year")).append("=").append(thirdYear)
					.append("<br>");
		}

		if (fourthYear != null && !"".equals(fourthYear)) {
			shortedInfo.append(FbResourceUtil.getLabel("Graduation_Fourth_Year")).append("=").append(fourthYear)
					.append("<br>");
		}
		
		if (pgFirst != null && !"".equals(pgFirst)) {
			shortedInfo.append(FbResourceUtil.getLabel("PG_I_Sem")).append("=").append(pgFirst)
					.append("<br>");
		}
		
		if (pgSecond != null && !"".equals(pgSecond)) {
			shortedInfo.append(FbResourceUtil.getLabel("PG_II_Sem")).append("=").append(pgSecond)
					.append("<br>");
		}
		
		if (pgThird != null && !"".equals(pgThird)) {
			shortedInfo.append(FbResourceUtil.getLabel("PG_III_Sem")).append("=").append(pgThird)
					.append("<br>");
		}
		
		if (pgFourth != null && !"".equals(pgFourth)) {
			shortedInfo.append(FbResourceUtil.getLabel("PG_IV_Sem")).append("=").append(pgFourth)
					.append("<br>");
		}
		if(selectedBranchList !=null && selectedBranchList.size() >0) {
			shortedInfo.append(FbResourceUtil.getLabel("Branch_List")).append("=").append(TpoUtil.getComaSeprateValueWithOutQuotation(selectedBranchList));
			shortedInfo.append("<br>");
		}
	
		return shortedInfo.toString();
	}

	public void sendMessage() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Registration.class);
			List<String> collegeList = null;
			AdminUser user = AdminUser.getUser();
			if (user != null) {
				collegeList = AdminUser.getUser().getCollegeList();
				if (collegeList != null && collegeList.size() > 0) {
					criteria.add(Restrictions.in("collegeName", collegeList));
				} else {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error2));
					return;
				}
			}
			addCriteria(criteria);
			EmailUtil emailUtill = getEmailInstance();
			if (emailUtill != null) {
				int total = 0;
				int i = 0;
				StringBuffer wrongMessage = null;
				List<String> errorToSend = new ArrayList<>(1);
				if (selectedAdminFromList != null && selectedAdminFromList.size() > 0) {
					errorToSend.add(selectedAdminFromList.get(0));
					List<Registration> list = criteria.list();
					total = 0;
					if (list != null) {
						total = list.size();
					}
					i = 0;
					String emailAddress = null;
					for (Registration registration : list) {
						if (registration.getEmailVarified() && registration.getEmail() != null) {
							emailAddress = registration.getEmail();
							emailAddress = emailAddress.trim();
							emailAddress = emailAddress.replaceAll(" ", "");
							if (!validate(emailAddress)) {
								if (wrongMessage == null) {
									wrongMessage = new StringBuffer(FbMessageUtil.getLabel("Dear_Sir") + ",<br>"
											+ FbMessageUtil.getLabel("While_sending_the_E_mail"));
								}
								wrongMessage.append("<br><font color=red>").append(emailAddress).append("</font>");
							} else {
								List<String> recipients = new ArrayList<>(1);
								recipients.add(emailAddress);
								i++;
								StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear"));
								message.append(registration.getFirstName()).append(" ")
										.append(registration.getLastName()).append(",<br>");
								// t&p Message
								message.append("Below message is from your TPO/Admin/College<br><br>");
								message.append(
										"<font color=red>########################Message Starts here#########################</font>");
								message.append(studentMessage).append("<br><br>");
								message.append(
										"<font color=red>########################Message Ends here#########################</font><br>");
								message.append(TpoUtil.getMesageString());
								message.append("<br>");
								if (emailAttachmentBean.getFileList() != null
										&& emailAttachmentBean.getFileList().size() > 0) {
									message.append(FbMessageUtil.getLabel("please_find_attachment"));
								}
								emailUtill.sendEmailWithAttachment(TpoUtil.ADMIN_EMAIL, emailAddress,
										message.toString(),
										FbMessageUtil.getLabel("Message_is_from_your_TPO_Admin_College"),
										emailAttachmentBean.getFileList());
								emailAttachmentBean.setFileList(null);
							}
						}
					}
					if (wrongMessage != null && (errorToSend != null && errorToSend.size() > 0)) {
						String subject = FbMessageUtil.getLabel("Message_from_Freshers_Buddy_Wrong_E_mail_Address");
						wrongMessage.append(TpoUtil.getMesageString());
						emailUtill.postMail(errorToSend, subject, wrongMessage.toString(), TpoUtil.ADMIN_EMAIL,
								Message.RecipientType.TO);
					}
					Object param[] = new Object[2];
					param[0] = i;
					param[1] = total;
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Message_has_been_sent_to", param));
					list = null;
				}
			} else {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("E_mail_can_not_be_sent"));
			}

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private void addCriteria(Criteria criteria) {
		criteria.createAlias("personalinfo", "personalInfo");
		criteria.createAlias("percentageinfo", "percentageInfo");
		criteria.createAlias("contactinfo", "contactInfo");
		criteria.createAlias("backdetails", "backDetails");

		if (currentCourse != null && !currentCourse.equals("")) {
			criteria.add(Restrictions.eq("personalInfo.currentCourse", currentCourse));
		}
		if (course != null && !course.equals("")) {
			criteria.add(Restrictions.eq("personalInfo.course", course));
		}
		if (gender != null && !gender.equals("")) {
			criteria.add(Restrictions.eq("personalInfo.gender", gender));
		}
		if (handicapped != null && (!handicapped.equals("") && !handicapped.equals("YES"))) {
			criteria.add(Restrictions.eq("personalInfo.handicapped", handicapped));
		}
		if (semester != null && !semester.equals("")) {
			criteria.add(Restrictions.eq("personalInfo.semester", semester));
		}
		if (selectedBranchList != null && selectedBranchList.size() > 0) {
			criteria.add(Restrictions.in("personalInfo.branch", selectedBranchList));
		}
		if (dateOfBirth != null && !dateOfBirth.equals("")) {
			criteria.add(Restrictions.ge("personalInfo.dob", dateOfBirth));
		}
		if (yearOfPassing != null && !yearOfPassing.equalsIgnoreCase("")) {
			criteria.add(Restrictions.eq("personalInfo.yearOfPassing", Integer.valueOf(yearOfPassing)));
		}
		if (collegeName != null && !collegeName.equals("")) {
			criteria.add(Restrictions.eq("collegeName", collegeName));
		}
		if (allowSelected == false) {
			criteria.add(Restrictions.eq("personalInfo.companyName", ""));
		}

		if (graduation != null && !graduation.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.beAverege", new Double(graduation)));
		}
		if (postGradustion != null && !postGradustion.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.meAverage", new Double(postGradustion)));
		}
		if (highSchool != null && !highSchool.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.highSchoolPercent", new Double(highSchool)));
		}
		if (higherSecondary != null && !higherSecondary.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.higherSecondarypercent", new Double(higherSecondary)));
		}
		if (firstSem != null && !firstSem.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.be1sem", new Double(firstSem)));
		}
		if (secondSem != null && !secondSem.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.be2sem", new Double(secondSem)));
		}
		if (thirdSem != null && !thirdSem.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.be3sem", new Double(thirdSem)));
		}
		if (fourthSem != null && !fourthSem.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.be4sem", new Double(fourthSem)));
		}
		if (fifthSem != null && !fifthSem.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.be5sem", new Double(fifthSem)));
		}
		if (sixthSem != null && !sixthSem.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.be6sem", new Double(sixthSem)));
		}
		if (seventhSem != null && !seventhSem.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.be7sem", new Double(seventhSem)));
		}
		if (eightSem != null && !eightSem.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.be8sem", new Double(eightSem)));
		}
		if (firstYear != null && !firstYear.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.avgbe1year", new Double(firstYear)));
		}
		if (secondYear != null && !secondYear.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.avgbe2year", new Double(secondYear)));
		}
		if (thirdYear != null && !thirdYear.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.avgbe3year", new Double(thirdYear)));
		}
		if (fourthYear != null && !fourthYear.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.avgbe4year", new Double(fourthYear)));
		}

		if (pgFirst != null && !pgFirst.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.meBsc1sem", new Double(pgFirst)));
		}
		if (pgSecond != null && !pgSecond.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.meBsc2sem", new Double(pgSecond)));
		}
		if (pgThird != null && !pgThird.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.meBsc3sem", new Double(pgThird)));
		}
		if (pgFourth != null && !pgFourth.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.meBsc4sem", new Double(pgFourth)));
		}
		if (diplomaI != null && !diplomaI.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.diploma1Sem", new Double(diplomaI)));
		}
		if (diplomaII != null && !diplomaII.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.diploma2Sem", new Double(diplomaII)));
		}
		if (diplomaIII != null && !diplomaIII.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.diploma3Sem", new Double(diplomaIII)));
		}
		if (diplomaIV != null && !diplomaIV.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.diploma4Sem", new Double(diplomaIV)));
		}
		if (diplomaV != null && !diplomaV.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.diploma5Sem", new Double(diplomaV)));
		}
		if (diplomaVI != null && !diplomaVI.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.diploma6Sem", new Double(diplomaVI)));
		}
		if (diploma != null && !diploma.equals("")) {
			criteria.add(Restrictions.ge("percentageInfo.diplomaOthers", new Double(diploma)));
		}
		if (height != null && !height.equals("")) {
			criteria.add(Restrictions.ge("contactInfo.hieght", new Double(height)));
		}
		if (weight != null && !weight.equals("")) {
			criteria.add(Restrictions.ge("contactInfo.weight", new Integer(weight)));
		}
		if (glassPowerL != null && !glassPowerL.equals("")) {
			criteria.add(Restrictions.le("contactInfo.glassPowerLeft", glassPowerL));
		}
		if (glassPowerR != null && !glassPowerR.equals("")) {
			criteria.add(Restrictions.le("contactInfo.glassPowerRight", glassPowerR));
		}
		if (presentBacklog != null && !presentBacklog.equals("")) {
			criteria.add(Restrictions.eq("backDetails.backLog", new Integer(presentBacklog)));
		}
		if (educationGap != null && !String.valueOf(educationGap).equals("0")) {
			criteria.add(Restrictions.lt("backDetails.educationGap", educationGap));
		}
		if (passMoreThenOne != null && !passMoreThenOne.equals("")) {
			criteria.add(Restrictions.eq("backDetails.passMoreThenOneAttempt", new Integer(passMoreThenOne)));
		}
		if (isBackListed != null && (!isBackListed.equals("") && !isBackListed.equals("YES"))) {
			criteria.add(Restrictions.eq("backDetails.blackList", false));
		}
		criteria.add(Restrictions.eq("approved", true));
	}

	
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return the branchFirstList
	 */
	public List<String> getBranchList() {
		List<String> list = commonDBBean.getCommonData("Branch");
		return list;
	}

	/**
	 * @return the adminFromListFirst
	 */
	public List<String> getAdminFromList() {
		if (adminFromList == null) {
			
			adminFromList =  commonDBBean.getCommonData("AdminEmailFROM");
		}
		return adminFromList;
	}

	/**
	 * @return the adminToListFirst
	 */
	public List<String> getAdminToList() {
		if (adminToList == null) {
			adminToList = commonDBBean.getCommonData("AdminEmailTO");
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Company.class).setProjection(Projections.property("email"));
			criteria.add(Restrictions.isNotNull("email"));
			criteria.add(Restrictions.eq("createdBy", AdminUser.getUser().getUserName()));
			adminToList.addAll(criteria.list());
		}
		return adminToList;
	}

	/**
	 * @return the companyFirstList
	 */
	public List<String> getCompanyList() {
		if (companyList == null) {
			CommonDBBean bean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
			companyList = bean.getCampusListByUserName(AdminUser.getUser().getUserName());
		}
		return companyList;
	}

	/**
	 * @return the highSchool
	 */
	public String getHighSchool() {
		return highSchool;
	}

	/**
	 * @param highSchool the highSchool to set
	 */
	public void setHighSchool(String highSchool) {
		this.highSchool = highSchool;
	}

	/**
	 * @return the higherSecondary
	 */
	public String getHigherSecondary() {
		return higherSecondary;
	}

	/**
	 * @param higherSecondary the higherSecondary to set
	 */
	public void setHigherSecondary(String higherSecondary) {
		this.higherSecondary = higherSecondary;
	}

	/**
	 * @return the firstSem
	 */
	public String getFirstSem() {
		return firstSem;
	}

	/**
	 * @param firstSem the firstSem to set
	 */
	public void setFirstSem(String firstSem) {
		this.firstSem = firstSem;
	}

	/**
	 * @return the secondSem
	 */
	public String getSecondSem() {
		return secondSem;
	}

	/**
	 * @param secondSem the secondSem to set
	 */
	public void setSecondSem(String secondSem) {
		this.secondSem = secondSem;
	}

	/**
	 * @return the thirdSem
	 */
	public String getThirdSem() {
		return thirdSem;
	}

	/**
	 * @param thirdSem the thirdSem to set
	 */
	public void setThirdSem(String thirdSem) {
		this.thirdSem = thirdSem;
	}

	/**
	 * @return the fourthSem
	 */
	public String getFourthSem() {
		return fourthSem;
	}

	/**
	 * @param fourthSem the fourthSem to set
	 */
	public void setFourthSem(String fourthSem) {
		this.fourthSem = fourthSem;
	}

	/**
	 * @return the fifthSem
	 */
	public String getFifthSem() {
		return fifthSem;
	}

	/**
	 * @param fifthSem the fifthSem to set
	 */
	public void setFifthSem(String fifthSem) {
		this.fifthSem = fifthSem;
	}

	/**
	 * @return the sixthSem
	 */
	public String getSixthSem() {
		return sixthSem;
	}

	/**
	 * @param sixthSem the sixthSem to set
	 */
	public void setSixthSem(String sixthSem) {
		this.sixthSem = sixthSem;
	}

	/**
	 * @return the seventhSem
	 */
	public String getSeventhSem() {
		return seventhSem;
	}

	/**
	 * @param seventhSem the seventhSem to set
	 */
	public void setSeventhSem(String seventhSem) {
		this.seventhSem = seventhSem;
	}

	/**
	 * @return the eightSem
	 */
	public String getEightSem() {
		return eightSem;
	}

	/**
	 * @param eightSem the eightSem to set
	 */
	public void setEightSem(String eightSem) {
		this.eightSem = eightSem;
	}

	/**
	 * @return the firstYear
	 */
	public String getFirstYear() {
		return firstYear;
	}

	/**
	 * @param firstYear the firstYear to set
	 */
	public void setFirstYear(String firstYear) {
		this.firstYear = firstYear;
	}

	/**
	 * @return the secondYear
	 */
	public String getSecondYear() {
		return secondYear;
	}

	/**
	 * @param secondYear the secondYear to set
	 */
	public void setSecondYear(String secondYear) {
		this.secondYear = secondYear;
	}

	/**
	 * @return the thirdYear
	 */
	public String getThirdYear() {
		return thirdYear;
	}

	/**
	 * @param thirdYear the thirdYear to set
	 */
	public void setThirdYear(String thirdYear) {
		this.thirdYear = thirdYear;
	}

	/**
	 * @return the fourthYear
	 */
	public String getFourthYear() {
		return fourthYear;
	}

	/**
	 * @param fourthYear the fourthYear to set
	 */
	public void setFourthYear(String fourthYear) {
		this.fourthYear = fourthYear;
	}

	/**
	 * @return the height
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @return the weight
	 */
	public String getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(String weight) {
		this.weight = weight;
	}

	/**
	 * @return the glassPowerL
	 */
	public String getGlassPowerL() {
		return glassPowerL;
	}

	/**
	 * @param glassPowerL the glassPowerL to set
	 */
	public void setGlassPowerL(String glassPowerL) {
		this.glassPowerL = glassPowerL;
	}

	/**
	 * @return the glassPowerR
	 */
	public String getGlassPowerR() {
		return glassPowerR;
	}

	/**
	 * @param glassPowerR the glassPowerR to set
	 */
	public void setGlassPowerR(String glassPowerR) {
		this.glassPowerR = glassPowerR;
	}

	/**
	 * @return the yearOfPassing
	 */
	public String getYearOfPassing() {
		return yearOfPassing;
	}

	/**
	 * @param yearOfPassing the yearOfPassing to set
	 */
	public void setYearOfPassing(String yearOfPassing) {
		this.yearOfPassing = yearOfPassing;
	}

	/**
	 * @return the collegeName
	 */
	public String getCollegeName() {
		return collegeName;
	}

	/**
	 * @param collegeName the collegeName to set
	 */
	public void setCollegeName(String collegeName) {
		this.collegeName = collegeName;
	}

	/**
	 * @return the presentBacklog
	 */
	public String getPresentBacklog() {
		return presentBacklog;
	}

	/**
	 * @param presentBacklog the presentBacklog to set
	 */
	public void setPresentBacklog(String presentBacklog) {
		this.presentBacklog = presentBacklog;
	}

	/**
	 * @return the passMoreThenOne
	 */
	public String getPassMoreThenOne() {
		return passMoreThenOne;
	}

	/**
	 * @param passMoreThenOne the passMoreThenOne to set
	 */
	public void setPassMoreThenOne(String passMoreThenOne) {
		this.passMoreThenOne = passMoreThenOne;
	}

	/**
	 * @return the excelReport
	 */
	public Boolean getExcelReport() {
		return excelReport;
	}

	/**
	 * @param excelReport the excelReport to set
	 */
	public void setExcelReport(Boolean excelReport) {
		this.excelReport = excelReport;
	}

	/**
	 * @return the emailNotification
	 */
	public Boolean getEmailNotification() {
		return emailNotification;
	}

	/**
	 * @param emailNotification the emailNotification to set
	 */
	public void setEmailNotification(Boolean emailNotification) {
		this.emailNotification = emailNotification;
	}

	/**
	 * @return the allowSelected
	 */
	public Boolean getAllowSelected() {
		return allowSelected;
	}

	/**
	 * @param allowSelected the allowSelected to set
	 */
	public void setAllowSelected(Boolean allowSelected) {
		this.allowSelected = allowSelected;
	}

	/**
	 * @return the semester
	 */
	public String getSemester() {
		return semester;
	}

	/**
	 * @param semester the semester to set
	 */
	public void setSemester(String semester) {
		this.semester = semester;
	}

	/**
	 * @return the graduation
	 */
	public String getGraduation() {
		return graduation;
	}

	/**
	 * @param graduation the graduation to set
	 */
	public void setGraduation(String graduation) {
		this.graduation = graduation;
	}

	/**
	 * @return the postGradustion
	 */
	public String getPostGradustion() {
		return postGradustion;
	}

	/**
	 * @param postGradustion the postGradustion to set
	 */
	public void setPostGradustion(String postGradustion) {
		this.postGradustion = postGradustion;
	}

	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @param logger the logger to set
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @return the pgFirst
	 */
	public String getPgFirst() {
		return pgFirst;
	}

	/**
	 * @param pgFirst the pgFirst to set
	 */
	public void setPgFirst(String pgFirst) {
		this.pgFirst = pgFirst;
	}

	/**
	 * @return the pgSecond
	 */
	public String getPgSecond() {
		return pgSecond;
	}

	/**
	 * @param pgSecond the pgSecond to set
	 */
	public void setPgSecond(String pgSecond) {
		this.pgSecond = pgSecond;
	}

	/**
	 * @return the pgThird
	 */
	public String getPgThird() {
		return pgThird;
	}

	/**
	 * @param pgThird the pgThird to set
	 */
	public void setPgThird(String pgThird) {
		this.pgThird = pgThird;
	}

	/**
	 * @return the pgFourth
	 */
	public String getPgFourth() {
		return pgFourth;
	}

	/**
	 * @param pgFourth the pgFourth to set
	 */
	public void setPgFourth(String pgFourth) {
		this.pgFourth = pgFourth;
	}

	/**
	 * @return the diplomaI
	 */
	public String getDiplomaI() {
		return diplomaI;
	}

	/**
	 * @param diplomaI the diplomaI to set
	 */
	public void setDiplomaI(String diplomaI) {
		this.diplomaI = diplomaI;
	}

	/**
	 * @return the diplomaII
	 */
	public String getDiplomaII() {
		return diplomaII;
	}

	/**
	 * @param diplomaII the diplomaII to set
	 */
	public void setDiplomaII(String diplomaII) {
		this.diplomaII = diplomaII;
	}

	/**
	 * @return the diplomaIII
	 */
	public String getDiplomaIII() {
		return diplomaIII;
	}

	/**
	 * @param diplomaIII the diplomaIII to set
	 */
	public void setDiplomaIII(String diplomaIII) {
		this.diplomaIII = diplomaIII;
	}

	/**
	 * @return the diplomaIV
	 */
	public String getDiplomaIV() {
		return diplomaIV;
	}

	/**
	 * @param diplomaIV the diplomaIV to set
	 */
	public void setDiplomaIV(String diplomaIV) {
		this.diplomaIV = diplomaIV;
	}

	/**
	 * @return the diplomaV
	 */
	public String getDiplomaV() {
		return diplomaV;
	}

	/**
	 * @param diplomaV the diplomaV to set
	 */
	public void setDiplomaV(String diplomaV) {
		this.diplomaV = diplomaV;
	}

	/**
	 * @return the diplomaVI
	 */
	public String getDiplomaVI() {
		return diplomaVI;
	}

	/**
	 * @param diplomaVI the diplomaVI to set
	 */
	public void setDiplomaVI(String diplomaVI) {
		this.diplomaVI = diplomaVI;
	}

	/**
	 * @return the diploma
	 */
	public String getDiploma() {
		return diploma;
	}

	/**
	 * @param diploma the diploma to set
	 */
	public void setDiploma(String diploma) {
		this.diploma = diploma;
	}

	/**
	 * @return the currentCourse
	 */
	public String getCurrentCourse() {
		return currentCourse;
	}

	/**
	 * @param currentCourse the currentCourse to set
	 */
	public void setCurrentCourse(String currentCourse) {
		this.currentCourse = currentCourse;
	}

	/**
	 * @return the educationGap
	 */
	public Short getEducationGap() {
		return educationGap;
	}

	/**
	 * @param educationGap the educationGap to set
	 */
	public void setEducationGap(Short educationGap) {
		this.educationGap = educationGap;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getHandicapped() {
		return handicapped;
	}

	public void setHandicapped(String handicapped) {
		this.handicapped = handicapped;
	}

	public Date getDateOfVisit() {
		return dateOfVisit;
	}

	public void setDateOfVisit(Date dateOfVisit) {
		this.dateOfVisit = dateOfVisit;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPackageOffering() {
		return packageOffering;
	}

	public void setPackageOffering(String packageOffering) {
		this.packageOffering = packageOffering;
	}

	public boolean isCheckBox() {
		return checkBox;
	}

	public void setCheckBox(boolean checkBox) {
		this.checkBox = checkBox;
	}

	public List<String> getSelectedBranchList() {
		return selectedBranchList;
	}

	public void setSelectedBranchList(List<String> selectedBranchList) {
		this.selectedBranchList = selectedBranchList;
	}

	public List<String> getSelectedCompanyList() {
		return selectedCompanyList;
	}

	public void setSelectedCompanyList(List<String> selectedCompanyList) {
		this.selectedCompanyList = selectedCompanyList;
	}

	public List<String> getSelectedAdminFromList() {
		return selectedAdminFromList;
	}

	public void setSelectedAdminFromList(List<String> selectedAdminFromList) {
		this.selectedAdminFromList = selectedAdminFromList;
	}

	public List<String> getSelectedAdminToList() {
		return selectedAdminToList;
	}

	public void setSelectedAdminToList(List<String> selectedAdminToList) {
		this.selectedAdminToList = selectedAdminToList;
	}

	public Boolean getExcelReportEmail() {
		return excelReportEmail;
	}

	public void setExcelReportEmail(Boolean excelReportEmail) {
		this.excelReportEmail = excelReportEmail;
	}

	public Boolean validate(String email) throws ValidatorException {
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher m = p.matcher(email);
		boolean matchFound = m.matches();
		return matchFound;
	}

	public String getStudentMessage() {
		return studentMessage;
	}

	public void setStudentMessage(String studentMessage) {
		this.studentMessage = studentMessage;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getIsBackListed() {
		return isBackListed;
	}

	public void setIsBackListed(String isBackListed) {
		this.isBackListed = isBackListed;
	}

	public EmailAttachmentBean getEmailAttachmentBean() {
		return emailAttachmentBean;
	}

	public void setEmailAttachmentBean(EmailAttachmentBean emailAttachmentBean) {
		this.emailAttachmentBean = emailAttachmentBean;
	}

	public Date getLastDateToApply() {
		return lastDateToApply;
	}

	public void setLastDateToApply(Date lastDateToApply) {
		this.lastDateToApply = lastDateToApply;
	}

	public boolean isAllowDigitalSignature() {
		return allowDigitalSignature;
	}

	public void setAllowDigitalSignature(boolean allowDigitalSignature) {
		this.allowDigitalSignature = allowDigitalSignature;
	}

	public String getInterviewLocation() {
		return interviewLocation;
	}

	public void setInterviewLocation(String interviewLocation) {
		this.interviewLocation = interviewLocation;
	}

	public String getPostingLocation() {
		return postingLocation;
	}

	public void setPostingLocation(String postingLocation) {
		this.postingLocation = postingLocation;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	

}
