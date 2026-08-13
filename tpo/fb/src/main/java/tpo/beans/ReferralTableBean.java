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
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.admin.excel.ExcelHandler;
import tpo.hibernate.annotation.ReferralHistory;
import tpo.pdf.generator.PDFGenerator;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("ReferralTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class ReferralTableBean {

	private Logger logger = LoggerFactory.getLogger(ReferralTableBean.class);

	private String referred;
	private String referredBY;
	protected Date date;
	protected Date toDate;

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private PDFGenerator pDFGenerator;

	@Autowired
	private Pagination pagination;

	private List<ReferralHistory> referralList;
	private List<ReferralHistory> selectedList = new ArrayList<ReferralHistory>();

	public void updateRecord() {

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSeletedRecord() {
		try {
			if (selectedList != null && selectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (ReferralHistory referralHistory : selectedList) {
					session.delete(referralHistory);
				}
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("referraldetailsDeletedSuccessfully"));
				referralList = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
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
			Criteria criteria = session.createCriteria(ReferralHistory.class);
			String queryStr = "select count(referred) from referralhistory where 1=1";
			
			if (referred != null && !referred.equals("")) {
				criteria.add(Restrictions.ilike("referred", "%" + referred + "%"));
				queryStr = queryStr + " and referred like '%" + referred + "%'";
			}
			if (referredBY != null && !referredBY.equals("")) {
				criteria.add(Restrictions.ilike("referredBY", "%" + referredBY + "%"));
				queryStr = queryStr + " and referredBY like '%" + referredBY + "%'";
			}
			
			if (date != null && !date.equals("") && toDate != null && !toDate.equals("")) {
				String strDate = TpoUtil.getDateToStringYYYYMMdd(date);
				String endDate = TpoUtil.getDateToStringYYYYMMdd(toDate);
				criteria.add(Restrictions.ge("date", date));
				criteria.add(Restrictions.le("date", toDate));
				queryStr = queryStr + " and date between STR_TO_DATE('"+strDate+"', '%Y-%m-%d') and STR_TO_DATE('"+endDate+"', '%Y-%m-%d')";
			}else if (date != null && !date.equals("") &&  (toDate == null || toDate.equals("")) ) {
				String strDate = TpoUtil.getDateToStringYYYYMMdd(date);
				criteria.add(Restrictions.eq("date", date));
				queryStr = queryStr + " and date like '%" + strDate + "%'";
			}
			
			NativeQuery<BigInteger> query = session.createSQLQuery(queryStr);
			criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			referralList = criteria.list();
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
			if (referralList != null && !referralList.isEmpty()) {
				String reportName = "FB_" +  "ReferralList_" + AdminUser.getUser().getUserName() + "_" + TpoUtil.getDateToStringYYYYMMdd(new Date());
				TpoUtil.renderEXcelFile(ExcelHandler.generateReferralXls((selectedList != null && selectedList.size() >0)?selectedList:referralList, reportName),reportName);
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
			if (referralList != null && !referralList.isEmpty()) {
				pDFGenerator.generateReferralList((selectedList != null && selectedList.size() >0)?selectedList:referralList);
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

	public List<ReferralHistory> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(List<ReferralHistory> selectedList) {
		this.selectedList = selectedList;
	}

	public List<ReferralHistory> getReferralList() {
		return referralList;
	}

	public void setReferralList(List<ReferralHistory> referralList) {
		this.referralList = referralList;
	}

	public String getReferred() {
		return referred;
	}

	public void setReferred(String referred) {
		this.referred = referred;
	}

	public String getReferredBY() {
		return referredBY;
	}

	public void setReferredBY(String referredBY) {
		this.referredBY = referredBY;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public void clear(){
		referred=null;
		referredBY=null;
		date=null;
		toDate=null;
	}

}