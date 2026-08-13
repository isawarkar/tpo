/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
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
import tpo.email.EmailUtil;
import tpo.hibernate.College;
import tpo.hibernate.Userdetails;
import tpo.hibernate.annotation.CollegeGroup;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("CollegeGroupBean")
@Transactional(readOnly = true)
@Scope("session")
public class CollegeGroupBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(CollegeGroupBean.class);

	private List<String> selectedCollegeList;

	private List<CollegeGroup> collegeGroupList = null;

	private List<CollegeGroup> selectedCollegeGroupList = null;

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public void inIt() {
		try {
			collegeGroupList = null;
			Session session = sessionFactory.getCurrentSession();
			AdminUser user = AdminUser.getUser();
			List<String> collegeList = null;
			if (user != null) {

				// Received Requests
				Criteria criteria = session.createCriteria(CollegeGroup.class);
				NativeQuery<String> query = session.createSQLQuery(
						"select CollegeName from college where userName = '" + user.getUserName() + "'");
				collegeList = query.list();
				if (collegeList != null && collegeList.size() > 0) {
					criteria.add(Restrictions.in("collegeName", collegeList));
					criteria.addOrder(Order.asc("collegeName"));
					collegeGroupList = criteria.list();
				}
				if (collegeGroupList != null && !collegeGroupList.isEmpty()) {
					for (CollegeGroup collegeGroup : collegeGroupList) {
						collegeGroup.setType("R");
					}
				}else {
					collegeGroupList = new ArrayList<CollegeGroup>();
				}

				// Sent Requests
				criteria = session.createCriteria(CollegeGroup.class);
				criteria.add(Restrictions.eq("userName", user.getUserName()));
				criteria.addOrder(Order.asc("collegeName"));
				List<CollegeGroup> collegeGroupListSent = criteria.list();
				if (collegeGroupListSent != null && !collegeGroupListSent.isEmpty()) {
					for (CollegeGroup collegeGroup : collegeGroupListSent) {
						collegeGroup.setType("S");
					}
					collegeGroupList.addAll(collegeGroupListSent);
				}

				NativeQuery<College> query1 = session.createSQLQuery(
						"SELECT * FROM college where CollegeName not in(select CollegeName from collegegroup where userName = '"
								+ user.getUserName()
								+ "') and CollegeName not in(select CollegeName from college where userName = '"
								+ user.getUserName() + "')");
				query1.addEntity(College.class);
				List<College> list = query1.list();
				if (list != null && !list.isEmpty()) {
					CollegeGroup collegeGroup;
					for (College college : list) {
						collegeGroup = new CollegeGroup();
						collegeGroup.setId(TpoUtil.getRandomNumber());
						collegeGroup.setCollegeName(college.getCollegeName());
						collegeGroup.setStatus(null);
						collegeGroup.setType("N");
						collegeGroupList.add(collegeGroup);
					}
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

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void changeStatus(String status) {
		try {
			if (selectedCollegeGroupList != null && selectedCollegeGroupList.isEmpty()) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Info12"));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (CollegeGroup collegeGroup : selectedCollegeGroupList) {
					if (!"R".equals(collegeGroup.getType())) {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("please_select_only_received_records"));
						return;
					}
				}
				EmailUtil emailUtill = getEmailInstance();
				List<String> recipients = null;
				for (CollegeGroup collegeGroup : selectedCollegeGroupList) {
					if ("A".equals(status)) {
						UIBackingBean
								.setSuccessMessage(FbMessageUtil.getLabel("Selected_record_has_been_now_Approved"));
					} else {
						UIBackingBean
								.setSuccessMessage(FbMessageUtil.getLabel("Selected_record_has_been_now_Rejected"));
					}
					collegeGroup.setStatus(status);
					session.update(collegeGroup);
					Userdetails userdetail = session.get(Userdetails.class, collegeGroup.getUserName());
					if (emailUtill != null && userdetail != null) {
						recipients = new ArrayList<String>(1);
						recipients.add(userdetail.getEmail());
						String subject = "Your College Group request has been "
								+ String.valueOf("A".equals(status) ? "Approved by " : "Rejected by ")
								+ collegeGroup.getCollegeName();
						StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear")
								+ userdetail.getLastName() + " " + userdetail.getFirstName());
						if ("A".equals(status)) {
							message.append(",<br><br>").append("<font color=green size=5>" + subject + "</font><br>");
						} else {
							message.append(",<br><br>").append("<font color=red size=5>" + subject + "</font><br>");
						}
						message.append(TpoUtil.getMesageString());
						emailUtill.postMail(recipients, subject, message.toString(), TpoUtil.ADMIN_EMAIL,
								Message.RecipientType.TO);
					}
				}
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

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSelectedRequest() {
		try {
			if (selectedCollegeGroupList != null && selectedCollegeGroupList.isEmpty()) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (CollegeGroup collegeGroup : selectedCollegeGroupList) {
					if (!"S".equals(collegeGroup.getType())) {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("please_select_only_sent_records"));
						return;
					}
				}
				for (CollegeGroup collegeGroup : selectedCollegeGroupList) {
					session.delete(collegeGroup);
				}
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success1));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void sendRequest() {
		try {
			Session session = sessionFactory.getCurrentSession();
			for (CollegeGroup collegeGroup : selectedCollegeGroupList) {
				if (!"N".equals(collegeGroup.getType())) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("please_select_only_not_submitted_records"));
					return;
				}
			}
			for (CollegeGroup collegeGroup : selectedCollegeGroupList) {
				collegeGroup.setStatus("P");
				collegeGroup.setId(null);
				collegeGroup.setUserName(AdminUser.getUser().getUserName());
				collegeGroup.setDate(Calendar.getInstance().getTime());
				session.save(collegeGroup);
				NativeQuery<String> query = session.createSQLQuery(
						"select emailAddress from college where CollegeName = '" + collegeGroup.getCollegeName() + "'");
				String emailAddress = (String) query.uniqueResult();
				if (emailAddress != null) {
					EmailUtil emailUtill = getEmailInstance();
					if (emailUtill != null) {
						List<String> recipients = new ArrayList<String>(1);
						recipients.add(emailAddress);
						String subject = FbMessageUtil.getLabel("Freshers_Buddy_Group_Request");
						StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Dear_Sir"));
						message.append(",<br><br>")
								.append("<font color=red size=5>"
										+ FbMessageUtil.getLabel("Freshers_Buddy_group_request_has_been_sent_by")
										+ collegeGroup.getUserName() + "</font><br>");
						String style = "display: block;width: 115px;height: 25px;background: #4E9CAF;padding: 10px;text-align: center;border-radius: 5px;color: white;font-weight: bold;";
						String approveAction = TpoUtil.getBasePath(null)
								+ "xhtml/collegeGroupRequest.faces?pnsgffmffbhvgkbf=" + collegeGroup.getCollegeName()
								+ "&dfdnmfbnndfn=" + collegeGroup.getUserName() + "&fgfddgfdgdfgtdgbvcbdg=Approve"
								+ "&dfgdgfdgfdgfdgdg=" + collegeGroup.getId() + "";
						String rejectAction = TpoUtil.getBasePath(null)
								+ "xhtml/collegeGroupRequest.faces?pnsgffmffbhvgkbf=" + collegeGroup.getCollegeName()
								+ "&dfdnmfbnndfn=" + collegeGroup.getUserName() + "&fgfddgfdgdfgtdgbvcbdg=Reject"
								+ "&dfgdgfdgfdgfdgdg=" + collegeGroup.getId() + "";

						message.append("<br><table width='300px;'><tr><td><a href='" + approveAction + "' style='"
								+ style + "'>Approve</a></td><td><a href='" + rejectAction + "' style='" + style
								+ "'>Reject</a></td></tr></table>");
						message.append(TpoUtil.getMesageString());
						emailUtill.postMail(recipients, subject, message.toString(), TpoUtil.ADMIN_EMAIL,
								Message.RecipientType.TO);

					}
				}
				UIBackingBean.setSuccessMessage(
						FbMessageUtil.getLabel("You_request_has_been_sent_to", collegeGroup.getCollegeName()));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public List<CollegeGroup> getCollegeGroupList() {
		return collegeGroupList;
	}

	public void setCollegeGroupList(List<CollegeGroup> collegeGroupList) {
		this.collegeGroupList = collegeGroupList;
	}

	public List<CollegeGroup> getSelectedCollegeGroupList() {
		return selectedCollegeGroupList;
	}

	public void setSelectedCollegeGroupList(List<CollegeGroup> selectedCollegeGroupList) {
		this.selectedCollegeGroupList = selectedCollegeGroupList;
	}

	public List<String> getSelectedCollegeList() {
		return selectedCollegeList;
	}

	public void setSelectedCollegeList(List<String> selectedCollegeList) {
		this.selectedCollegeList = selectedCollegeList;
	}
}
