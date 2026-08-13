/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

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

import tpo.admin.beans.EmailAttachmentBean;
import tpo.email.EmailUtil;
import tpo.hibernate.annotation.NewsLetter;
import tpo.util.FbMessageUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("NewsLetterBean")
@Transactional(readOnly = true)
@Scope("request")
public class NewsLetterBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(NewsLetterBean.class);

	private String email;

	private String message;

	@Autowired
	private EmailAttachmentBean emailAttachmentBean;

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addEmail() {
		try {
			Session session = sessionFactory.getCurrentSession();
			NewsLetter letter = new NewsLetter();
			letter.setEmail(email);
			session.save(letter);
			UIBackingBean.setSuccessMessage(FbMessageUtil
					.getLabel("You_are_successfully_Subscribed_to_Fresher_Buddy_campus_events_and_news_letter"));
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void sendMessage() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(NewsLetter.class);
			List<NewsLetter> list = criteria.list();
			List<String> email = new ArrayList<String>(1);
			if (list != null && list.size() > 0) {
				EmailUtil emailUtill = getEmailInstance();
				if (emailUtill != null) {
					StringBuffer newMessage = null;
					for (NewsLetter nl : list) {
						newMessage = new StringBuffer(message);
						newMessage.append(TpoUtil.getMesageString().toString());
						// String emailAddress =
						// AES.symmetricEncrypt(nl.getEmail(),
						// TpoUtil.geyKeyInfo());
						newMessage.append(
								"<br><br><a href='" + TpoUtil.getBasePath(null) + "xhtml/unsubscribe.faces?email="
										+ nl.getEmail() + "'>" + FbMessageUtil.getLabel("unsubscribe") + "</a><br>");
						if (emailAttachmentBean.getFileList() != null && emailAttachmentBean.getFileList().size() > 0) {
							newMessage.append(FbMessageUtil.getLabel("please_find_attachment"));
						}
						emailUtill.sendEmailWithAttachment(TpoUtil.ADMIN_EMAIL, nl.getEmail(), newMessage.toString(),
								FbMessageUtil.getLabel("message_from_buddy"), emailAttachmentBean.getFileList());
						emailAttachmentBean.setFileList(null);
					}
				}
				Object param[] = new Object[2];
				param[0] = list.size();
				param[1] = list.size();
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Message_has_been_sent_to", param));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {

			e.printStackTrace();
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isRecordExist(String email) {
		Session session = sessionFactory.getCurrentSession();
		NewsLetter letter = (NewsLetter) session.get(NewsLetter.class, email);
		if (letter != null) {
			return true;
		}
		return false;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public EmailAttachmentBean getEmailAttachmentBean() {
		return emailAttachmentBean;
	}

	public void setEmailAttachmentBean(EmailAttachmentBean emailAttachmentBean) {
		this.emailAttachmentBean = emailAttachmentBean;
	}

}
