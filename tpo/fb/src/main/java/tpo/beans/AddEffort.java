/**
 * 
 */
package tpo.beans;

import java.util.ArrayList;
import java.util.Date;
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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.hibernate.EmployeeEfforts;
import tpo.hibernate.Logindetails;
import tpo.hibernate.Module;
import tpo.hibernate.Project;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;

/**
 * @author indrajeet
 * 
 */
@Repository("AddEffort")
@Transactional(readOnly = true)
@Scope("session")
public class AddEffort {

	private HtmlSelectOneMenu module;

	private Session session = null;

	private HtmlSelectOneMenu projectsListSelectOne = new HtmlSelectOneMenu();

	private EmployeeEfforts employeeEffort;

	private String currentDocMode = CCPConstant.CREATE;

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void save() {
		boolean isRecordExist = false;
		try {
			session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Logindetails.class);
			String userName = AdminUser.getUser().getUserName();
			if (AdminUser.getUser().getChildUserName() != null && !AdminUser.getUser().getChildUserName().isEmpty()) {
				userName = AdminUser.getUser().getChildUserName();
			}
			criteria.add(Restrictions.eq("userName", userName));
			Logindetails logindetails = (Logindetails) criteria.uniqueResult();
			if (CCPConstant.CREATE.equalsIgnoreCase(currentDocMode)) {
				employeeEffort.setEffortid(null);
				isRecordExist = isEffortExist(employeeEffort.getSdate());
				employeeEffort.setLogindetails(logindetails);
				employeeEffort.setCreatedBy(AdminUser.getUser().getParent());
			}
			employeeEffort.setStatus(CCPConstant.EFFORT_SUBMMITED);
			if (!isRecordExist) {

				session.saveOrUpdate(employeeEffort);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Your_effort_was_successfully", currentDocMode));
			} else {
				UIBackingBean.setSuccessMessage(
						FbMessageUtil.getLabel("Effort_is_all_ready_exists_for", employeeEffort.getSdate()));
			}
		} catch (HibernateException e) {

			e.printStackTrace();
		}
	}

	private boolean isEffortExist(Date date) {
		Criteria criteria = session.createCriteria(EmployeeEfforts.class)
				.setProjection(Projections.projectionList().add(Projections.property("effortid"), "effortid"));
		;
		criteria.add(Restrictions.eq("logindetails.userName", AdminUser.getUser().getUserName()));
		criteria.add(Restrictions.eq("sdate", date));
		if (criteria.uniqueResult() != null) {
			return true;
		}
		return false;
	}

	public void initEffort(EmployeeEfforts employeeEfforts) {
		if (employeeEfforts != null) {
			this.employeeEffort = employeeEfforts;
			session = sessionFactory.getCurrentSession();
			module = new HtmlSelectOneMenu();
			loadModules(employeeEffort.getProject());
			currentDocMode = CCPConstant.UPDATE;
		}
	}

	@SuppressWarnings("unchecked")
	public void renderModule(ValueChangeEvent event) {
		session = sessionFactory.getCurrentSession();
		if (projectsListSelectOne.getValue() != null && !projectsListSelectOne.getValue().toString().isEmpty()) {
			loadModules(Long.valueOf(projectsListSelectOne.getValue().toString()));
		}
	}

	private void loadModules(Long projectId) {
		Project project1 = (Project) session.get(Project.class, projectId);
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
			List<UIComponent> listOfUIComponents = module.getChildren();
			listOfUIComponents.clear();
			listOfUIComponents.add(selectItems);
		}
	}

	public HtmlSelectOneMenu getModule() {
		return module;
	}

	public void setModule(HtmlSelectOneMenu module) {
		this.module = module;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public HtmlSelectOneMenu getProjectsListSelectOne() {
		if (projectsListSelectOne != null) {
			session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Project.class);
			List<String> users = new ArrayList<String>();
			users.addAll(AdminUser.getUser().getUserList());
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

	public EmployeeEfforts getEmployeeEffort() {
		return employeeEffort;
	}

	public void setEmployeeEffort(EmployeeEfforts employeeEffort) {
		this.employeeEffort = employeeEffort;
	}

	public String getCurrentDocMode() {
		return currentDocMode;
	}

	public void setCurrentDocMode(String currentDocMode) {
		this.currentDocMode = currentDocMode;
	}

}
