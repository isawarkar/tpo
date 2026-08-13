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
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlInputHidden;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.openfaces.component.chart.ChartModel;
import org.openfaces.component.chart.PlainModel;
import org.openfaces.component.chart.PlainSeries;
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
import tpo.email.EmailUtil;
import tpo.hibernate.HallTicket;
import tpo.hibernate.HallTicketConnect;
import tpo.hibernate.HallTicketConnectID;
import tpo.hibernate.Registration;
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
@Repository("OpenningListTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class OpenningListTableBean extends Parent {

	public String companyName;

	public String chartCompanyName;

	private Logger logger = LoggerFactory.getLogger(OpenningListTableBean.class);

	@Autowired
	private SessionFactory sessionFactory;

	private Integer hallTicketId;

	private String hallTicketIdString;

	@Autowired
	private Pagination pagination;

	@Autowired
	private PDFGenerator pDFGenerator;

	@Autowired
	private FileUploadUtility fileUploadUtility;

	private List<HallTicket> hallTicketList;
	private List<HallTicket> selectedList = new ArrayList<HallTicket>();

	private HallTicket hallTicket;

	private List<HallTicketConnect> hallTicketConnectList;
	private List<HallTicketConnect> hallTicketConnectSelectedList = new ArrayList<HallTicketConnect>();

	private String rollnumber;

	private Integer hallticketID;

	private String applied;

	private String approved;

	public PieSectorInfoImpl pieSelectedCategory;

	public PieSectorInfoImpl pieSelectedCategoryChild;

	private String criteria;

	@Autowired
	private CommonDBBean commonDBBean;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSeletedRecord() {
		try {
			if (selectedList != null && selectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (HallTicket hallTicket : selectedList) {
					NativeQuery<?> query = session.createSQLQuery(
							"delete FROM hallticketconnect where hallTicketId = " + hallTicket.getHallTicketId() + "");
					query.executeUpdate();
					session.delete(hallTicket);
				}
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success12));
				hallTicketList = null;
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
	public void approve() {
		try {
			if (hallTicketConnectSelectedList != null && hallTicketConnectSelectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_at_least_one_record_to_Approve"));
			} else {
				sendEmail(true);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Sucessfully_Approved"));
				hallTicketConnectSelectedList = null;
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
	public void updateOpenning() {
		try {
			Session session = sessionFactory.getCurrentSession();
			session.update(hallTicket);
			UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("openning_updated"));
			hallTicket = null;
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void reject() {
		try {
			if (hallTicketConnectSelectedList != null && hallTicketConnectSelectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_at_least_one_record_to_Reject"));
			} else {
				sendEmail(false);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Sucessfully_Rejected"));
				hallTicketConnectSelectedList = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private void sendEmail(boolean status) throws MessagingException {
		EmailUtil emailUtill = getEmailInstance();
		StringBuffer emailMessage;
		String subject;
		Session session = sessionFactory.getCurrentSession();
		for (HallTicketConnect hallTicketConnect : hallTicketConnectSelectedList) {
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
			}
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void changeStatusToActive() {
		try {
			if (selectedList != null && selectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error13));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (HallTicket hallTicket : selectedList) {
					hallTicket.setIsActive(true);
					session.update(hallTicket);
				}
				UIBackingBean
						.setSuccessMessage(FbMessageUtil.getLabel("Status_has_been_chnaged_to_active_successfully"));
				hallTicketList = null;
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
	public void changeStatusToInActive() {
		try {
			if (selectedList != null && selectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error13));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (HallTicket hallTicket : selectedList) {
					hallTicket.setIsActive(false);
					session.update(hallTicket);
				}
				UIBackingBean
						.setSuccessMessage(FbMessageUtil.getLabel("Status_has_been_chnaged_to_In_active_successfully"));
				hallTicketList = null;
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
	}

	public void search() {
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
		pieSelectedCategory = null;
		pieSelectedCategoryChild = null;
	}

	public List<HallTicketConnect> getHallTicketConnectList() {
		return hallTicketConnectList;
	}

	public List<HallTicket> getHallTicketList() {
		return hallTicketList;
	}

	public void setHallTicketList(List<HallTicket> hallTicketList) {
		this.hallTicketList = hallTicketList;
	}

	public List<HallTicket> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(List<HallTicket> selectedList) {
		this.selectedList = selectedList;
	}

	public String getRollnumber() {
		return rollnumber;
	}

	public void setRollnumber(String rollnumber) {
		this.rollnumber = rollnumber;
	}

	public Integer getHallticketID() {
		return hallticketID;
	}

	public void setHallticketID(Integer hallticketID) {
		this.hallticketID = hallticketID;
	}

	public List<HallTicketConnect> getHallTicketConnectSelectedList() {
		return hallTicketConnectSelectedList;
	}

	public void setHallTicketConnectSelectedList(List<HallTicketConnect> hallTicketConnectSelectedList) {
		this.hallTicketConnectSelectedList = hallTicketConnectSelectedList;
	}

	public String getApplied() {
		return applied;
	}

	public void setApplied(String applied) {
		this.applied = applied;
	}

	public void inIt() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(HallTicket.class);

			List<String> userList = AdminUser.getUser().getUserList();
			String queryStr = "select count(hallTicketId) from hallticket where 1=1";
			criteria.add(Restrictions.in("userName", userList));
			if (companyName != null && !"".equals(companyName)) {
				if (companyName.contains("#")) {
					String cName = companyName.split("#")[1];
					criteria.add(Restrictions.ilike("companyName", cName + "%"));
					queryStr = queryStr + " and companyName like '" + cName + "%' ";
				} else {
					criteria.add(Restrictions.ilike("companyName", companyName + "%"));
					queryStr = queryStr + " and companyName like '" + companyName + "%' ";
				}
			}
			queryStr = queryStr + " and  userName in (" + TpoUtil.getComaSeprateValue(userList) + ")";

			NativeQuery<BigInteger> query = session.createSQLQuery(queryStr);
			criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			hallTicketList = criteria.list();
			if (hallTicketList != null && hallTicketList.isEmpty()) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("no_data"));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void inItHall() {
		try {

			if (pieSelectedCategory != null) {
				String str[] = pieSelectedCategory.getKey().toString().split("\\(");
				if (str.length == 2) {
					String str1[] = str[1].split("\\)");
					hallticketID = new Integer(str1[0]);
				}
			}
			if (pieSelectedCategoryChild != null) {
				String condition = pieSelectedCategoryChild.getKey().toString();
				if (!FbResourceUtil.getLabel("Total").equals(condition)) {
					if (FbResourceUtil.getLabel("Applied").equals(condition)) {
						applied = "true";
						approved = null;
					} else if (FbResourceUtil.getLabel("Approved").equals(condition)) {
						applied = null;
						approved = "true";
					}
				}
			}
			if (hallTicketIdString != null) {
				if ("All".equals(hallTicketIdString)) {
					hallticketID = -1111;
				} else {
					String[] arr = hallTicketIdString.split("-");
					if (arr != null && arr.length == 1) {
						hallticketID = Integer.valueOf(hallTicketIdString.split("-")[0]);
					} else {
						hallticketID = Integer.valueOf(hallTicketIdString.split("-")[1]);
					}
				}
			}
			if (hallticketID != null) {
				Session session = sessionFactory.getCurrentSession();
				String mainStr;
				String mainCount;
				if (hallticketID != -1111) {
					mainStr = "SELECT * FROM hallticketconnect where hallTicketId = " + hallticketID + "";
					mainCount = "SELECT count(rollnumber) FROM hallticketconnect where hallTicketId = " + hallticketID
							+ "";
				} else {
					mainStr = "SELECT * FROM hallticketconnect where 1=1 ";
					mainCount = "SELECT count(rollnumber) FROM hallticketconnect where 1=1";
				}

				if (rollnumber != null && !"".equals(rollnumber)) {
					mainStr = mainStr + " and rollnumber='" + rollnumber + "'";
					mainCount = mainCount + " and rollnumber='" + rollnumber + "'";
				}
				if (applied != null && !"".equals(applied)) {
					mainStr = mainStr + " and isApplied=" + applied + "";
					mainCount = mainCount + " and isApplied=" + applied + "";
				}
				if (approved != null && !"".equals(approved)) {
					mainStr = mainStr + " and isApproved=" + approved + "";
					mainCount = mainCount + " and isApproved=" + approved + "";
				}
				NativeQuery<HallTicketConnect> query = session.createSQLQuery(mainStr);
				query.addEntity(HallTicketConnect.class);
				query.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
				query.setMaxResults(pagination.getPageSize());
				hallTicketConnectList = query.list();
				if (hallTicketConnectList != null && hallTicketConnectList.isEmpty()) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("no_data"));
				}
				NativeQuery<BigInteger> query1 = session.createSQLQuery(mainCount);
				BigInteger totalCount = (BigInteger) query1.uniqueResult();
				pagination.setTotalDisplayRecords(totalCount.intValue());
			} else {
				hallTicketConnectList = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// logger.error(e.getMessage());
			hallTicketConnectList = null;
			UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_enter_correct_ID"));
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public String goToHallTicketAction() {
		return "hallTicketList";
	}

	public String showInfo() {
		try {
			if (hallTicketConnectSelectedList != null && hallTicketConnectSelectedList.size() == 0) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_select_at_least_one_record_to_see_full_information"));
			} else {
				Session session = sessionFactory.getCurrentSession();
				if (hallTicketConnectSelectedList.size() > 1) {
					UIBackingBean.setInfoMessage(
							FbMessageUtil.getLabel("Please_select_only_one_record_to_see_full_information"));
				} else {
					HallTicketConnect hallTicketConnect = hallTicketConnectSelectedList.get(0);
					Registration registration = (Registration) session.get(Registration.class,
							hallTicketConnect.getId().getRollnumber());
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
						UIBackingBean
								.setErrorMessage(FbMessageUtil.getLabel("Record_has_been_deleted_for_Enrollment_No",
										hallTicketConnect.getId().getRollnumber()));
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

	public String showEligibleList() {
		try {
			if (selectedList != null && selectedList.size() == 0) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_select_at_least_one_record_to_see_eligible_student_list"));
			} else {
				if (selectedList.size() > 1) {
					UIBackingBean.setInfoMessage(
							FbMessageUtil.getLabel("Please_select_only_one_record_to_see_eligible_student_list"));
				} else {
					HallTicket ticket = selectedList.get(0);
					setHallticketID(ticket.getHallTicketId());
					return "hallTicketList";
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

	public String showCompanyList() {
		try {
			if (selectedList != null && selectedList.size() == 0) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_select_at_least_one_record_to_see_eligible_student_list"));
			} else {
				if (selectedList.size() > 1) {
					UIBackingBean.setInfoMessage(
							FbMessageUtil.getLabel("Please_select_only_one_record_to_see_eligible_student_list"));
				} else {
					HallTicket ticket = selectedList.get(0);
					CompanyTableBean bean = (CompanyTableBean) TpoUtil
							.getManagedBean(CompanyTableBean.class.getSimpleName());
					if (bean != null) {
						bean.setCompanyName(null);
						bean.setCompanyID(ticket.getCompanyID());
					}
					return "companyList";
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void generateHallTicket() {
		try {
			if (hallTicketConnectSelectedList != null && hallTicketConnectSelectedList.isEmpty()) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_at_least_one_record_to_generate"));
			} else {
				if (hallTicketConnectSelectedList.size() > 1) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_generate"));
				} else {
					Session session = sessionFactory.getCurrentSession();
					HallTicketConnect hallTicketConnect = hallTicketConnectSelectedList.get(0);
					HallTicketConnectID hallTicketConnectID = hallTicketConnect.getId();
					if (hallTicketConnectID != null) {
						List<HallTicket> hallTicketList = new ArrayList<HallTicket>(1);
						hallTicketList.add(hallTicketConnectID.getHallTicket());
						Registration registration = (Registration) session.get(Registration.class,
								hallTicketConnectID.getRollnumber());
						if (registration != null && hallTicketList.size() > 0) {
							for (HallTicket hallTicket : hallTicketList) {
								hallTicket.setCompany(commonDBBean.getCompnay(hallTicket.getCompanyID()));
							}

							TpoUtil.renderPDFFile(pDFGenerator.generateHallTicket(registration, hallTicketList,
									AdminUser.getUser().getUserName()), registration.getRollnumber());
						} else {
							UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("No_Hall_Ticket_record_found_for",
									hallTicketConnectID.getRollnumber()));
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

	public void generateXls() {
		try {
			if (hallTicketList != null && !hallTicketList.isEmpty()) {
				String reportName = "FB_" + "OpeningList_" + AdminUser.getUser().getUserName() + "_"
						+ TpoUtil.getDateToStringYYYYMMdd(new Date());
				TpoUtil.renderEXcelFile(ExcelHandler.generateOpeningXls(
						(selectedList != null && selectedList.size() > 0) ? selectedList : hallTicketList, reportName),
						reportName);
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
			if (hallTicketList != null && !hallTicketList.isEmpty()) {
				pDFGenerator.generateOpenningList(
						(selectedList != null && selectedList.size() > 0) ? selectedList : hallTicketList);
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

	public void generateHallTicketXls() {
		try {
			if (hallTicketConnectList != null && !hallTicketConnectList.isEmpty()) {
				String reportName = "FB_" + "HallticketList_" + AdminUser.getUser().getUserName() + "_"
						+ TpoUtil.getDateToStringYYYYMMdd(new Date());
				TpoUtil.renderEXcelFile(ExcelHandler.generateHallticketXls(
						(hallTicketConnectSelectedList != null && hallTicketConnectSelectedList.size() > 0)
								? hallTicketConnectSelectedList
								: hallTicketConnectList,
						reportName), reportName);
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

	public void generateHallTicketPdfReport() {
		try {
			if (hallTicketConnectList != null && !hallTicketConnectList.isEmpty()) {
				pDFGenerator.generateHallTicketList(
						(hallTicketConnectSelectedList != null && hallTicketConnectSelectedList.size() > 0)
								? hallTicketConnectSelectedList
								: hallTicketConnectList);
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

	public void setOpenningResult(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				List<UIComponent> components = link.getChildren();
				if (components != null) {
					HtmlInputHidden hidden = (HtmlInputHidden) components.get(1);
					setList((Integer) hidden.getValue(), (String) ((HtmlInputHidden) components.get(2)).getValue());
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

	public void setList(Integer hallTicketId, String chartCompanyName) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(HallTicketConnect.class);
		criteria.add(Restrictions.eq("id.hallTicket.hallTicketId", hallTicketId));
		hallTicketConnectList = criteria.list();
		if (hallTicketConnectList == null || hallTicketConnectList.isEmpty()) {
			UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("No_one_has_given_this_exam"));
		}
	}

	public void setListNull() {
		hallTicketConnectList = null;
	}

	@SuppressWarnings("unchecked")
	public ChartModel getResultPieAnalysis() {

		if (pieSelectedCategory != null) {
			String str[] = pieSelectedCategory.getKey().toString().split("\\(");
			if (str.length == 2) {
				String str1[] = str[1].split("\\)");
				hallTicketId = new Integer(str1[0]);
				chartCompanyName = str[0];
				setList(hallTicketId, companyName);
			}
		}

		if (hallTicketConnectList != null && !hallTicketConnectList.isEmpty()) {
			Map data = new HashMap();
			int isApplied = 0;
			int isApproved = 0;
			int total = 0;
			for (HallTicketConnect result : hallTicketConnectList) {
				if (result.getIsApplied()) {
					isApplied++;
				}
				if (result.getIsApproved()) {
					isApproved++;
				}
				total++;
			}
			data.put(FbResourceUtil.getLabel("Applied"), new Integer(isApplied));
			data.put(FbResourceUtil.getLabel("Approved"), new Integer(isApproved));
			data.put(FbResourceUtil.getLabel("Total"), new Integer(total));

			PlainSeries series = new PlainSeries();
			series.setData(data);
			series.setKey("OpenningChart");

			PlainModel model = new PlainModel();
			model.addSeries(series);
			return model;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public ChartModel getResultAnalysis() {

		if (hallTicketConnectList != null && !hallTicketConnectList.isEmpty()) {
			Map isAppliedMap = new HashMap();
			Map isApprovedMap = new HashMap();
			Map totalMap = new HashMap();
			int isApplied = 0;
			int isApproved = 0;
			int total = 0;
			for (HallTicketConnect result : hallTicketConnectList) {
				if (result.getIsApplied()) {
					isApplied++;
				}
				if (result.getIsApproved()) {
					isApproved++;
				}
				total++;
			}
			isAppliedMap.put(FbResourceUtil.getLabel("Applied"), new Integer(isApplied));
			isApprovedMap.put(FbResourceUtil.getLabel("Approved"), new Integer(isApproved));
			totalMap.put(FbResourceUtil.getLabel("Total"), new Integer(total));

			PlainSeries series = new PlainSeries();
			series.setData(isAppliedMap);
			series.setKey(FbResourceUtil.getLabel("Applied"));

			PlainSeries series1 = new PlainSeries();
			series1.setData(isApprovedMap);
			series1.setKey(FbResourceUtil.getLabel("Approved"));

			PlainSeries series2 = new PlainSeries();
			series2.setData(totalMap);
			series2.setKey("Total");

			PlainModel model = new PlainModel();
			model.addSeries(series);
			model.addSeries(series1);
			model.addSeries(series2);
			return model;
		} else {
			return null;
		}
	}

	public Integer getHallTicketId() {
		return hallTicketId;
	}

	public void setHallTicketId(Integer hallTicketId) {
		this.hallTicketId = hallTicketId;
	}

	public String getChartCompanyName() {
		return chartCompanyName;
	}

	public void setChartCompanyName(String chartCompanyName) {
		this.chartCompanyName = chartCompanyName;
	}

	public PieSectorInfoImpl getPieSelectedCategory() {
		return pieSelectedCategory;
	}

	public void setPieSelectedCategory(PieSectorInfoImpl pieSelectedCategory) {
		this.pieSelectedCategory = pieSelectedCategory;
	}

	public PieSectorInfoImpl getPieSelectedCategoryChild() {
		return pieSelectedCategoryChild;
	}

	public void setPieSelectedCategoryChild(PieSectorInfoImpl pieSelectedCategoryChild) {
		this.pieSelectedCategoryChild = pieSelectedCategoryChild;
	}

	public String getApproved() {
		return approved;
	}

	public void setApproved(String approved) {
		this.approved = approved;
	}

	public String getHallTicketIdString() {
		return hallTicketIdString;
	}

	public void setHallTicketIdString(String hallTicketIdString) {
		this.hallTicketIdString = hallTicketIdString;
	}

	public void setCritera(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					criteria = (String) parameter.getValue();
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

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public void setOpenning(AjaxActionEvent event) {
		if (selectedList != null && selectedList.size() == 0) {
			UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Info12"));
			hallTicket = null;
		} else {
			if (selectedList.size() > 1) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
				hallTicket = null;
			} else {
				hallTicket = selectedList.get(0);
			}
		}
	}

	public HallTicket getHallTicket() {
		return hallTicket;
	}

	public void setHallTicket(HallTicket hallTicket) {
		this.hallTicket = hallTicket;
	}

	public void renderShortReport() {
		try {
			if (selectedList != null && selectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				if (selectedList.size() > 1) {
					UIBackingBean.setInfoMessage(
							FbMessageUtil.getLabel("Please_select_only_one_record_to_see_full_information"));
					return;
				} 
				HallTicket hallTicket = selectedList.get(0);
				if (hallTicket != null) {
					String fileName = hallTicket.getHallTicketId() + ".xls";
					byte[] a = fileUploadUtility.downloadFile(getFileServiceUrl() + "/download", fileName, IMAGECONS.openingXls);
					if(a != null) {
					TpoUtil.renderEXcelFile(a, fileName);
					}else {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("File_not_found"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}