/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

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
import tpo.hibernate.College;
import tpo.hibernate.Logindetails;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;

/**
 * @author Uddanda Technologies
 */
@Repository("CollegeBean")
@Transactional(readOnly = true)
@Scope("session")
public class CollegeBean {

	private Logger logger = LoggerFactory.getLogger(CollegeBean.class);

	private College college;

	private String currentDocMode = CCPConstant.CREATE;

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addCollege() {
		try {
			Session session = sessionFactory.getCurrentSession();
			if (CCPConstant.CREATE.equals(currentDocMode)) {
				Criteria criteria = session.createCriteria(College.class).setProjection(Projections.property("collegeName"));
				criteria.add(Restrictions.eq("collegeName", college.getCollegeName()));
				String collegeName = (String)criteria.uniqueResult();
				if (collegeName == null) {
					Logindetails logindetails = (Logindetails) session.get(
							Logindetails.class, AdminUser.getUser()
									.getUserName());
					college.setLogindetails(logindetails);
					session.save(college);
					currentDocMode = CCPConstant.UPDATE;
					UIBackingBean.setSuccessMessage(FbMessageUtil
							.getLabel(ResourceID.Success10));
				} else {
					UIBackingBean.setErrorMessage(FbMessageUtil
							.getLabel(ResourceID.Error24));
				}
			} else {
				session.update(college);
				UIBackingBean.setSuccessMessage(FbMessageUtil
						.getLabel(ResourceID.Success11));
			}

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public College getCollege() {
		return college;
	}

	public void setCollege(College college) {
		this.college = college;
	}

	public String getCurrentDocMode() {
		return currentDocMode;
	}

	public void setCurrentDocMode(String currentDocMode) {
		this.currentDocMode = currentDocMode;
	}

}
