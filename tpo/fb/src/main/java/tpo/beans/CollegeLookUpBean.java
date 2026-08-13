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

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.hibernate.College;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("CollegeLookUpBean")
@Transactional(readOnly = true)
@Scope("request")
public class CollegeLookUpBean {

	private Logger logger = LoggerFactory.getLogger(CollegeLookUpBean.class);

	private String collegeCode;

	private String collegeName;

	private String address;

	private String city;

	private List<College> collegeList = null;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private Pagination pagination;

	public List<College> getCollegeList() {
		return collegeList;
	}

	public void searchRecord() {
		if (((collegeCode != null && !collegeCode.equals("")) || (collegeName != null && !collegeName.equals(""))
				|| (address != null && !address.equals("")) || (city != null && !city.equals("")))) {
			Pagination pagination = Pagination.getPagination();
			pagination.resetCurrentPage();
		} else {
			UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error1));
		}

	}

	public String getCollegeCode() {
		return collegeCode;
	}

	public void setCollegeCode(String collegeCode) {
		this.collegeCode = collegeCode;
	}

	public String getCollegeName() {
		return collegeName;
	}

	public void setCollegeName(String collegeName) {
		this.collegeName = collegeName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@SuppressWarnings("unchecked")
	public void inIt() {
		if (((collegeCode != null && !collegeCode.equals("")) || (collegeName != null && !collegeName.equals(""))
				|| (address != null && !address.equals("")) || (city != null && !city.equals("")))) {
			try {
				Session session = sessionFactory.getCurrentSession();
				Criteria criteria = session.createCriteria(College.class);
				String queryStr = "select count(collegeName) from college where 1=1";
				AdminUser user = AdminUser.getUser();
				if (user != null && user.getUserName() != null && !"".equals(user.getUserName())) {
					List<String> userList = AdminUser.getUser().getUserList();
					criteria.add(Restrictions.in("logindetails.userName", userList));
					queryStr = queryStr + " and  userName in ("	+ TpoUtil.getComaSeprateValue(userList) + ")";
				}
				if (collegeCode != null && !collegeCode.equals("")) {
					if ("*".equals(collegeCode) || "all".equalsIgnoreCase(collegeCode)) {
						//do Nothing
					}else{
						criteria.add(Restrictions.ilike("collegeName", "%" + collegeCode + "%"));
						queryStr = queryStr + " and  collegeName like '%" + collegeCode + "%'";
					}
				}
				if (collegeName != null && !collegeName.equals("")) {
					if ("*".equals(collegeName) || "all".equalsIgnoreCase(collegeName)) {
						//do Nothing
					}else{
						criteria.add(Restrictions.ilike("collegeFullName", "%" + collegeName + "%"));
						queryStr = queryStr + " and  collegeFullName like '%" + collegeName + "%'";
					}
					
				}
				if (address != null && !address.equals("")) {
					if ("*".equals(address) || "all".equalsIgnoreCase(address)) {
						//do Nothing
					}else{
						criteria.add(Restrictions.ilike("address", "%" + address + "%"));
						queryStr = queryStr + " and  address like '%" + address + "%'";
					}
					
				}
				if (city != null && !city.equals("")) {
					if ("*".equals(city) || "all".equalsIgnoreCase(city)) {
						//do Nothing
					}else{
						criteria.add(Restrictions.ilike("place", "%" + city + "%"));
						queryStr = queryStr + " and  place like '%" + city + "%'";
					}
					
				}
				NativeQuery<BigInteger> query = session.createSQLQuery(queryStr);
				criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
				criteria.setMaxResults(pagination.getPageSize());
				BigInteger totalCount = (BigInteger) query.uniqueResult();
				pagination.setTotalDisplayRecords(totalCount.intValue());
				collegeList = criteria.list();
			} catch (HibernateException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}
}