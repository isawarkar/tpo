/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.admin.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Calendar;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.openfaces.component.command.CommandLink;
import org.openfaces.event.AjaxActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.backup.CollegeConnectBackUp;
import tpo.beans.Parent;
import tpo.beans.StudentRegistrationBean;
import tpo.beans.UIBackingBean;
import tpo.dao.CommonDBBean;
import tpo.email.EmailUtil;
import tpo.hibernate.Notice;
import tpo.hibernate.annotation.BroadCastMessage;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.SystemUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("DashBoardBean")
@Transactional(readOnly = true)
@Scope("request")
public class DashBoardBean extends Parent {

	static Logger logger = LoggerFactory.getLogger(DashBoardBean.class);

	private BigInteger totalStudent;

	private BigInteger totalSelected;

	private BigInteger totalClients;

	private BigInteger totalExam;

	private BigInteger totalCompany;

	private BigInteger totalCollege;

	private BigInteger totalNotices;

	private BigInteger totalOpenings;

	private BigInteger totalComplaints;

	private String classCode;

	private BroadCastMessage broadCastMessage;

	@Autowired
	private CommonDBBean commonDBBean;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private CollegeConnectBackUp collegeConnectBackUp;

	String userName = AdminUser.getUser().getUserName();

	/**
	 * @return the totalStudent
	 */
	public BigInteger getTotalStudent() {
		try {

			Session session = sessionFactory.getCurrentSession();
			String collegeList = TpoUtil.getComaSeprateValue(AdminUser.getUser().getCollegeList());
			String queryStr = null;
			NativeQuery<?> query = null;
			if (totalStudent == null) {
				queryStr = "select count(rollnumber) from registration where collegeName in(" + collegeList + ")";
				query = session.createNativeQuery(queryStr);
				totalStudent = (BigInteger) query.getSingleResult();
			}
			if (totalSelected == null) {
				queryStr = "select count(r.rollnumber) from personalinfo pi,registration r where pi.rollnumber = r.rollnumber and pi.companyName !='' and collegeName in ("
						+ collegeList + ")";
				query = session.createNativeQuery(queryStr);
				totalSelected = (BigInteger) query.getSingleResult();
			}

			if (totalClients == null) {
				queryStr = "select count(userName) from logindetails where createdBy = '" + userName + "'";
				query = session.createNativeQuery(queryStr);
				totalClients = (BigInteger) query.getSingleResult();
			}

			String userList = TpoUtil.getComaSeprateValue(AdminUser.getUser().getUserList());
			if (totalExam == null) {
				queryStr = "select count(testname) from exam where createdBy in (" + userList + ")";
				query = session.createNativeQuery(queryStr);
				totalExam = (BigInteger) query.getSingleResult();
			}
			if (totalCompany == null) {
				queryStr = "select count(companyname) from company where createdBy in (" + userList + ")";
				query = session.createNativeQuery(queryStr);
				totalCompany = (BigInteger) query.getSingleResult();
			}

			if (totalCollege == null) {
				queryStr = "select count(CollegeName) from college where userName in (" + userList + ")";
				query = session.createNativeQuery(queryStr);
				totalCollege = (BigInteger) query.getSingleResult();
			}

			if (totalNotices == null) {
				queryStr = "select count(noticeName) from notice where createdBy in (" + userList + ")";
				query = session.createNativeQuery(queryStr);
				totalNotices = (BigInteger) query.getSingleResult();
			}

			if (totalOpenings == null) {
				queryStr = "select count(companyName) from hallticket where userName in (" + userList + ")";
				query = session.createNativeQuery(queryStr);
				totalOpenings = (BigInteger) query.getSingleResult();
			}

			if (totalComplaints == null) {
				queryStr = "select count(complaintNumber) from complaints";
				query = session.createNativeQuery(queryStr);
				totalComplaints = (BigInteger) query.getSingleResult();
			}

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return totalStudent;
	}

	/**
	 * @param totalStudent the totalStudent to set
	 */
	public void setTotalStudent(BigInteger totalStudent) {
		this.totalStudent = totalStudent;
	}

	/**
	 * @return the totalClients
	 */
	public BigInteger getTotalClients() {
		return totalClients;
	}

	/**
	 * @param totalClients the totalClients to set
	 */
	public void setTotalClients(BigInteger totalClients) {
		this.totalClients = totalClients;
	}

	/**
	 * @return the totalExam
	 */
	public BigInteger getTotalExam() {
		return totalExam;
	}

	/**
	 * @param totalExam the totalExam to set
	 */
	public void setTotalExam(BigInteger totalExam) {
		this.totalExam = totalExam;
	}

	/**
	 * @return the totalCompany
	 */
	public BigInteger getTotalCompany() {
		return totalCompany;
	}

	/**
	 * @param totalCompany the totalCompany to set
	 */
	public void setTotalCompany(BigInteger totalCompany) {
		this.totalCompany = totalCompany;
	}

	/**
	 * @return the totalCollege
	 */
	public BigInteger getTotalCollege() {
		return totalCollege;
	}

	/**
	 * @param totalCollege the totalCollege to set
	 */
	public void setTotalCollege(BigInteger totalCollege) {
		this.totalCollege = totalCollege;
	}

	/**
	 * @return the totalSelected
	 */
	public BigInteger getTotalSelected() {
		return totalSelected;
	}

	/**
	 * @param totalSelected the totalSelected to set
	 */
	public void setTotalSelected(BigInteger totalSelected) {
		this.totalSelected = totalSelected;
	}

	public void disbledAction() {
		UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("diabledAction"));
	}

