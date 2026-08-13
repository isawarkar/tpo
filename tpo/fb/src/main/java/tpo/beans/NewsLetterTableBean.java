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

import tpo.hibernate.annotation.NewsLetter;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;

/**
 * @author Uddanda Technologies
 */
@Repository("NewsLetterTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class NewsLetterTableBean {

	private Logger logger = LoggerFactory.getLogger(ShortRecordTableBean.class);

	private String email;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private Pagination pagination;

	private List<NewsLetter> newsLetterList = null;

	private List<NewsLetter> selectedNewsLetterList = new ArrayList<NewsLetter>();

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSeletedRecord() {
		try {
			if (selectedNewsLetterList != null && selectedNewsLetterList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (NewsLetter letter : selectedNewsLetterList) {
					session.delete(letter);
				}
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success1));
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
			Criteria criteria = session.createCriteria(NewsLetter.class);
			String sqlStr = "select count(email) from news_letter where 1=1";

			if (email != null && !email.equals("")) {
				criteria.add(Restrictions.ilike("email", "%" + email + "%"));
				sqlStr = sqlStr + " and email like '%" + email + "%'";
			}
			criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			NativeQuery<BigInteger> query = session.createSQLQuery(sqlStr);
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			newsLetterList = criteria.list();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<NewsLetter> getNewsLetterList() {
		return newsLetterList;
	}

	public void setNewsLetterList(List<NewsLetter> newsLetterList) {
		this.newsLetterList = newsLetterList;
	}

	public List<NewsLetter> getSelectedNewsLetterList() {
		return selectedNewsLetterList;
	}

	public void setSelectedNewsLetterList(List<NewsLetter> selectedNewsLetterList) {
		this.selectedNewsLetterList = selectedNewsLetterList;
	}
	
	

}
