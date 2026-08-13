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
import java.util.Date;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
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
import tpo.admin.beans.RegistrationTableBean;
import tpo.admin.excel.ExcelHandler;
import tpo.hibernate.Company;
import tpo.pdf.generator.PDFGenerator;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("CompanyTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class CompanyTableBean {

	private Logger logger = LoggerFactory.getLogger(CompanyTableBean.class);

	private String companyName;

	private Integer companyID;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private Pagination pagination;

	@Autowired
	private PDFGenerator pDFGenerator;

	private List<Company> companyList;
	private List<Company> selectedList = new ArrayList<Company>();

	private int totalStudents = 0;

	public void updateRecord() {

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSeletedRecord() {
		try {
			if (selectedList != null && selectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (Company company : selectedList) {
					session.delete(company);
				}
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success12));
				companyList = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public List<Company> getCompanyList() {
		return companyList;
	}

	public void searchRecord() {
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public List<Company> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(List<Company> selectedList) {
		this.selectedList = selectedList;
	}

	public void setCompanyList(List<Company> companyList) {
		this.companyList = companyList;
	}

	public void setCompany(AjaxActionEvent event) {
		try {
			CompanyBean bean = (CompanyBean) TpoUtil.getManagedBean(CompanyBean.class.getSimpleName());
			if (selectedList != null && selectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Info12"));
				if (bean != null) {
					bean.setCompany(null);
				}
			} else {
				if (selectedList.size() > 1) {
					UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
					if (bean != null) {
						bean.setCompany(null);
					}
				} else {
					if (bean != null) {
						bean.initCompany(selectedList.get(0));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setCompanyToView(AjaxActionEvent event) {
		try {
			CompanyBean bean = (CompanyBean) TpoUtil.getManagedBean(CompanyBean.class.getSimpleName());
			if (selectedList != null && selectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Info12"));
				if (bean != null) {
					bean.setCompany(null);
				}
			} else {
				if (selectedList.size() > 1) {
					UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
					if (bean != null) {
						bean.setCompany(null);
					}
				} else {
					if (bean != null) {
						bean.viewCompany(selectedList.get(0));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
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
			totalStudents = 0;
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Company.class);
			String userName = AdminUser.getUser().getUserName();
			List<String> userNames = AdminUser.getUser().getUserList();
			criteria.add(Restrictions.in("createdBy", userNames));
			criteria.addOrder(Order.asc("createdBy"));
			String queryStr = "select count(companyName) from company where createdBy in("
					+ TpoUtil.getComaSeprateValue(userNames) + ")";
			if (companyName != null && !companyName.equals("")) {
				criteria.add(Restrictions.ilike("companyname", "%" + companyName + "%"));
				queryStr = queryStr + " and companyname like '%" + companyName + "%'";
			}
			if (companyID != null) {
				criteria.add(Restrictions.eq("companyID", companyID));
				queryStr = queryStr + " and companyID = " + companyID + "";
			}
			NativeQuery<BigInteger> query = session.createSQLQuery(queryStr);
			criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			companyList = criteria.list();
			if (companyList != null && !companyList.isEmpty()) {
				ChartBean bean = (ChartBean) TpoUtil.getManagedBean(ChartBean.class.getSimpleName());
				if (bean != null) {
					int totalPerCompany;
					for (Company company : companyList) {
						totalPerCompany = bean.getStudentCount(userName, company.getCompanyname(), session);
						company.setTotal(totalPerCompany);
						totalStudents += totalPerCompany;
					}
				}
				Company company = new Company();
				company.setCompanyID(1);
				company.setWebsite("Total=");
				company.setTotal(totalStudents);
				companyList.add(company);
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
			if (companyList != null && !companyList.isEmpty()) {
				String reportName = "FB_" + "CompanyList_" + AdminUser.getUser().getUserName() + "_"
						+ TpoUtil.getDateToStringYYYYMMdd(new Date());
				TpoUtil.renderEXcelFile(ExcelHandler.generateCompanyXls(
						(selectedList != null && selectedList.size() > 0) ? selectedList : companyList, reportName),
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
			if (companyList != null && !companyList.isEmpty()) {
				pDFGenerator.generateCompanyList(
						(selectedList != null && selectedList.size() > 0) ? selectedList : companyList);
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

	public String showInfo() {
		try {
			if (selectedList != null && selectedList.size() == 0) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_select_at_least_one_record_to_see_full_information"));
			} else {
				if (selectedList.size() > 1) {
					UIBackingBean.setInfoMessage(
							FbMessageUtil.getLabel("Please_select_only_one_record_to_see_full_information"));
				} else {
					Company company = selectedList.get(0);
					if (company != null) {
						RegistrationTableBean bean = (RegistrationTableBean) TpoUtil
								.getManagedBean(RegistrationTableBean.class.getSimpleName());
						if (bean != null) {
							bean.setCompanyName(company.getCompanyname());
							return "studentList";
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public int getTotalStudents() {
		return totalStudents;
	}

	public void setTotalStudents(int totalStudents) {
		this.totalStudents = totalStudents;
	}

	public void setLogo(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					if (parameter.getValue() != null)
						companyID = (int) parameter.getValue();
					else
						companyID = null;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public Integer getCompanyID() {
		return companyID;
	}

	public void setCompanyID(Integer companyID) {
		this.companyID = companyID;
	}

}