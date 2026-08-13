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

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
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
import tpo.hibernate.College;
import tpo.pdf.generator.PDFGenerator;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("CollegeTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class CollegeTableBean {

	private Logger logger = LoggerFactory.getLogger(CollegeTableBean.class);

	private String collegeName;

	private List<College> collegeList = null;

	private List<College> selectedCollegeList = new ArrayList<College>();

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private Pagination pagination;
	
	@Autowired
	private PDFGenerator pDFGenerator;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSeletedRecord() {
		try {
			if (selectedCollegeList != null && selectedCollegeList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil
						.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				StringBuffer query = new StringBuffer(
						"select count(rollnumber) from registration where collegeName=");
				for (College college : selectedCollegeList) {
					query.append("'" + college.getCollegeName() + "'");
					BigInteger count = (BigInteger) session.createSQLQuery(
							query.toString()).uniqueResult();
					if (count.intValue() > 0) {
						String param[] = new String[2];
						param[0] = college.getCollegeName();
						param[1] = String.valueOf(count);
						UIBackingBean
								.setErrorMessage(FbMessageUtil.getLabel("delete_College",param));
						return;
					} else {
						session.delete(college);
					}
				}
				collegeList = null;
				UIBackingBean.setSuccessMessage(FbMessageUtil
						.getLabel(ResourceID.Success13));
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
	public void deleteSeletedRecordWithStudents() {
		try {
			if (selectedCollegeList != null && selectedCollegeList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil
						.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				StringBuffer query = new StringBuffer(
						"delete from registration where collegeName=");
				for (College college : selectedCollegeList) {
					query.append("'" + college.getCollegeName() + "'");
					int count = session.createSQLQuery(query.toString())
							.executeUpdate();
					if (count > 0) {
						session.delete(college);
					}
				}
				collegeList = null;
				UIBackingBean.setSuccessMessage(FbMessageUtil
						.getLabel(ResourceID.Success13));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public List<College> getCollegeList() {

		return collegeList;
	}

	public void searchRecord() {
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
	}

	public String getCollegeName() {
		return collegeName;
	}

	public void setCollegeName(String collegeName) {
		this.collegeName = collegeName;
	}

	public List<College> getSelectedCollegeList() {
		return selectedCollegeList;
	}

	public void setSelectedCollegeList(List<College> selectedCollegeList) {
		this.selectedCollegeList = selectedCollegeList;
	}

	public void setCollege(AjaxActionEvent event) {
		try {
			CollegeBean bean = (CollegeBean) TpoUtil
					.getManagedBean(CollegeBean.class.getSimpleName());
			if (selectedCollegeList != null && selectedCollegeList.size() == 0) {
				UIBackingBean
						.setInfoMessage(FbMessageUtil.getLabel("Info12"));
				if (bean != null) {
					bean.setCollege(null);
				}
			} else {
				if (selectedCollegeList.size() > 1) {
					UIBackingBean
							.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
					if (bean != null) {
						bean.setCollege(null);
					}
				} else {
					if (bean != null) {
						bean.setCollege(selectedCollegeList.get(0));
						bean.setCurrentDocMode(CCPConstant.UPDATE);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setCollegeToAdd(AjaxActionEvent event) {
		CollegeBean bean = (CollegeBean) TpoUtil
				.getManagedBean(CollegeBean.class.getSimpleName());
		if (bean != null) {
			bean.setCurrentDocMode(CCPConstant.CREATE);
			bean.setCollege(new College());
		}
	}

	public void inIt() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(College.class);
			String queryStr = "select count(collegeName) from college where 1=1";
			AdminUser user = AdminUser.getUser();
			if (user != null && user.getUserName() != null
					&& !"".equals(user.getUserName())) {
				List<String> userList = AdminUser.getUser().getUserList();
				criteria.add(Restrictions.in("logindetails.userName",
						userList));
				queryStr = queryStr + " and  userName in ("	+ TpoUtil.getComaSeprateValue(userList) + ")";
			}
			if (collegeName != null && !collegeName.equals("")) {
				criteria.add(Restrictions.ilike("collegeName", "%"
						+ collegeName + "%"));
				queryStr = queryStr + " and  collegeName like '%" + collegeName
						+ "%'";
			}

			NativeQuery<BigInteger> query = session.createSQLQuery(queryStr);
			criteria.addOrder(Order.asc("collegeName"));
			criteria.setFirstResult(pagination.getPageSize()
					* (pagination.getCurrentPage() - 1));
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
	
	public void generateXls() {
		try {
			if (collegeList != null && !collegeList.isEmpty()) {
				String reportName = "FB_" +  "CollegeList_" + AdminUser.getUser().getUserName() + "_" + TpoUtil.getDateToStringYYYYMMdd(new Date());
				TpoUtil.renderEXcelFile(ExcelHandler.generateCollegeXls((selectedCollegeList != null && selectedCollegeList.size() >0)?selectedCollegeList:collegeList, reportName),reportName);
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
			if (collegeList != null && !collegeList.isEmpty()) {
				pDFGenerator.generateCollegeList((selectedCollegeList != null && selectedCollegeList.size() >0)?selectedCollegeList:collegeList);
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
}