/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import java.io.IOException;
import java.util.List;

import org.apache.catalina.core.ApplicationPart;
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
import tpo.dao.CommonDBBean;
import tpo.hibernate.Notice;
import tpo.imageservice.client.FileUploadUtility;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.IMAGECONS;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("Noticeboard")
@Transactional(readOnly = true)
@Scope("session")
public class Noticeboard extends Parent {

	private Logger logger = LoggerFactory.getLogger(Noticeboard.class);

	private String currentDocMode = null;

	private Notice notice = null;

	private List<String> studentRollnumberList;

	private List<String> selectedStudentList;

	private ApplicationPart file1;

	private ApplicationPart file2;

	private ApplicationPart file3;

	private ApplicationPart file4;

	private ApplicationPart file5;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private FileUploadUtility fileUploadUtility;

	@Autowired
	private CommonDBBean commonDBBean;

	public void changeNotice() {
		try {
			AdminUser adminUser = AdminUser.getUser();
			if (adminUser != null && adminUser.getUserName() != null) {
				String noticeName = notice.getNoticeName();
				Session session = sessionFactory.getCurrentSession();
				Criteria criteria = session.createCriteria(Notice.class);
				criteria.add(Restrictions.eq("noticeName", noticeName));
				criteria.add(Restrictions.eq("createdBy", adminUser.getUserName()));
				notice = (Notice) criteria.uniqueResult();
				if (notice != null) {
					currentDocMode = CCPConstant.UPDATE;
				} else {
					notice = new Notice();
					notice.setNoticeName(noticeName);
					currentDocMode = CCPConstant.CREATE;
				}
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void saveNotice() {
		try {
			Session session = sessionFactory.getCurrentSession();
			if ("".equals(notice.getNotice())) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Content_must_be_enter"));
				return;
			}
			if ("".equals(notice.getNoticeName())) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Notice_Name_must_be_enter"));
				return;
			}
			if (notice.getNotice().length() > 5000) {
				UIBackingBean
						.setErrorMessage(FbMessageUtil.getLabel("Content_lenght_can_not_be_grater_than_5000_char"));
				return;
			}
			if (CCPConstant.CREATE.equals(currentDocMode)) {

				if (selectedStudentList != null && selectedStudentList.size() > 0) {
					Notice noticeTemp;
					for (String rno : selectedStudentList) {
						noticeTemp = new Notice();
						noticeTemp.setNoticeName(notice.getNoticeName());
						noticeTemp.setNotice(notice.getNotice());
						noticeTemp.setExpiryDate(notice.getExpiryDate());
						noticeTemp.setImpTag(notice.getImpTag());
						noticeTemp.setStudentSpecific(true);
						noticeTemp.setActive(true);
						noticeTemp.setCreatedBy(rno);
						session.save(noticeTemp);
					}
				} else {
					AdminUser adminUser = AdminUser.getUser();
					if (adminUser != null && adminUser.getUserName() != null) {
						session = sessionFactory.getCurrentSession();
						Criteria criteria = session.createCriteria(Notice.class);
						criteria.add(Restrictions.eq("noticeName", notice.getNoticeName()));
						criteria.add(Restrictions.eq("createdBy", adminUser.getUserName()));
						Notice noticeTemp = (Notice) criteria.uniqueResult();
						if (noticeTemp != null) {
							notice = noticeTemp;
							currentDocMode = CCPConstant.UPDATE;
							UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Notice_is_already_exist"));
						} else {
							if (((file1 != null && file1.getSize() > 0) || (file2 != null && file2.getSize() > 0)
									|| (file3 != null && file3.getSize() > 0) || (file4 != null && file4.getSize() > 0)
									|| (file5 != null && file5.getSize() > 0)) && TpoUtil.isStringHasSpace(notice.getNoticeName())) {
								UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("noticeNameError"));
								return;
							}

							if (file1.getSize() > TpoUtil.IMAGE_SIZE) {
								UIBackingBean
										.setErrorMessage(FbMessageUtil.getLabel("Error_file_notice", "Attachment 1"));
								return;
							} else if (file2.getSize() > TpoUtil.IMAGE_SIZE) {
								UIBackingBean
										.setErrorMessage(FbMessageUtil.getLabel("Error_file_notice", "Attachment 2"));
								return;
							} else if (file3.getSize() > TpoUtil.IMAGE_SIZE) {
								UIBackingBean
										.setErrorMessage(FbMessageUtil.getLabel("Error_file_notice", "Attachment 3"));
								return;
							} else if (file4.getSize() > TpoUtil.IMAGE_SIZE) {
								UIBackingBean
										.setErrorMessage(FbMessageUtil.getLabel("Error_file_notice", "Attachment 4"));
								return;
							} else if (file5.getSize() > TpoUtil.IMAGE_SIZE) {
								UIBackingBean
										.setErrorMessage(FbMessageUtil.getLabel("Error_file_notice", "Attachment 5"));
								return;
							}
							
							if((file1 != null && file1.getSize() > 0) && !TpoUtil.doImageUploadValidation(file1)) {
								return;
							}
							if((file2 != null && file2.getSize() > 0) && !TpoUtil.doImageUploadValidation(file2)) {
								return;
							}
							if((file3 != null && file3.getSize() > 0) && !TpoUtil.doImageUploadValidation(file3)) {
								return;
							}
							if((file4 != null && file4.getSize() > 0) && !TpoUtil.doImageUploadValidation(file4)) {
								return;
							}
							if((file5 != null && file5.getSize() > 0) && !TpoUtil.doImageUploadValidation(file5)) {
								return;
							}
							
							

							notice.setActive(true);
							notice.setCreatedBy(adminUser.getUserName());
							setAttachment(session, notice);
							session.save(notice);

							UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Your_notice_successfully_added"));
						}
					}
				}
			} else {
				session.update(notice);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Your_notice_successfully_updated"));

			}
			if (commonDBBean != null) {
				commonDBBean.loadNotice();
			}
			currentDocMode = null;
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	private void setAttachment(Session session, Notice noticeTemp) throws IOException {

		if (file1 != null && file1.getSize() > 0) {
				noticeTemp.setFileName1("file1" + TpoUtil.getImageExt(file1.getContentType()));
				fileUploadUtility.uploadFileWithByteArrayWithExt(getImageServiceUrl() + "/uploadWithExt",
						noticeTemp.getNoticeName() + "_" + noticeTemp.getFileName1(),
						TpoUtil.convertInputStreamToBytesArray(file1.getInputStream()), IMAGECONS.notice.toString());
		}
		if (file2 != null && file2.getSize() > 0) {
				noticeTemp.setFileName2("file2" + TpoUtil.getImageExt(file2.getContentType()));
				fileUploadUtility.uploadFileWithByteArrayWithExt(getImageServiceUrl() + "/uploadWithExt",
						noticeTemp.getNoticeName() + "_" + noticeTemp.getFileName2(),
						TpoUtil.convertInputStreamToBytesArray(file2.getInputStream()), IMAGECONS.notice.toString());
		}
		if (file3 != null && file3.getSize() > 0) {
				noticeTemp.setFileName3("file3" + TpoUtil.getImageExt(file3.getContentType()));
				fileUploadUtility.uploadFileWithByteArrayWithExt(getImageServiceUrl() + "/uploadWithExt",
						noticeTemp.getNoticeName() + "_" + noticeTemp.getFileName3(),
						TpoUtil.convertInputStreamToBytesArray(file3.getInputStream()), IMAGECONS.notice.toString());
		}
		if (file4 != null && file4.getSize() > 0) {
				noticeTemp.setFileName4("file4" + TpoUtil.getImageExt(file4.getContentType()));
				fileUploadUtility.uploadFileWithByteArrayWithExt(getImageServiceUrl() + "/uploadWithExt",
						noticeTemp.getNoticeName() + "_" + noticeTemp.getFileName4(),
						TpoUtil.convertInputStreamToBytesArray(file4.getInputStream()), IMAGECONS.notice.toString());
		}
		if (file5 != null && file5.getSize() > 0 ) {
				noticeTemp.setFileName5("file5" + TpoUtil.getImageExt(file5.getContentType()));
				fileUploadUtility.uploadFileWithByteArrayWithExt(getImageServiceUrl() + "/uploadWithExt",
						noticeTemp.getNoticeName() + "_" + noticeTemp.getFileName5(),
						TpoUtil.convertInputStreamToBytesArray(file5.getInputStream()), IMAGECONS.notice.toString());
		}
	}

	public String getCurrentDocMode() {
		return currentDocMode;
	}

	public void setCurrentDocMode(String currentDocMode) {
		this.currentDocMode = currentDocMode;
	}

	public Notice getNotice() {
		return notice;
	}

	public void setNotice(Notice notic) {
		this.notice = notic;
	}

	public List<String> getSelectedStudentList() {
		return selectedStudentList;
	}

	public void setSelectedStudentList(List<String> selectedStudentList) {
		this.selectedStudentList = selectedStudentList;
	}

	public void setStudentRollnumberList(List<String> studentRollnumberList) {
		this.studentRollnumberList = studentRollnumberList;
	}

	public List<String> getStudentRollnumberList() {
		if (studentRollnumberList == null && notice != null) {
			Session session = sessionFactory.getCurrentSession();
			NativeQuery<String> query = session
					.createSQLQuery("select rollnumber from registration where collegename in("
							+ TpoUtil.getComaSeprateValue(AdminUser.getUser().getCollegeList()) + ")");
			studentRollnumberList = query.getResultList();
		}
		return studentRollnumberList;
	}

	public ApplicationPart getFile1() {
		return file1;
	}

	public void setFile1(ApplicationPart file1) {
		this.file1 = file1;
	}

	public ApplicationPart getFile2() {
		return file2;
	}

	public void setFile2(ApplicationPart file2) {
		this.file2 = file2;
	}

	public ApplicationPart getFile3() {
		return file3;
	}

	public void setFile3(ApplicationPart file3) {
		this.file3 = file3;
	}

	public ApplicationPart getFile4() {
		return file4;
	}

	public void setFile4(ApplicationPart file4) {
		this.file4 = file4;
	}

	public ApplicationPart getFile5() {
		return file5;
	}

	public void setFile5(ApplicationPart file5) {
		this.file5 = file5;
	}

}
