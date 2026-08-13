/* 

 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.servlet.http.Part;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openfaces.event.AjaxActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.email.EmailUtil;
import tpo.hibernate.Logindetails;
import tpo.hibernate.Userdetails;
import tpo.imageservice.client.FileUploadUtility;
import tpo.util.CCPConstant;
import tpo.util.Encryption;
import tpo.util.FbMessageUtil;
import tpo.util.IMAGECONS;
import tpo.util.ResourceID;
import tpo.util.SystemUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("CreateUser")
@Transactional(readOnly = true)
@Scope("session")
public class CreateUser extends Parent{

	private String currentDocMode = CCPConstant.CREATE;

	private Logger logger = LoggerFactory.getLogger(CreateUser.class);

	private String confirmPassword;

	private String newPassword;

	private Logindetails logindetails = new Logindetails();

	private Userdetails userdetails = new Userdetails();

	private Part file;

	private Part profileFile;

	private boolean profileSelected;

	private boolean profileForm = true;

	private boolean passwordForm = true;

	private String newEmail;
	
	private Long newMobileNumber;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private FileUploadUtility fileUploadUtility;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String uploadImage() throws HibernateException, IOException, SQLException {
		if (logindetails != null) {
			try {
				if (file != null) {
					if (file.getSize() <= 0) {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Error21"));
					} else {
						if (file.getSize() > 0 && file.getSize() < 102400) {
							if (TpoUtil.imageTypes.contains(file.getContentType())) {
								Userdetails userdetails = logindetails.getUserdetails();
								fileUploadUtility.uploadFileWithByteArray(getImageServiceUrl()+"/upload", userdetails.getUserName(), TpoUtil.convertInputStreamToBytesArray(file.getInputStream()),IMAGECONS.userlogo);
								AdminUser adminUser = (AdminUser)TpoUtil.getManagedBean(AdminUser.class.getSimpleName());
								if(adminUser != null) {
									adminUser.setLogo(null);
								}
								UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Logo_is_uploaded_successfuly"));
							} else {
								UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error21));
							}
						} else {
							UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Max_image_size_is_100kb"));
						}
					}
				}
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			} 
		}
		return "";
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String uploadProfileImage() throws HibernateException, IOException, SQLException {
		if (logindetails != null) {
			try {
				if (profileFile != null) {
					if (profileFile.getSize() <= 0) {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Error21"));
					} else {
						if (profileFile.getSize() > 0 && profileFile.getSize() < 102400) {
							if (TpoUtil.imageTypes.contains(profileFile.getContentType())) {
								Userdetails userdetails = logindetails.getUserdetails();
								fileUploadUtility.uploadFileWithByteArray(getImageServiceUrl()+"/upload", userdetails.getUserName(), TpoUtil.convertInputStreamToBytesArray(profileFile.getInputStream()),IMAGECONS.userprofilepics);
								
								AdminUser adminUser = (AdminUser)TpoUtil.getManagedBean(AdminUser.class.getSimpleName());
								if(adminUser != null) {
									adminUser.setProfilePic(null);
								}
								UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Success19"));
							} else {
								UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error21));
							}
						} else {
							UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Max_image_size_is_100kb"));
						}
					}
				}
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return "";
	}

	public boolean isUserExistForCreate(Session session) {
		Criteria criteria = session.createCriteria(Logindetails.class).setProjection(Projections.property("userName"));
		criteria.add(Restrictions.eq("userName", logindetails.getUserName()));
		String userName = (String) criteria.uniqueResult();
		if (userName != null) {
			userName = null;
			return true;
		} else {
			return false;
		}
	}
	
	public Logindetails isUserExist(Session session) {
		Logindetails userinfo = null;
		try {
			Criteria criteria = session.createCriteria(Logindetails.class);
			criteria.add(Restrictions.eq("userName", logindetails.getUserName()));
			criteria.add(Restrictions.eq("password", Encryption.getEncryptedString(logindetails.getPassword())));
			AdminUser user = AdminUser.getUser();
			if (user != null && user.getUserName() != null && !user.getUserName().equals(logindetails.getUserName())) {
				criteria.add(Restrictions.eq("createdBy", user.getUser().getUserName()));
			}
			userinfo = (Logindetails) criteria.uniqueResult();

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return userinfo;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveAction() {
		try {
			
			if(logindetails.getRole() == null) {
				logindetails.setRole("A");
				
			}
			if ("S".equals(logindetails.getRole())) {
				AdminUser user = AdminUser.getUser();
				if (user != null) {
					if (!CCPConstant.SUPERUSER.equals(user.getRole())) {
						UIBackingBean.setErrorMessage(
								FbMessageUtil.getLabel("Only_Super_User_can_create_another_Super_User"));
						return;
					}
				}

			}
			Session session = sessionFactory.getCurrentSession();
			userdetails.setNumberVerified(false);
			userdetails.setEmailVarified(false);
			if (CCPConstant.CREATE.equals(currentDocMode)) {
				if (logindetails.getPassword().equals(confirmPassword)) {

					if (isUserExistForCreate(session)) {
						UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Info1));
					} else {
						String encryptedPassword = Encryption.getEncryptedString(logindetails.getPassword());
						logindetails.setPassword(encryptedPassword);
						userdetails.setUserName(logindetails.getUserName());
						userdetails.setLogindetails(logindetails);
						logindetails.setActive(false);
						logindetails.setLastLogin(new Date());
						logindetails.setLoginAttempt(0);
						logindetails.setUi(true);
						if(AdminUser.getUser().getUserName() != null) {
						logindetails.setCreatedBy(AdminUser.getUser().getUserName());
						}else {
							String supperUser = SystemUtil.getLabel("supperUser");
							logindetails.setCreatedBy(supperUser);
						}
						session.save(logindetails);
						session.save(userdetails);
						UIBackingBean.setSuccessMessage(
								FbMessageUtil.getLabel("is_successfully_created", logindetails.getUserName()));
						currentDocMode = CCPConstant.UPDATE;
					}
				} else {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error5));
				}

			} else {
				logindetails.setLoginAttempt(0);
				session.update(logindetails);
				session.update(userdetails);
				UIBackingBean.setSuccessMessage(
						FbMessageUtil.getLabel("is_successfully_updated", logindetails.getUserName()));
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
	public void changePassword() {
		try {
			AdminUser adminUser = AdminUser.getUser();
			if (adminUser != null) {
				Session session = sessionFactory.getCurrentSession();
				Logindetails userinfo = (Logindetails) session.get(Logindetails.class, adminUser.getUserName());
				if (userinfo == null) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error8));
				} else {
					userinfo.setPassword(Encryption.getEncryptedString(newPassword));
					session.update(userinfo);
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(
							"is_successfully_updated_and_Password_changed_successfully", userinfo.getUserName()));
					userinfo = null;
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

	public String getCurrentDocMode() {
		return currentDocMode;
	}

	public void setCurrentDocMode(String currentDocMode) {
		this.currentDocMode = currentDocMode;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public Logindetails getLogindetails() {
		return logindetails;
	}

	public void setLogindetails(Logindetails logindetails) {
		this.logindetails = logindetails;
	}

	public Userdetails getUserdetails() {
		return userdetails;
	}

	public void setUserdetails(Userdetails userdetails) {
		this.userdetails = userdetails;
	}

	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}

	public boolean isProfileSelected() {
		return profileSelected;
	}

	public void setProfileSelected(boolean profileSelected) {
		this.profileSelected = profileSelected;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setUser(AjaxActionEvent event) {
		passwordForm = false;
		profileForm = true;
		try {
			AdminUser adminUser = AdminUser.getUser();
			Session session = sessionFactory.getCurrentSession();
			logindetails = (Logindetails) session.get(Logindetails.class, adminUser.getUserName());
			if (logindetails != null)
				userdetails = logindetails.getUserdetails();
			CreateUser bean = (CreateUser) TpoUtil.getManagedBean(CreateUser.class.getSimpleName());
			if (bean != null) {
				bean.setCurrentDocMode(CCPConstant.UPDATE);
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setPassword(AjaxActionEvent event) {
		profileForm = false;
		passwordForm = true;
	}

	public boolean isProfileForm() {
		return profileForm;
	}

	public void setProfileForm(boolean profileForm) {
		this.profileForm = profileForm;
	}

	public boolean isPasswordForm() {
		return passwordForm;
	}

	public void setPasswordForm(boolean passwordForm) {
		this.passwordForm = passwordForm;
	}

	public Part getProfileFile() {
		return profileFile;
	}

	public void setProfileFile(Part profileFile) {
		this.profileFile = profileFile;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void verifyNumberFromAdmin() {
		try {
			Session session = sessionFactory.getCurrentSession();
			userdetails.setNumberVerified(true);
			session.update(userdetails);
			UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Your_number_is_verified_successfully"));
			// sendEmail();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void sendEmail() {
		try {
			EmailUtil emailUtill = getEmailInstance();
			if (emailUtill != null) {
				List<String> recipients = new ArrayList<String>(1);
				recipients.add(userdetails.getEmail());
				String subject = FbMessageUtil.getLabel("E_mail_Verification");
				StringBuffer message = new StringBuffer("<font color=green size=5>" + FbMessageUtil.getLabel("Dear")
						+ userdetails.getFirstName() + " " + userdetails.getLastName() + ",<br>");
				message.append("<br><a href='" + TpoUtil.getBasePath(null) + "servlet/AjaxServlet?dfdnmfbnndfnfdgdfgfgfdgjlkgh="
						+ logindetails.getUserName()
						+ "&fdfdfemailfdf=65980hgj5j56j4h6j456j78j3k77956b5n6b4j6b64746hgbhdppqhqqsssrrnvnhvgkgnghkggggngh@0146556"
						+ logindetails.getPassword()
						+ "46556@0145j5hk3k8j9k63nvnknrf35846guyofndgs46fh4h78394nhvjvh6vnvbgh7jgjggh0000h89g7f9b111bjhjghgydjfhgnd'>Please click here to verify your email.</a><br>");
				message.append(TpoUtil.getMesageString());
				emailUtill.postMail(recipients, subject, message.toString(), TpoUtil.ADMIN_EMAIL,
						Message.RecipientType.TO);
				UIBackingBean.setSuccessMessage(
						FbMessageUtil.getLabel("Email_verification_email_sent", userdetails.getEmail()));

			}
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEmail() {
		try {
			if (userdetails.getEmail() != null && !userdetails.getEmail().equals(newEmail)) {
				Session session = sessionFactory.getCurrentSession();
				userdetails.setEmail(newEmail);
				userdetails.setEmailVarified(false);
				session.update(userdetails);
				UIBackingBean.setSuccessMessage(
						FbMessageUtil.getLabel("Email_is_updated_successfully_Please_verify_new_email_address"));
				sendEmail();
			} else {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Old_and_New_email_is_same"));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public String getNewEmail() {
		return newEmail;
	}

	public void setNewEmail(String newEmail) {
		this.newEmail = newEmail;
	}
	
	public void clearNewEmail() {
		this.newEmail = null;
	}

	public Long getNewMobileNumber() {
		return newMobileNumber;
	}

	public void setNewMobileNumber(Long newMobileNumber) {
		this.newMobileNumber = newMobileNumber;
	}
	
	public void clearNewNumber() {
		this.newMobileNumber = null;;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateNumber() {
		try {
			if (userdetails.getMobleNo() != null && !userdetails.getMobleNo().equals(newMobileNumber)) {
				Session session = sessionFactory.getCurrentSession();
				userdetails.setMobleNo(newMobileNumber);
				userdetails.setNumberVerified(false);
				session.update(userdetails);
				UIBackingBean.setSuccessMessage(
						FbMessageUtil.getLabel("Number_is_updated_successfully_Please_verify_new_number"));
				// sendEmail();
			} else {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Old_and_New_number_is_same"));
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
