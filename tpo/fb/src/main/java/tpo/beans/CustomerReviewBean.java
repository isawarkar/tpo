/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.dao.CommonDBBean;
import tpo.hibernate.annotation.CustomerReview;
import tpo.util.FbMessageUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("CustomerReviewBean")
@Transactional(readOnly = true)
@Scope("session")
public class CustomerReviewBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(CustomerReviewBean.class);

	private String comment;
	private Integer starRating;

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private CommonDBBean commonDBBean;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String addReview() {
		try {
			Session session = sessionFactory.getCurrentSession();
			AdminUser user = AdminUser.getUser();
			CustomerReview customerReview = isRecordExist(user.getUserName());
			if (customerReview == null) {
				customerReview = new CustomerReview();
			}
			customerReview.setUserName(user.getUserName());
			customerReview.setComment(comment);
			customerReview.setStarRating(starRating);
			customerReview.setFullName(user.getUser().getFullName());
			session.saveOrUpdate(customerReview);
			
			if(commonDBBean != null) {
				commonDBBean.loadReviews();
			}
			UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Your_review_is_successfully_saved_Thank_for_your_review"));
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return "review";
	}

	public CustomerReview isRecordExist(String userName) {
		Session session = sessionFactory.getCurrentSession();
		CustomerReview customerReview = (CustomerReview) session.get(CustomerReview.class, userName);
		return customerReview;
	}

	public List<CustomerReview> getReviewList() {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(CustomerReview.class);
		List<CustomerReview> customerReviews = criteria.list();
		return customerReviews;
	}

	public int getRating() {
		int avgRating = 0;
		List<CustomerReview> customerReviews = getReviewList();
		if(customerReviews != null && customerReviews.size() > 0){
			int i = 0;
		for (CustomerReview customerReview : customerReviews) {
			i += customerReview.getStarRating();
		}
		
		avgRating = i/customerReviews.size();
		}
		
		return avgRating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getStarRating() {
		return starRating;
	}

	public void setStarRating(Integer starRating) {
		this.starRating = starRating;
	}

}
