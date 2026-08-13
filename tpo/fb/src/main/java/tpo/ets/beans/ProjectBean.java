/**
 * 
 */
package tpo.ets.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.beans.UIBackingBean;
import tpo.hibernate.Module;
import tpo.hibernate.Project;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;

/**
 * @author indrajeet
 * 
 */
@Repository("ProjectBean")
@Transactional(readOnly = true)
@Scope("session")
public class ProjectBean {

	private Project project;
	
	private Long projectId;

	private HtmlSelectOneMenu projectsListSelectOne = new HtmlSelectOneMenu();

	private HtmlSelectOneMenu moduleList;

	private Module module;

	private Session session;

	private String currentDocMode = CCPConstant.CREATE;

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void save() {
		try {
			session = sessionFactory.getCurrentSession();
			if (CCPConstant.CREATE.equalsIgnoreCase(currentDocMode)) {
				Project projectObj = session.get(Project.class, project.getProjectid());
				if (projectObj == null) {
					project.setStatus(1);
					project.setCreatedBy(AdminUser.getUser().getUserName());
					session.save(project);
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Project_Added_successfully"));
				} else {
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Project_is_all_ready_exist_for_project_id",
							project.getProjectid()));
				}
			}else{
				session.update(project);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Project_Updated_successfully"));
			}
		} catch (HibernateException e) {
			UIBackingBean.setErrorMessage(e.getMessage());
			e.printStackTrace();
		}

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveModule() {
		try {
			session = sessionFactory.getCurrentSession();
			Project project = (Project) session.get(Project.class,
					new Long(projectsListSelectOne.getValue().toString()));
			if (project != null) {
				
				if (CCPConstant.CREATE.equalsIgnoreCase(currentDocMode)) {
				Criteria criteria = session.createCriteria(Module.class);
				criteria.add(Restrictions.eq("project", project));
				criteria.add(Restrictions.or(Restrictions.eq("moduleid", new Long(module.getModuleid())),
						Restrictions.eq("modulename", module.getModulename())));
				Module moduleObj = (Module) criteria.uniqueResult();
				if (moduleObj == null) {
					module.setProject(project);
					module.setCreatedBy(AdminUser.getUser().getUserName());
					module.setStatus(1);
					session.save(module);
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Module_Added_successfully"));
				} else {
					String arr[] = new String[2];
					arr[0] = module.getModuleid().toString()+module.getModulename();
					arr[1] = project.getProjectname();
					UIBackingBean
							.setSuccessMessage(FbMessageUtil.getLabel("Module_is_all_ready_exist_for_project", arr));
				}
				}else{
					module.setProject(project);
					session.update(module);
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Module_Updated_successfully"));
				}
			} else {
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Project_Does_not_exist"));
			}
		} catch (HibernateException e) {
			UIBackingBean.setErrorMessage(e.getMessage());
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void renderModule(ValueChangeEvent event) {
		session = sessionFactory.getCurrentSession();
		if (projectsListSelectOne.getValue() != null && !projectsListSelectOne.getValue().toString().isEmpty()) {
			Project project1 = (Project) session.get(Project.class,
					Long.valueOf(projectsListSelectOne.getValue().toString()));
			if (project1 != null) {
				Set<Module> moduleSet = project1.getModules();
				UISelectItems selectItems = new UISelectItems();
				List<SelectItem> items = new ArrayList<SelectItem>();
				items.add(new SelectItem("", ""));
				for (Module module : moduleSet) {
					if (module.getStatus() == 1L) {
						items.add(new SelectItem(module.getModuleid(), module.getModulename()));
					}
				}
				selectItems.setValue(items);
				List<UIComponent> listOfUIComponents = moduleList.getChildren();
				listOfUIComponents.clear();
				listOfUIComponents.add(selectItems);
			}
		}
	}

	public HtmlSelectOneMenu getProjectsListSelectOne() {
		if (projectsListSelectOne != null) {
			session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Project.class);
			List<String> users = new ArrayList<String>();
			users.add(AdminUser.getUser().getUserName());
			users.add(AdminUser.getUser().getParent());
			criteria.add(Restrictions.in("createdBy", users));
			List<Project> projectsList = criteria.list();
			UISelectItems selectItems = new UISelectItems();
			List<SelectItem> items = new ArrayList<SelectItem>();
			items.add(new SelectItem("", ""));
			for (Project project : projectsList) {
				if (project.getStatus() == 1L) {
					items.add(new SelectItem(project.getProjectid(), project.getProjectname()));
				}
			}
			selectItems.setValue(items);
			List<UIComponent> listOfUIComponents = projectsListSelectOne.getChildren();
			listOfUIComponents.clear();
			listOfUIComponents.add(selectItems);
		}
		return projectsListSelectOne;
	}

	public void setProjectsListSelectOne(HtmlSelectOneMenu projectsListSelectOne) {
		this.projectsListSelectOne = projectsListSelectOne;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public HtmlSelectOneMenu getModuleList() {
		return moduleList;
	}

	public void setModuleList(HtmlSelectOneMenu moduleList) {
		this.moduleList = moduleList;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public String getCurrentDocMode() {
		return currentDocMode;
	}

	public void setCurrentDocMode(String currentDocMode) {
		this.currentDocMode = currentDocMode;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	public void initProject(Project project) {
		this.project = project;
		currentDocMode = CCPConstant.UPDATE;
	}
	
	public void initModule(Module module) {
		this.module = module;
		currentDocMode = CCPConstant.UPDATE;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
	
}
