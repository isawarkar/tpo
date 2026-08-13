/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import java.math.BigInteger;
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
import org.springframework.transaction.annotation.Transactional;

import tpo.dao.CommonDBBean;
import tpo.hibernate.College;
import tpo.hibernate.Personalinfo;
import tpo.hibernate.Registration;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("PlacedStudentTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class PlacedStudentTableBean {

	private Logger logger = LoggerFactory.getLogger(PlacedStudentTableBean.class);

	private String rollNumber;
	private String collegeName;
	private String companyName;

	private College college;
	
	private List<Registration> selectedStudenets;

	private List<Personalinfo> palcedList = null;
	private List<String> selectedStudenetList = null;

	private String fileName;

	public byte[] getImageBytes() {
		byte[] b = null;
		try {
			StudentRegistrationBean bean = (StudentRegistrationBean) TpoUtil
					.getManagedBean(StudentRegistrationBean.class.getSimpleName());
			if (bean != null && bean.getRegistration() != null) {
				CommonDBBean commonDBBean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
				if (commonDBBean != null) {
					b = commonDBBean.getStudentProfilePic(fileName);
					if (b == null) {
						b = TpoUtil.convertInputStreamToBytesArray(TpoUtil.getNAFile());
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return b;
	}

	public void setFileName(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
						List<UIComponent> list = link.getChildren();
						UIParameter parameter = (UIParameter) list.get(0);
						fileName = (String) parameter.getValue();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setCollege(AjaxActionEvent event) {
		try {
			if (event != null) {
				Session session = null;
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
						session = sessionFactory.getCurrentSession();
						college = (College) session.get(College.class,
								(String) link.getValue());
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

	

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private Pagination pagination;

	public List<Personalinfo> getPlacedList() {
		return palcedList;
	}

	public String getRollNumber() {
		return rollNumber;
	}

	public void setRollNumber(String rollNumber) {
		this.rollNumber = rollNumber;
	}

	public String getCollegeName() {
		return collegeName;
	}

	public void setCollegeName(String collegeName) {
		this.collegeName = collegeName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void searchRecord() {
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
	}

	public void inIt() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Personalinfo.class);
			criteria.createAlias("registration", "registration");
			criteria.add(Restrictions.ne("companyName", ""));
			StringBuffer quertStr = new StringBuffer(
					"select count(r.rollnumber) from personalinfo p,registration r where r.rollnumber = p.rollnumber and  p.companyName != ''");
			if (rollNumber != null && !rollNumber.equals("")) {
				criteria.add(Restrictions.eq("rollnumber", rollNumber)
						.ignoreCase());
				quertStr.append(" and r.rollnumber = '" + rollNumber + "'");
			}
			if (collegeName != null && !collegeName.equals("")) {
				criteria.add(Restrictions.eq("registration.collegeName",
						collegeName));
				quertStr.append(" and r.collegeName = '" + collegeName + "'");
			}
			if (companyName != null && !companyName.equals("")) {
				criteria.add(Restrictions.ilike("companyName", "%"
						+ companyName + "%"));
				quertStr.append(" and p.companyName like '%" + companyName
						+ "%'");
			}
			NativeQuery<BigInteger> query = session.createSQLQuery(quertStr.toString());
			criteria.addOrder(Order.desc("lastUpdated"));
			criteria.setFirstResult(pagination.getPageSize()
					* (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			palcedList = criteria.list();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public List<String> getSelectedStudenetList() {
		try {
			if (selectedStudenetList == null) {
				Session session = sessionFactory.getCurrentSession();
				NativeQuery<String> query = session.createSQLQuery("select r.rollnumber from personalinfo p, registration r where r.rollnumber = p.rollnumber and  p.companyName != '' order by r.lastUpdated desc");
				query.setMaxResults(100);
				selectedStudenetList = query.list();
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return selectedStudenetList;
	}
	
	
	
	
	public void setSelectedStudenetList(List<String> selectedStudenetList) {
		this.selectedStudenetList = selectedStudenetList;
	}

	public void setSelectedStudenets(List<Registration> selectedStudenets) {
		this.selectedStudenets = selectedStudenets;
	}

	public List<Registration> getSelectedStudenets() {
		List<Registration> sstudent= null;
		try {
			Session session = sessionFactory.getCurrentSession();
				Criteria criteria = session.createCriteria(Registration.class).createAlias("personalinfo", "p");
				criteria.add(Restrictions.ne("p.companyName",""));
				criteria.addOrder(Order.desc("lastUpdated"));
				sstudent =  criteria.list();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return sstudent;
	}

	public College getCollege() {
		return college;
	}

	public void setCollege(College college) {
		this.college = college;
	}


}