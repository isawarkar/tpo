/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.ets.beans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.Message;

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

import tpo.admin.beans.AdminUser;
import tpo.admin.excel.ExcelHandler;
import tpo.beans.AddEffort;
import tpo.beans.CompanyBean;
import tpo.beans.Pagination;
import tpo.beans.Parent;
import tpo.beans.UIBackingBean;
import tpo.email.EmailUtil;
import tpo.hibernate.Company;
import tpo.hibernate.EmployeeEfforts;
import tpo.hibernate.Module;
import tpo.hibernate.Project;
import tpo.hibernate.Userdetails;
import tpo.pdf.generator.PDFGenerator;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("EffortTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class EffortTableBean extends Parent {

	public Logger logger = LoggerFactory.getLogger(EffortTableBean.class);

	private String rejectReason;
	
	protected String status;

	protected Date date;

	protected Date toDate;

	protected Project project;

	protected Module module;

	protected String userNameObj;

	protected int totalEfforts;

	protected String employeeName;
	
	@Autowired
	private PDFGenerator pDFGenerator;

	protected List<EmployeeEfforts> effortsList;
	protected List<EmployeeEfforts> selectedEffortsList = new ArrayList<EmployeeEfforts>();

	@Autowired
	protected SessionFactory sessionFactory;

	@Autowired
	protected Pagination pagination;

	public void clearBean() {
		rejectReason = null;
		date = null;
		toDate = null;
		project = null;
		module = null;
		userNameObj = null;
		totalEfforts = 0;
		employeeName = null;
		effortsList = null;
		selectedEffortsList = null;
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
	}

	public void updateRecord() {

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSeletedRecord() {
		try {
			if (selectedEffortsList != null && selectedEffortsList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (EmployeeEfforts effort : selectedEffortsList) {
					session.delete(effort);
				}
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Selected_efforts_has_been_deleted"));
				effortsList = null;
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
	public void approveSeletedRecord() {
		try {
			if (selectedEffortsList != null && selectedEffortsList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error13));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (EmployeeEfforts effort : selectedEffortsList) {
					effort.setStatus(CCPConstant.EFFORT_APPROVED);
					session.update(effort);
				}
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Selected_efforts_has_been_approved"));
				effortsList = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setEffort(AjaxActionEvent event) {
		try {
			AddEffort bean = (AddEffort) TpoUtil.getManagedBean(AddEffort.class.getSimpleName());
			if (selectedEffortsList != null && selectedEffortsList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Info12"));
				if (bean != null) {
					bean.setEmployeeEffort(null);
				}
			} else {
				if (selectedEffortsList.size() > 1) {
					UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
					if (bean != null) {
						bean.setEmployeeEffort(null);
					}
				} else {
					if (bean != null) {
						bean.initEffort(selectedEffortsList.get(0));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setEffortToAdd(AjaxActionEvent event) {
		try {
			AddEffort bean = (AddEffort) TpoUtil.getManagedBean(AddEffort.class.getSimpleName());
			if (bean != null) {
				bean.setCurrentDocMode(CCPConstant.CREATE);
				bean.setEmployeeEffort(new EmployeeEfforts());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void searchRecord() {
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setCompanyToAdd(AjaxActionEvent event) {
		try {
			CompanyBean bean = (CompanyBean) TpoUtil.getManagedBean(CompanyBean.class.getSimpleName());
			if (bean != null) {
				bean.setCurrentDocMode(CCPConstant.CREATE);
				bean.setCompany(new Company());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void inIt() {
		try {
			totalEfforts = 0;
			if (date == null) {
				date = TpoUtil.getWeekStartDate();
			}
			if (toDate == null) {
				toDate = TpoUtil.getWeekEndDate();
			}
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(EmployeeEfforts.class);
			String userName = AdminUser.getUser().getUserName();
			if (AdminUser.getUser().getChildUserName() != null && !AdminUser.getUser().getChildUserName().isEmpty()) {
				userName = AdminUser.getUser().getChildUserName();
			}
			if (userNameObj != null) {
				userName = userNameObj;
			}
			
			criteria.add(Restrictions.eq("logindetails.userName", userName));
			String queryStr = "select count(username) from employee_efforts where username='" + userName + "'";
			NativeQuery<BigInteger> query;

			if (date != null && !date.equals("") && toDate != null && !toDate.equals("")) {
				String strDate = TpoUtil.getDateToStringYYYYMMdd(date);
				String endDate = TpoUtil.getDateToStringYYYYMMdd(toDate);
				criteria.add(Restrictions.ge("sdate", date));
				criteria.add(Restrictions.le("sdate", toDate));
				queryStr = queryStr + " and sdate between STR_TO_DATE('" + strDate + "', '%Y-%m-%d') and STR_TO_DATE('"
						+ endDate + "', '%Y-%m-%d')";
			} else if (date != null && !date.equals("") && (toDate == null || toDate.equals(""))) {
				String strDate = TpoUtil.getDateToStringYYYYMMdd(date);
				criteria.add(Restrictions.like("sdate", "%" + date + "%"));
				queryStr = queryStr + " and sdate like '%" + strDate + "%'";
			}
			
			if (status != null && !status.isEmpty()) {
				criteria.add(Restrictions.eq("status", status));
				queryStr = queryStr + " and status = '" + status + "'";
			}
			query = session.createSQLQuery(queryStr);
			criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			effortsList = criteria.list();
			if (effortsList != null && !effortsList.isEmpty()){
				for (EmployeeEfforts effort : effortsList) {
					if (effort.getTime() != null) {
						totalEfforts = totalEfforts + effort.getTime();
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
			List<EmployeeEfforts> effortsList = getTheEffortList();
			if (effortsList != null && !effortsList.isEmpty()) {
				String reportName = "FB_" +  "EmployeeEfforts_" + AdminUser.getUser().getUserName() + "_" + TpoUtil.getDateToStringYYYYMMdd(new Date());
				TpoUtil.renderEXcelFile(ExcelHandler.generateEmployeeEffortsXls(effortsList, reportName),reportName);
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
			List<EmployeeEfforts> effortsList = getTheEffortList();
			if (effortsList != null && !effortsList.isEmpty()) {
				pDFGenerator.generateEmployeeEffortsList(effortsList);
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

	public List<EmployeeEfforts> getEffortsList() {
		return effortsList;
	}

	public void setEffortsList(List<EmployeeEfforts> effortsList) {
		this.effortsList = effortsList;
	}

	public List<EmployeeEfforts> getSelectedEffortsList() {
		return selectedEffortsList;
	}

	public void setSelectedEffortsList(List<EmployeeEfforts> selectedEffortsList) {
		this.selectedEffortsList = selectedEffortsList;
	}

	public String getUserNameObj() {
		return userNameObj;
	}

	public void setUserNameObj(String userNameObj) {
		this.userNameObj = userNameObj;
	}

	public void setProject(AjaxActionEvent event) {
		try {
			if (event != null) {
				Session session = null;
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					session = sessionFactory.getCurrentSession();
					project = (Project) session.get(Project.class, new Long(link.getValue().toString()));
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

	public void setModule(AjaxActionEvent event) {
		try {
			if (event != null) {
				Session session = null;
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					session = sessionFactory.getCurrentSession();
					module = (Module) session.get(Module.class, new Long(link.getValue().toString()));
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

	private List<EmployeeEfforts> getTheEffortList() {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(EmployeeEfforts.class);
		String adminName = AdminUser.getUser().getUserName();
		if (null != adminName) {
			criteria.add(Restrictions.eq("logindetails.userName", adminName));
			criteria.add(Restrictions.eq("createdBy", adminName));
		}

		if (date != null && !date.equals("") && toDate != null && !toDate.equals("")) {
			criteria.add(Restrictions.ge("sdate", date));
			criteria.add(Restrictions.le("sdate", toDate));
		} else if (date != null && !date.equals("") && (toDate == null || toDate.equals(""))) {
			criteria.add(Restrictions.like("sdate", date));
		}
		List<EmployeeEfforts> effortsList = criteria.list();
		return effortsList;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public int getTotalEfforts() {
		return totalEfforts;
	}

	public void setTotalEfforts(int totalEfforts) {
		this.totalEfforts = totalEfforts;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void rejectSeletedRecord() {
		try {
			if (selectedEffortsList != null && selectedEffortsList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				EmailUtil emailUtill = getEmailInstance();
				List<String> recipients = null;
				StringBuffer message = null;
				if (emailUtill != null) {
					Object param[];
					int i = 1;
					for (EmployeeEfforts effort : selectedEffortsList) {
						effort.setStatus(CCPConstant.EFFORT_REJECTED);
						session.update(effort);
						param = new Object[3];
						param[0] = TpoUtil.getDateToStringInddmmyyyy(effort.getSdate());
						param[1] = effort.getProject();
						param[2] = effort.getModule();
						Userdetails userdetails = effort.getLogindetails().getUserdetails();
						if (recipients == null) {
							recipients = new ArrayList<String>(1);
							recipients.add(userdetails.getEmail());
							message = new StringBuffer(FbMessageUtil.getLabel("Dear"));
							message.append(userdetails.getFirstName()).append(" ").append(userdetails.getLastName())
									.append(",<br>");
						}
						message.append("<br>").append(i++).append(")")
								.append(FbMessageUtil.getLabel("Your_effort_has_been_rejected_for", param));

					}
					message.append(
							"<br><br><font color=red>Below is the reason for the rejection.Please contact you admin("
									+ AdminUser.getUser().getUserName() + ") for more info.</font><br>");
					message.append(
							"<br>======================================Reason Start==============================================<br><br>");
					message.append(rejectReason);
					message.append(
							"<br>======================================Reason End==============================================<br>");
					message.append(TpoUtil.getMesageString());
					emailUtill.postMail(recipients,
							"Your effors has been rejected by " + AdminUser.getUser().getUserName(), message.toString(),
							TpoUtil.ADMIN_EMAIL, Message.RecipientType.TO);
				}
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Selected_efforts_has_been_rejected"));
				effortsList = null;
				rejectReason = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void beforeReject() {
		try {
			if (selectedEffortsList != null && selectedEffortsList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error13));
			} else {
				for (EmployeeEfforts effort : selectedEffortsList) {
					if (CCPConstant.EFFORT_REJECTED.equals(effort.getStatus())) {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error13));
						selectedEffortsList.removeAll(selectedEffortsList);
						String str = effort.toString();
						UIBackingBean
								.setErrorMessage(FbMessageUtil.getLabel("Please_dont_select_rejected_record", str));
						return;
					}
				}
				String emp = null;
				for (EmployeeEfforts effort : selectedEffortsList) {
					if (emp == null) {
						emp = effort.getLogindetails().getUserName();
					}
					if (!emp.equals(effort.getLogindetails().getUserName())) {
						selectedEffortsList.removeAll(selectedEffortsList);
						UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_single_employee_name", emp));
						return;
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

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public void previousWeek() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.WEEK_OF_YEAR, -1);
		date = calendar.getTime();
		
		calendar.setTime(toDate);
		calendar.add(Calendar.WEEK_OF_YEAR, -1);
		toDate = calendar.getTime();
	}
	
	public void nextWeek() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.WEEK_OF_YEAR, 1);
		date = calendar.getTime();
		
		calendar.setTime(toDate);
		calendar.add(Calendar.WEEK_OF_YEAR, 1);
		toDate = calendar.getTime();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}