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

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.openfaces.component.command.CommandLink;
import org.openfaces.event.AjaxActionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.admin.excel.ExcelHandler;
import tpo.beans.Pagination;
import tpo.beans.UIBackingBean;
import tpo.hibernate.EmployeeEfforts;
import tpo.pdf.generator.PDFGenerator;
import tpo.util.FbMessageUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("EffortReportTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class EffortReportTableBean extends EffortTableBean {

	private List<String> employeeNameList = new ArrayList<String>();

	private List<String> selectedEmpList = new ArrayList<String>();
	
	private String description;
	
	@Autowired
	private PDFGenerator pDFGenerator;
	
	private String remarks;
	
	public void searchRecord() {
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
		userNameObj = null;
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

	private List<EmployeeEfforts> getTheEffortList() {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(EmployeeEfforts.class);
		String adminName = AdminUser.getUser().getUserName();
		criteria.add(Restrictions.ne("logindetails.userName", adminName));
		criteria.add(Restrictions.eq("createdBy", AdminUser.getUser().getUserName()));

		if (employeeName != null && !employeeName.isEmpty()) {
			criteria.add(Restrictions.eq("logindetails.userName", employeeName));
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

	public void inIt() {
		totalEfforts = 0;
		try {
			if (date == null) {
				date = TpoUtil.getWeekStartDate();
			}
			if (toDate == null) {
				toDate = TpoUtil.getWeekEndDate();
			}
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(EmployeeEfforts.class);
			String adminName = AdminUser.getUser().getUserName();
			criteria.add(Restrictions.ne("logindetails.userName", adminName));
			String queryStr = "select count(username) from employee_efforts where username !='" + adminName + "'";
			criteria.add(Restrictions.eq("createdBy", AdminUser.getUser().getUserName()));
			
			if (userNameObj != null) {
				employeeName = userNameObj;
			}

			if (employeeName != null && !employeeName.isEmpty()) {
				criteria.add(Restrictions.eq("logindetails.userName", employeeName));
				queryStr = queryStr + " and username like '%" + employeeName + "%'";
			}else if(selectedEmpList != null && !selectedEmpList.isEmpty()){
				criteria.add(Restrictions.in("logindetails.userName", selectedEmpList));
				StringBuffer emplyoes = null;
				for (String entry : selectedEmpList) {
					if (emplyoes == null) {
						emplyoes = new StringBuffer();
						emplyoes.append("'"+entry+"'");
					} else {
						emplyoes.append(",").append("'"+entry+"'");
					}
				}
				queryStr = queryStr + " and username in(" + emplyoes + ")";
			}
			if (date != null && !date.equals("") && toDate != null && !toDate.equals("")) {
				String strDate = TpoUtil.getDateToStringYYYYMMdd(date);
				String endDate = TpoUtil.getDateToStringYYYYMMdd(toDate);
				criteria.add(Restrictions.ge("sdate", date));
				criteria.add(Restrictions.le("sdate", toDate));
				queryStr = queryStr + " and sdate between '%" + strDate + "%' and '%" + endDate + "%'";
			} else if (date != null && !date.equals("") && (toDate == null || toDate.equals(""))) {
				String strDate = TpoUtil.getDateToStringYYYYMMdd(date);
				criteria.add(Restrictions.like("sdate", date));
				queryStr = queryStr + " and sdate like '%" + strDate + "%'";
			}
			
			if (status != null && !status.isEmpty()) {
				criteria.add(Restrictions.eq("status", status));
				queryStr = queryStr + " and status = '" + status + "'";
			}
			
			NativeQuery<BigInteger> query;
			query = session.createSQLQuery(queryStr);
			criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			effortsList = criteria.list();
			if (effortsList != null && !effortsList.isEmpty()) {
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

	public void clearBean() {
		date = null;
		toDate = null;
		userNameObj = null;
		totalEfforts = 0;
		employeeName = null;
		effortsList = null;
		selectedEffortsList = null;
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
	}

	public List<String> getEmployeeNameList() {
		try {
			if (employeeNameList.size() == 0) {
				Session session = sessionFactory.getCurrentSession();
				String userName = AdminUser.getUser().getUserName();
				NativeQuery<String> query = session.createSQLQuery("select userName from logindetails where createdBy in("+TpoUtil.getComaSeprateValue(AdminUser.getUser().getUserList())+") and userName!='" + userName + "'");
				employeeNameList = query.list();
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return employeeNameList;
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

	public List<String> getSelectedEmpList() {
		return selectedEmpList;
	}

	public void setSelectedEmpList(List<String> selectedEmpList) {
		this.selectedEmpList = selectedEmpList;
	}
	
	public void setData(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
						List<UIComponent> list = link.getChildren();
						UIParameter parameter = (UIParameter) list.get(0);
						description = (String) parameter.getValue();
						parameter = (UIParameter) list.get(1);
						remarks = (String) parameter.getValue();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
	
}