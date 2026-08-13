package tpo.beans;

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
import tpo.hibernate.annotation.BroadCastMessage;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;

@Repository("BroadCastMessageBean")
@Transactional(readOnly = true)
@Scope("session")
public class BroadCastMessageBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(BroadCastMessageBean.class);

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private CommonDBBean commonDBBean;

	private BroadCastMessage broadCastMessage;

	private String currentDocMode = CCPConstant.CREATE;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String saveRecord() {
		Session session;
		try {
			session = sessionFactory.getCurrentSession();
			AdminUser user = AdminUser.getUser();
			if (user != null) {
				if (CCPConstant.CREATE.equals(currentDocMode)) {
					BroadCastMessage broadCastMessageNew = commonDBBean.getCurrentBroadCastMessage(user.getUserName(),false);
					if(broadCastMessageNew != null) {
						this.broadCastMessage = broadCastMessageNew;
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("messageAlreadyExist"));
						currentDocMode = CCPConstant.UPDATE;
						return "";
					}
					broadCastMessage.setUserName(user.getUserName());
					session.save(broadCastMessage);
					currentDocMode = CCPConstant.UPDATE;
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("messageAddedSuccessfuly"));
				} else {
					session.update(broadCastMessage);
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("messageUpdatedSuccessfuly"));
				}
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
		return "";
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String deleteMessage() {
		Session session;
		try {
			session = sessionFactory.getCurrentSession();
			if (session != null && broadCastMessage != null) {
				session.delete(broadCastMessage);
				currentDocMode = CCPConstant.CREATE;
				broadCastMessage =new BroadCastMessage();
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("messageDeletedSuccessfuly"));
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
		return "";
	}

	public BroadCastMessage getBroadCastMessage() {
		return broadCastMessage;
	}

	public void setBroadCastMessage(BroadCastMessage broadCastMessage) {
		this.broadCastMessage = broadCastMessage;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public String getCurrentDocMode() {
		return currentDocMode;
	}

	public void setCurrentDocMode(String currentDocMode) {
		this.currentDocMode = currentDocMode;
	}

}