	public void doBackup() {
		if (collegeConnectBackUp.doBackup("b")) {
			UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success17));
		} else {
			UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error));
		}
	}

	public void restoreBackup() {

		if (!TpoUtil.isUnix()) {

			if (collegeConnectBackUp.doBackup("r")) {
				UIBackingBean.setSuccessMessage(
						FbMessageUtil.getLabel(ResourceID.Success18, collegeConnectBackUp.getDateAndTime()));
			} else {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error));
			}
		}

	}

	
	
	public void setNotice(AjaxActionEvent event) {
		try {
			if (event != null) {
				Session session = sessionFactory.getCurrentSession();
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					String noticeName = (String) link.getValue();
					String supperUser = SystemUtil.getLabel("supperUser");
					Criteria criteria = session.createCriteria(Notice.class);
					criteria.add(Restrictions.eq("noticeName", noticeName));
					criteria.add(Restrictions.in("createdBy", supperUser));
					StudentRegistrationBean bean = (StudentRegistrationBean)TpoUtil.getManagedBean(StudentRegistrationBean.class.getSimpleName());
							if(bean != null) {
								bean.setNotice((Notice) criteria.uniqueResult());
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

	

	public BigInteger getTotalNotices() {
		return totalNotices;
	}

	public void setTotalNotices(BigInteger totalNotices) {
		this.totalNotices = totalNotices;
	}

	public BigInteger getTotalOpenings() {
		return totalOpenings;
	}

	public void setTotalOpenings(BigInteger totalOpenings) {
		this.totalOpenings = totalOpenings;
	}

	public void downloadBackup() {
		EmailUtil emailUtil = getEmailInstance();
		String path = TpoUtil.backupPath;
		String bacupkFileName = null;
		if (emailUtil != null && !TpoUtil.isUnix()) {
			String dName = commonDBBean.getCommonData("DriveName").get(0);
			path = dName + "\\" + path;
		} else {
			path = CollegeConnectBackUp.unixBackupPath;
			bacupkFileName = TpoUtil.getDateToStringYYYYMMdd(Calendar.getInstance().getTime()).replaceAll(" ","_");
			
		}

		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				if(bacupkFileName != null) {
					if(listOfFiles[i].getName().startsWith(bacupkFileName)) {
						renderFile(listOfFiles[i]);
					}
				}else {
				renderFile(listOfFiles[i]);
				}
			}
		}
	}

	private void renderFile(File file) {
		OutputStream servletOutputStream = null;
		HttpServletResponse response = null;
		InputStream in = null;
		byte[] bytes = null;
		FacesContext facesContext = null;
		try {
			if (file.length() != 0) {
				facesContext = FacesContext.getCurrentInstance();
				response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
				servletOutputStream = response.getOutputStream();

				response.setContentType("application/sql");
				response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
				response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());

				in = new FileInputStream(file);

				long length = file.length();
				bytes = new byte[(int) length];
				int offset = 0;
				int numRead = 0;
				while (offset < bytes.length && (numRead = in.read(bytes, offset, bytes.length - offset)) >= 0) {
					offset += numRead;
				}
				servletOutputStream.write(bytes);
			}
		} catch (IOException e) {
		} finally {
			try {
				if (servletOutputStream != null) {
					servletOutputStream.close();
					facesContext.responseComplete();
					bytes = null;
				}
			} catch (IOException e) {
			}
		}
	}

	public BroadCastMessage getBroadCastMessage() {
		return broadCastMessage;
	}

	public void setBroadCastMessage(BroadCastMessage broadCastMessage) {
		this.broadCastMessage = broadCastMessage;
	}

	public String getColor() {
		String color = "";
		if (broadCastMessage == null) {
			AdminUser adminUser = AdminUser.getUser();
			if (adminUser != null && !CCPConstant.SUPERUSER.equals(adminUser.getRole())) {
				broadCastMessage = commonDBBean.getCurrentBroadCastMessage(adminUser.getParent(), true);
			}
		}
		if (broadCastMessage != null) {
			if (CCPConstant.URGENT.equals(broadCastMessage.getMessageSeverity())) {
				color = "red";
				classCode = "danger";
			} else if (CCPConstant.HIGH.equals(broadCastMessage.getMessageSeverity())) {
				color = "orange";
				classCode = "warning";
			} else if (CCPConstant.MEDIUM.equals(broadCastMessage.getMessageSeverity())) {
				color = "yellow";
				classCode = "info";
			} else if (CCPConstant.LOW.equals(broadCastMessage.getMessageSeverity())) {
				color = "white";
				classCode = "success";
			}
		}
		return color;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public BigInteger getTotalComplaints() {
		return totalComplaints;
	}

	public void setTotalComplaints(BigInteger totalComplaints) {
		this.totalComplaints = totalComplaints;
	}


	
	

}
