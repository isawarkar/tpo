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
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
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
import tpo.beans.Pagination;
import tpo.beans.UIBackingBean;
import tpo.hibernate.Project;
import tpo.pdf.generator.PDFGenerator;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("ProjectTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class ProjectTableBean {

	private Logger logger = LoggerFactory.getLogger(ProjectTableBean.class);

	private String projectName;
	
	private String projectId;
	
	@Autowired
	private PDFGenerator pDFGenerator;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private Pagination pagination;

	private List<Project> projectList;
	private List<Project> selectedProjectList = new ArrayList<Project>();

	public void updateRecord() {

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSeletedRecord() {
		try {
			if (selectedProjectList != null && selectedProjectList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil
						.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (Project company : selectedProjectList) {
					session.delete(company);
				}
				UIBackingBean.setSuccessMessage(FbMessageUtil
						.getLabel("Selected_efforts_has_been_deleted"));
				projectList = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setProject(AjaxActionEvent event) {
		try {
			ProjectBean bean = (ProjectBean) TpoUtil
					.getManagedBean(ProjectBean.class.getSimpleName());
			if (selectedProjectList != null && selectedProjectList.size() == 0) {
				UIBackingBean
						.setInfoMessage(FbMessageUtil.getLabel("Info12"));
				if (bean != null) {
					bean.setProject(null);
				}
			} else {
				if (selectedProjectList.size() > 1) {
					UIBackingBean
							.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
					if (bean != null) {
						bean.setProject(null);
					}
				} else {
					if (bean != null) {
						bean.initProject(selectedProjectList.get(0));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void setProjectToAdd(AjaxActionEvent event) {
		try {
			ProjectBean bean = (ProjectBean) TpoUtil
					.getManagedBean(ProjectBean.class.getSimpleName());
			if (bean != null) {
				bean.setCurrentDocMode(CCPConstant.CREATE);
				bean.setProject(new Project());
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


	public void inIt() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Project.class);
			AdminUser user = AdminUser.getUser();
			List<String> userList = user.getUserList();
			criteria.add(Restrictions.in("createdBy", userList));
			String queryStr = "select count(projectname) from project where createdBy in("+TpoUtil.getComaSeprateValue(userList)+")";
			NativeQuery<BigInteger> query;
			if (projectName != null && !projectName.equals("")) {
				criteria.add(Restrictions.ilike("projectname", "%" + projectName + "%"));
				queryStr = queryStr + " and projectname like '%" + projectName + "%'";
			}
			if (projectId != null && !projectId.equals("")) {
				criteria.add(Restrictions.ilike("projectid", "%" + projectId + "%"));
				queryStr = queryStr + " and projectid like '%" + projectId + "%'";
			}
			query = session.createSQLQuery(queryStr);
			criteria.setFirstResult(pagination.getPageSize()
					* (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			projectList = criteria.list();
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
			if (projectList != null && !projectList.isEmpty()) {
			
				String reportName = "FB_" +  "ProjectList_" + AdminUser.getUser().getUserName() + "_" + TpoUtil.getDateToStringYYYYMMdd(new Date());
				TpoUtil.renderEXcelFile(ExcelHandler.generateProjectXls((selectedProjectList != null && selectedProjectList.size() >0)?selectedProjectList:projectList, reportName),reportName);
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
			if (projectList != null && !projectList.isEmpty()) {
				pDFGenerator.generateProjectList((selectedProjectList != null && selectedProjectList.size() >0)?selectedProjectList:projectList);
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
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void changeStatus() {
		try {
			Session session = sessionFactory.getCurrentSession();
			if (selectedProjectList != null && selectedProjectList.size() > 0) {
				for (Project project : selectedProjectList) {

					if (project.getStatus() == 1) {
						project.setStatus(0);
					} else {
						project.setStatus(1);
					}					
					session.update(project);
				}
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Status_changed_successfully"));
					selectedProjectList = null;
			} else {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public List<Project> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<Project> projectList) {
		this.projectList = projectList;
	}

	public List<Project> getSelectedProjectList() {
		return selectedProjectList;
	}

	public void setSelectedProjectList(List<Project> selectedProjectList) {
		this.selectedProjectList = selectedProjectList;
	}
	
	public String showInfo() {
		try {
			if (selectedProjectList != null && selectedProjectList.size() == 0) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_select_at_least_one_record_to_see_full_information"));
			} else {
				Session session = sessionFactory.getCurrentSession();
				if (selectedProjectList.size() > 1) {
					UIBackingBean.setInfoMessage(
							FbMessageUtil.getLabel("Please_select_only_one_record_to_see_full_information"));
				} else {
					Project project = selectedProjectList.get(0);
					if (project != null) {
						ModuleTableBean bean = (ModuleTableBean) TpoUtil
								.getManagedBean(ModuleTableBean.class.getSimpleName());
						if (bean != null) {
							bean.setProject(project);
							return "moduleList";
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
		return null;
	}

	
	
}