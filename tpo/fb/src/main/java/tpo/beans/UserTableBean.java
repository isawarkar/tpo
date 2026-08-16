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
import java.util.Random;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.mail.Message;

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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.admin.excel.ExcelHandler;
import tpo.email.EmailUtil;
import tpo.ets.beans.EffortReportTableBean;
import tpo.hibernate.College;
import tpo.hibernate.EmployeeEfforts;
import tpo.hibernate.Logindetails;
import tpo.hibernate.Userdetails;
import tpo.pdf.generator.PDFGenerator;
import tpo.util.CCPConstant;
import tpo.util.Encryption;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("UserTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class UserTableBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(UserTableBean.class);

	private String userName;

	private Boolean showPopUp;

	private String password;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private PDFGenerator pDFGenerator;

	@Autowired
	private Pagination pagination;

	private List<Logindetails> userList = null;

	private Logindetails selectedUserToForceDelete;

	private List<Logindetails> selectedUserList = new ArrayList<Logindetails>();

	private Logindetails user;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void changeStatus() {
		try {
			Session session = sessionFactory.getCurrentSession();
			if (selectedUserList != null && selectedUserList.size() > 0) {
				for (Logindetails logindetail : selectedUserList) {

					if (logindetail.getActive()) {
						logindetail.setActive(false);
						if (!CCPConstant.SUPERUSER.equals(logindetail.getRole())) {
							Criteria criteria = session.createCriteria(Logindetails.class);
							criteria.add(Restrictions.eq("createdBy", logindetail.getUserName()));
							List<Logindetails> list = criteria.list();
							for (Logindetails logindetailsObj : list) {
								logindetailsObj.setActive(false);
								session.update(logindetailsObj);
							}
						}
					} else {
						logindetail.setActive(true);
						if (!CCPConstant.SUPERUSER.equals(logindetail.getRole())) {
							Criteria criteria = session.createCriteria(Logindetails.class);
							criteria.add(Restrictions.eq("createdBy", logindetail.getUserName()));
							List<Logindetails> list = criteria.list();
							for (Logindetails logindetailsObj : list) {
								logindetailsObj.setActive(true);
								logindetailsObj.setLoginAttempt(0);
								session.update(logindetailsObj);
							}
						}

					}
					session.update(logindetail);
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Status_changed_successfully"));
					userList = null;
				}
			} else {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
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

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSeletedRecord() {
		try {
			Session session = sessionFactory.getCurrentSession();
			if (selectedUserList != null && selectedUserList.size() > 0) {
				for (Logindetails logindetail : selectedUserList) {
					Criteria criteria = session.createCriteria(College.class);
					criteria.add(Restrictions.eq("logindetails.userName", logindetail.getUserName()));
					List<College> colleges = criteria.list();
					if (colleges != null && colleges.size() > 0) {
						UIBackingBean.setErrorMessage(FbMessageUtil
								.getLabel("Please_delete_all_the_colleges_for_this_user_before_deleting_user"));
					} else {
						criteria = session.createCriteria(Logindetails.class);
						criteria.add(Restrictions.eq("createdBy", logindetail.getUserName()));
						List<Logindetails> list = criteria.list();
						if (list != null && list.size() > 0) {
							UIBackingBean.setErrorMessage(FbMessageUtil
									.getLabel("Please_delete_all_the_User_for_this_user_before_deleting_user"));
						} else {
							criteria = session.createCriteria(EmployeeEfforts.class);
							criteria.add(Restrictions.eq("logindetails.userName", logindetail.getUserName()));
							List<EmployeeEfforts> employeeEffortsList = criteria.list();
							if (employeeEffortsList != null && employeeEffortsList.size() > 0) {
								UIBackingBean.setErrorMessage(FbMessageUtil
										.getLabel("Please_delete_all_the_Effors_for_this_user_before_deleting_user"));
							} else {
								session.delete(logindetail);
								UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success1));
								userList = null;
							}
						}
					}
				}
			} else {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public String showInfo() {
		try {
			if (selectedUserList != null && selectedUserList.size() == 0) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_select_at_least_one_record_to_see_full_information"));
			} else {
				if (selectedUserList.size() > 1) {
					UIBackingBean.setInfoMessage(
							FbMessageUtil.getLabel("Please_select_only_one_record_to_see_full_information"));
				} else {
					Logindetails logindetails = selectedUserList.get(0);
					if (logindetails != null) {
						EffortReportTableBean bean = (EffortReportTableBean) TpoUtil
								.getManagedBean(EffortReportTableBean.class.getSimpleName());
						if (bean != null) {
							bean.setUserNameObj(logindetails.getUserName());
							return "effortsListReport";
						}
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
		return null;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void resetPassword() {
		try {
			Session session = sessionFactory.getCurrentSession();
			if (selectedUserList != null && selectedUserList.size() > 0) {
				for (Logindetails logindetail : selectedUserList) {
					Random rand = new Random();
					int random = rand.nextInt(500000);
					String tempPassword = String.valueOf(random);
					tempPassword = tempPassword + "@fb.com";
					String encryptedPassword = Encryption.getEncryptedString(tempPassword);
					logindetail.setPassword(encryptedPassword);
					logindetail.setLoginAttempt(0);
					logindetail.setActive(true);
					session.update(logindetail);
					String emailAddress = selectedUserList.get(0).getUserdetails().getEmail();
					UIBackingBean.setSuccessMessage(
							FbMessageUtil.getLabel("Password_reset_successfully_email", emailAddress));

					EmailUtil emailUtill = getEmailInstance();
					if (emailUtill != null) {
						StringBuffer message = new StringBuffer(
								FbMessageUtil.getLabel("Dear") + " " + logindetail.getUserName() + ",<br>");
						message.append(FbMessageUtil.getLabel("Your_password_has_been_changed_successfully"))
								.append("<br>");
						message.append(FbMessageUtil.getLabel("Your_User_Name_is")).append(logindetail.getUserName());
						message.append(FbMessageUtil.getLabel("Your_Password_is")).append(" ").append(tempPassword)
								.append("<br><br>");
						message.append(TpoUtil.getMesageString());
						List<String> recipients = new ArrayList<String>(2);
						recipients.add(emailAddress);
						recipients.add(superUserEmail);
						emailUtill.postMail(recipients, FbMessageUtil.getLabel("Password_Reset_Email"),
								message.toString(), TpoUtil.ADMIN_EMAIL, Message.RecipientType.BCC);
					}
					userList = null;
				}
			} else {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void forceDelete() {
		ImageBean imageBean = (ImageBean) TpoUtil.getManagedBean(ImageBean.class.getSimpleName());
		if (imageBean != null && imageBean.isImageCorrect()) {
			try {
				String encryptedPassword = Encryption.getEncryptedString(password);
				Session session = sessionFactory.getCurrentSession();
				Criteria criteria = session.createCriteria(Logindetails.class);
				criteria.add(Restrictions.eq("userName", AdminUser.getUser().getUserName()));
				criteria.add(Restrictions.eq("password", encryptedPassword));
				Logindetails userinfo = (Logindetails) criteria.uniqueResult();
				if (userinfo != null) {
					password = null;
					if (selectedUserToForceDelete != null) {
						String userName = selectedUserList.get(0).getUserName();
						StringBuffer message = new StringBuffer();
						NativeQuery<String> query = null;
						query = session
								.createSQLQuery("SELECT userName FROM logindetails where createdBy='" + userName + "'");
						List<String> userList = query.list();
						if (userList != null && userList.size() > 0) {
							userList.add(userName);
							for (String userN : userList) {
								deleteAll(userN, message, session);
								NativeQuery<?> query1 = session
										.createSQLQuery("delete FROM logindetails where userName='" + userN + "'");
								query1.executeUpdate();
							}
						} else {
							deleteAll(userName, message, session);
						}

						session.delete(selectedUserToForceDelete);
						EmailUtil emailUtill = getEmailInstance();
						if (emailUtill != null) {
							List<String> recipients = new ArrayList<String>(2);
							recipients.add(selectedUserList.get(0).getUserdetails().getEmail());
							recipients.add(superUserEmail);
							emailUtill.postMail(recipients, FbMessageUtil.getLabel("User_deleted_from_Freshers_Buddy"),
									FbMessageUtil.getLabel("User_and_its_all_the_childrens_are_deleted_successfully")
											+ "<br><br><br>" + message.toString(),
									TpoUtil.ADMIN_EMAIL, Message.RecipientType.BCC);
						}

						UIBackingBean.setSuccessMessage(
								FbMessageUtil.getLabel("User_and_its_all_the_childrens_are_deleted_successfully"));
					}
				} else {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_enter_correct_profile_password"));
				}
			} catch (HibernateException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			} catch (IndexOutOfBoundsException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		} else {
			UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_enter_correct_profile_password"));
		}
	}

	@SuppressWarnings("deprecation")
	private void deleteAll(String userName, StringBuffer message, Session session) {
		NativeQuery<?> query = session.createSQLQuery(
				"delete FROM result where loginname in(select rollnumber FROM registration where collegeName in(select collegeName FROM college where userName='"
						+ userName + "'))");
		message.append("Total Result=").append(query.executeUpdate());

		query = session.createSQLQuery(
				"delete FROM registration where collegeName in(select collegeName FROM college where userName='"
						+ userName + "')");
		message.append(",Total Student=").append(query.executeUpdate());
		query = session.createSQLQuery("delete FROM collegegroup where userName='" + userName + "'");
		message.append(",Total College Group=").append(query.executeUpdate());
		query = session.createSQLQuery("delete FROM college where userName='" + userName + "'");
		message.append(",Total College =").append(query.executeUpdate());
		query = session.createSQLQuery("delete FROM company where createdBy='" + userName + "'");
		message.append(",Total Company=").append(query.executeUpdate());
		NativeQuery<String> query1 = session
				.createSQLQuery("select testname FROM exam where createdBy='" + userName + "'");
		List<String> testNameList = query1.list();
		if (testNameList != null && testNameList.size() > 0) {
			query = session.createSQLQuery(
					"delete FROM questions where qtype in(" + TpoUtil.getComaSeprateValue(testNameList) + ")");
			message.append(",Total Questions=").append(query.executeUpdate());
		} else {
			message.append(",Total Questions=0");
		}
		query = session.createSQLQuery("delete FROM exam where createdBy='" + userName + "'");
		message.append(",Total Exam=").append(query.executeUpdate());
		query = session.createSQLQuery(
				"delete FROM hallticketconnect where hallTicketId in(select hallTicketId FROM hallticket where userName='"
						+ userName + "')");
		message.append(",Total Hallticket Records=").append(query.executeUpdate());
		query = session.createSQLQuery("delete FROM hallticket where userName='" + userName + "'");
		message.append(",Total Hallticket=").append(query.executeUpdate());
		query = session.createSQLQuery("delete FROM notice where createdBy='" + userName + "'");
		message.append(",Total Notice=").append(query.executeUpdate());

		// Employee Efforts

		query = session.createSQLQuery("delete FROM module where createdBy='" + userName + "'");
		message.append(",Total Module's=").append(query.executeUpdate());

		query = session.createSQLQuery("delete FROM project where createdBy='" + userName + "'");
		message.append(",Total Project's=").append(query.executeUpdate());

		query = session.createSQLQuery("delete FROM employee_efforts where username='" + userName + "'");
		message.append(",Total Employee efforts=").append(query.executeUpdate());

		query = session.createSQLQuery("delete FROM bookmarks where userName='" + userName + "'");
		message.append(",Total Bookmarks=").append(query.executeUpdate());

	}

	public String loginAsSelected() {
		String goTo = null;
		try {
			if (selectedUserList != null && selectedUserList.size() > 0) {
				Logindetails userinfo = selectedUserList.get(0);
				if (!CCPConstant.SUPERUSER.equals(userinfo.getRole())) {
					if (userinfo != null) {
						CCPLoginBean bean = (CCPLoginBean) TpoUtil.getManagedBean(CCPLoginBean.class.getSimpleName());
						{
							bean.setUserProfile(null, userinfo);
							goTo = bean.goToDashBoard();
						}
					}
				}
			} else {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_at_least_one_record"));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return goTo;
	}

	public List<Logindetails> getUserList() {
		return userList;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<Logindetails> getSelectedUserList() {
		return selectedUserList;
	}

	public void setSelectedUserList(List<Logindetails> selectedUserList) {
		this.selectedUserList = selectedUserList;
	}

	public void inIt() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Logindetails.class);
			criteria.add(Restrictions.eq("createdBy", AdminUser.getUser().getUserName()));
			if (userName != null && !userName.equals("")) {
				criteria.add(Restrictions.eq("userName", userName));
			}
			criteria.addOrder(Order.asc("userName"));
			criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			NativeQuery<BigInteger> query = session
					.createSQLQuery("select count(userName) from logindetails where createdBy = '"
							+ AdminUser.getUser().getUserName() + "'");
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			userList = criteria.list();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setUser(AjaxActionEvent event) {
		CreateUser bean = (CreateUser) TpoUtil.getManagedBean(CreateUser.class.getSimpleName());
		if (selectedUserList != null && selectedUserList.size() == 0) {
			UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Info12"));
			if (bean != null) {
				bean.setLogindetails(null);
			}
		} else {
			if (selectedUserList.size() > 1) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
				if (bean != null) {
					bean.setLogindetails(null);
				}
			} else {
				if (bean != null) {
					bean.setCurrentDocMode(CCPConstant.UPDATE);
					bean.setLogindetails(selectedUserList.get(0));
					bean.setUserdetails(selectedUserList.get(0).getUserdetails());
				}
			}
		}
	}

	public void setUserToAdd(AjaxActionEvent event) {
		CreateUser bean = (CreateUser) TpoUtil.getManagedBean(CreateUser.class.getSimpleName());

		if (bean != null) {
			bean.setCurrentDocMode(CCPConstant.CREATE);
			Logindetails logindetails = new Logindetails();
			bean.setLogindetails(logindetails);
			bean.setUserdetails(new Userdetails());
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getShowPopUp() {
		return showPopUp;
	}

	public void setShowPopUp(Boolean showPopUp) {
		this.showPopUp = showPopUp;
	}

	public void setPopUP(AjaxActionEvent event) {
		if (selectedUserList != null && selectedUserList.size() == 0) {
			UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			showPopUp = false;
		} else if (selectedUserList != null && selectedUserList.size() > 1) {
			UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Force_delete_can_be_done_only_one_user_at_a_time"));
			showPopUp = false;
		} else {
			selectedUserToForceDelete = selectedUserList.get(0);
		}
		showPopUp = true;
	}

	public void generateXls() {
		try {
			if (userList != null && !userList.isEmpty()) {

				String reportName = "FB_" + "UserList_" + AdminUser.getUser().getUserName() + "_"
						+ TpoUtil.getDateToStringYYYYMMdd(new Date());
				TpoUtil.renderEXcelFile(ExcelHandler.generateUserXls(
						(selectedUserList != null && selectedUserList.size() > 0) ? selectedUserList : userList,
						reportName), reportName);
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
			if (userList != null && !userList.isEmpty()) {
				pDFGenerator.generateUserList(
						(selectedUserList != null && selectedUserList.size() > 0) ? selectedUserList : userList);
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

	public void setUserObj(AjaxActionEvent event) {
		try {
			if (event != null) {
				Session session = null;
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					session = sessionFactory.getCurrentSession();
					user = (Logindetails) session.get(Logindetails.class, (String) link.getValue());
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

	public Logindetails getUser() {
		return user;
	}

	public void setUser(Logindetails user) {
		this.user = user;
	}

	public void setFileName(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					if (parameter.getValue() != null)
						userName = (String) parameter.getValue();
					else
						userName = null;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public Logindetails getSelectedUserToForceDelete() {
		return selectedUserToForceDelete;
	}

	public void setSelectedUserToForceDelete(Logindetails selectedUserToForceDelete) {
		this.selectedUserToForceDelete = selectedUserToForceDelete;
	}

}