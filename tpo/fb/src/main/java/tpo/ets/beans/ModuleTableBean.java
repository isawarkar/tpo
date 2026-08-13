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
import tpo.hibernate.Module;
import tpo.hibernate.Project;
import tpo.pdf.generator.PDFGenerator;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("ModuleTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class ModuleTableBean {

	private Logger logger = LoggerFactory.getLogger(ModuleTableBean.class);

	private String moduleName;
	
	private Project project;
	
	private String moduleId;

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private PDFGenerator pDFGenerator;

	@Autowired
	private Pagination pagination;

	private List<Module> moduleList;
	private List<Module> selectedModuleList = new ArrayList<Module>();

	public void updateRecord() {

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSeletedRecord() {
		try {
			if (selectedModuleList != null && selectedModuleList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil
						.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (Module module : selectedModuleList) {
					session.delete(module);
				}
				UIBackingBean.setSuccessMessage(FbMessageUtil
						.getLabel("Selected_efforts_has_been_deleted"));
				moduleList = null;
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
			ProjectBean bean = (ProjectBean) TpoUtil
					.getManagedBean(ProjectBean.class.getSimpleName());
			if (selectedModuleList != null && selectedModuleList.size() == 0) {
				UIBackingBean
						.setInfoMessage(FbMessageUtil.getLabel("Info12"));
				if (bean != null) {
					bean.setProject(null);
				}
			} else {
				if (selectedModuleList.size() > 1) {
					UIBackingBean
							.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
					if (bean != null) {
						bean.setProject(null);
					}
				} else {
					if (bean != null) {
						Module module = selectedModuleList.get(0);
						bean.initModule(module);
						bean.setProjectId(module.getProject().getProjectid());
						bean.setCurrentDocMode(CCPConstant.UPDATE);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void setModuleToAdd(AjaxActionEvent event) {
		try {
			ProjectBean bean = (ProjectBean) TpoUtil
					.getManagedBean(ProjectBean.class.getSimpleName());
			if (bean != null) {
				bean.setCurrentDocMode(CCPConstant.CREATE);
				bean.setModule(new Module());
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
			Criteria criteria = session.createCriteria(Module.class);
			AdminUser user = AdminUser.getUser();
			List<String> userList = user.getUserList();
			criteria.add(Restrictions.in("createdBy", userList));
			if(project !=null){
				criteria.add(Restrictions.eq("project", project));
			}
			criteria.createAlias("project", "project");
			String queryStr = "select count(modulename) from module where createdBy in("+TpoUtil.getComaSeprateValue(userList)+")";
			NativeQuery<BigInteger> query;
			if (moduleName != null && !moduleName.equals("")) {
				criteria.add(Restrictions.ilike("modulename", "%" + moduleName + "%"));
				queryStr = queryStr + " and modulename like '%" + moduleName + "%'";
			}
			if (moduleId != null && !moduleId.equals("")) {
				criteria.add(Restrictions.ilike("moduleid", "%" + moduleId + "%"));
				queryStr = queryStr + " and moduleid like '%" + moduleId + "%'";
			}
			query = session.createSQLQuery(queryStr);
			criteria.setFirstResult(pagination.getPageSize()
					* (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			moduleList = criteria.list();
			project = null;
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
			if (moduleList != null && !moduleList.isEmpty()) {
				
				String reportName = "FB_" +  "ModuleList_" + AdminUser.getUser().getUserName() + "_" + TpoUtil.getDateToStringYYYYMMdd(new Date());
				TpoUtil.renderEXcelFile(ExcelHandler.generateModuleXls((selectedModuleList != null && selectedModuleList.size() >0)?selectedModuleList:moduleList, reportName),reportName);
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
			if (moduleList != null && !moduleList.isEmpty()) {
		      pDFGenerator.generateModuleList((selectedModuleList != null && selectedModuleList.size() >0)?selectedModuleList:moduleList);
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
			if (selectedModuleList != null && selectedModuleList.size() > 0) {
				for (Module module : selectedModuleList) {

					if (module.getStatus() == 1) {
						module.setStatus(0);
					} else {
						module.setStatus(1);
					}					
					session.update(module);
				}
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Status_changed_successfully"));
					selectedModuleList = null;
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

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public List<Module> getModuleList() {
		return moduleList;
	}

	public void setModuleList(List<Module> moduleList) {
		this.moduleList = moduleList;
	}

	public List<Module> getSelectedModuleList() {
		return selectedModuleList;
	}

	public void setSelectedModuleList(List<Module> selectedModuleList) {
		this.selectedModuleList = selectedModuleList;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

		
	
}