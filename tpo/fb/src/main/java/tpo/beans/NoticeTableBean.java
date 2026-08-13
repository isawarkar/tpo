/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

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
import tpo.hibernate.Notice;
import tpo.imageservice.client.FileUploadUtility;
import tpo.pdf.generator.PDFGenerator;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.IMAGECONS;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("NoticeTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class NoticeTableBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(NoticeTableBean.class);

	private String noticeName;

	private String fileName;

	private Notice notice;

	private List<Notice> noticeList = null;

	private List<Notice> selectedNoticeList = new ArrayList<Notice>();

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private PDFGenerator pDFGenerator;

	@Autowired
	private FileUploadUtility fileUploadUtility;

	@Autowired
	private Pagination pagination;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSeletedRecord() {
		try {
			if (selectedNoticeList != null && selectedNoticeList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (Notice notice : selectedNoticeList) {
					session.delete(notice);
					if (notice.getFileName1() != null && !"".equals(notice.getFileName1())) {
						fileUploadUtility.deleteFileWithParam(getFileServiceUrl() + "/delete", notice.getNoticeName() + "_" + notice.getFileName1(),IMAGECONS.notice.toString());
					}
					if(notice.getFileName2() != null && !"".equals(notice.getFileName2())) {
						fileUploadUtility.deleteFileWithParam(getFileServiceUrl() + "/delete", notice.getNoticeName() + "_" + notice.getFileName2(),IMAGECONS.notice.toString());
						}
					if(notice.getFileName3() != null && !"".equals(notice.getFileName3())) {
						fileUploadUtility.deleteFileWithParam(getFileServiceUrl() + "/delete", notice.getNoticeName() + "_" + notice.getFileName3(),IMAGECONS.notice.toString());
						}
					if(notice.getFileName4() != null && !"".equals(notice.getFileName4())) {
						fileUploadUtility.deleteFileWithParam(getFileServiceUrl() + "/delete", notice.getNoticeName() + "_" + notice.getFileName4(),IMAGECONS.notice.toString());
						}
					if(notice.getFileName5() != null && !"".equals(notice.getFileName5())) {
						fileUploadUtility.deleteFileWithParam(getFileServiceUrl() + "/delete", notice.getNoticeName() + "_" + notice.getFileName5(),IMAGECONS.notice.toString());
						}
				}
				noticeList = null;
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel(ResourceID.Success13));
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
	public void changeStatus() {
		try {
			if (selectedNoticeList != null && selectedNoticeList.size() == 0) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_select_at_least_one_record_to_change_the_Status"));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (Notice notice : selectedNoticeList) {
					if (notice.getActive()) {
						notice.setActive(false);
					} else {
						notice.setActive(true);
					}
					session.update(notice);
				}
				noticeList = null;
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Status_has_been_changed_for_selected_records"));
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

	public void setNotice(AjaxActionEvent event) {
		try {
			Noticeboard bean = (Noticeboard) TpoUtil.getManagedBean(Noticeboard.class.getSimpleName());
			if (selectedNoticeList != null && selectedNoticeList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Info12"));
				if (bean != null) {
					bean.setNotice(null);
				}
			} else {
				if (selectedNoticeList.size() > 1) {
					UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
					if (bean != null) {
						bean.setNotice(null);
					}
				} else {
					if (bean != null) {
						bean.setNotice(selectedNoticeList.get(0));
						bean.setCurrentDocMode(CCPConstant.UPDATE);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setNoticeObj(AjaxActionEvent event) {
		try {
			Notice n = null;
			StudentRegistrationBean bean = (StudentRegistrationBean) TpoUtil
					.getManagedBean(StudentRegistrationBean.class.getSimpleName());
			if (selectedNoticeList != null && selectedNoticeList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Info12"));
				if (bean != null) {
					bean.setNotice(n);
				}
			} else {
				if (selectedNoticeList.size() > 1) {
					UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
					if (bean != null) {
						bean.setNotice(n);
					}
				} else {
					if (bean != null) {
						bean.setNotice(selectedNoticeList.get(0));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setNoticeToAdd(AjaxActionEvent event) {
		Noticeboard bean = (Noticeboard) TpoUtil.getManagedBean(Noticeboard.class.getSimpleName());
		if (bean != null) {
			bean.setCurrentDocMode(CCPConstant.CREATE);
			bean.setNotice(new Notice());
			bean.setSelectedStudentList(null);

		}
	}

	public void inIt() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Notice.class);
			String queryStr = "select count(noticeName) from notice where 1=1";
			AdminUser user = AdminUser.getUser();
			if (user != null && user.getUserName() != null && !"".equals(user.getUserName())) {
				List<String> userNames = AdminUser.getUser().getUserList();
				NativeQuery<String> query = session
						.createSQLQuery("select rollnumber from registration where collegename in("
								+ TpoUtil.getComaSeprateValue(AdminUser.getUser().getCollegeList()) + ")");
				List<String> rNoList = query.getResultList();
				if (rNoList != null && rNoList.size() > 0) {
					userNames.addAll(rNoList);
				}
				criteria.add(Restrictions.in("createdBy", userNames));
				criteria.addOrder(Order.asc("createdBy"));
				queryStr = queryStr + " and createdBy in(" + TpoUtil.getComaSeprateValue(userNames) + ")";
			}
			if (noticeName != null && !noticeName.equals("")) {
				criteria.add(Restrictions.ilike("noticeName", "%" + noticeName + "%"));
				queryStr = queryStr + " and  noticeName like '%" + noticeName + "%'";
			}

			NativeQuery<BigInteger> query = session.createSQLQuery(queryStr);
			criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			criteria.addOrder(Order.asc("expiryDate"));
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			noticeList = criteria.list();

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void inItMain() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Notice.class);
			String queryStr = "select count(noticeName) from notice where 1=1";

			if (noticeName != null && !noticeName.equals("")) {
				criteria.add(Restrictions.ilike("noticeName", "%" + noticeName + "%"));
				queryStr = queryStr + " and  noticeName like '%" + noticeName + "%'";
			}

			NativeQuery<BigInteger> query = session.createSQLQuery(queryStr);
			criteria.add(Restrictions.eq("active", true));
			criteria.add(Restrictions.isNull("studentSpecific"));
			criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			criteria.addOrder(Order.asc("expiryDate"));
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			noticeList = criteria.list();

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public String getNoticeName() {
		return noticeName;
	}

	public void setNoticeName(String noticeName) {
		this.noticeName = noticeName;
	}

	public List<Notice> getNoticeList() {
		return noticeList;
	}

	public void setNoticeList(List<Notice> noticeList) {
		this.noticeList = noticeList;
	}

	public List<Notice> getSelectedNoticeList() {
		return selectedNoticeList;
	}

	public void setSelectedNoticeList(List<Notice> selectedNoticeList) {
		this.selectedNoticeList = selectedNoticeList;
	}

	public void generateXls() {
		try {
			if (noticeList != null && !noticeList.isEmpty()) {

				String reportName = "FB_" + "NoticeList_" + AdminUser.getUser().getUserName() + "_"
						+ TpoUtil.getDateToStringYYYYMMdd(new Date());
				TpoUtil.renderEXcelFile(ExcelHandler.generateNoticListXls(
						(selectedNoticeList != null && selectedNoticeList.size() > 0) ? selectedNoticeList : noticeList,
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
			if (noticeList != null && !noticeList.isEmpty()) {
				pDFGenerator.generateNoticeList(
						(selectedNoticeList != null && selectedNoticeList.size() > 0) ? selectedNoticeList
								: noticeList);
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

	public void setFileName(AjaxActionEvent event) {
		try {
			if (event != null) {
				if (selectedNoticeList != null && selectedNoticeList.size() == 0) {
					UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Info12"));
					notice = null;
					fileName = null;
					return;
				} else {
					if (selectedNoticeList.size() > 1) {
						UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
						notice = null;
						fileName = null;
					} else {
						notice = selectedNoticeList.get(0);
						CommandLink link = (CommandLink) event.getSource();
						if (link != null) {
							List<UIComponent> list = link.getChildren();
							UIParameter parameter = (UIParameter) list.get(0);
							fileName = (String) parameter.getValue();
						}
					}

				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getImageBytesAsImage() {
		byte[] buf = null;
		if (fileName != null && notice != null) {
			if (fileName.equals(notice.getFileName1()))
				buf = fileUploadUtility.downloadFile(getImageServiceUrl() + "/download",
						notice.getNoticeName() + "_" + notice.getFileName1(),IMAGECONS.notice);
			else if (fileName.equals(notice.getFileName2()))
				buf = fileUploadUtility.downloadFile(getImageServiceUrl() + "/download",
						notice.getNoticeName() + "_" + notice.getFileName2(),IMAGECONS.notice);
			else if (fileName.equals(notice.getFileName3()))
				buf = fileUploadUtility.downloadFile(getImageServiceUrl() + "/download",
						notice.getNoticeName() + "_" + notice.getFileName3(),IMAGECONS.notice);
			else if (fileName.equals(notice.getFileName4()))
				buf = fileUploadUtility.downloadFile(getImageServiceUrl() + "/download",
						notice.getNoticeName() + "_" + notice.getFileName4(),IMAGECONS.notice);
			else if (fileName.equals(notice.getFileName5()))
				buf = fileUploadUtility.downloadFile(getImageServiceUrl() + "/download",
						notice.getNoticeName() + "_" + notice.getFileName5(),IMAGECONS.notice);
			return buf;
		} else {
			UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Error9"));
			return null;
		}
	}

	public Notice getNotice() {
		return notice;
	}

	public void setNotice(Notice notice) {
		this.notice = notice;
	}

	public void downloadDocument() {
		OutputStream servletOutputStream = null;
		HttpServletResponse response = null;
		FacesContext facesContext = null;
		try {
			if (fileName != null) {
				facesContext = FacesContext.getCurrentInstance();
				response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
				servletOutputStream = response.getOutputStream();
				response.setContentType("application/jpeg");
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".jpeg");
				servletOutputStream.write(getImageBytesAsImage());
			}
		} catch (IOException e) {
		} finally {
			try {
				if (servletOutputStream != null) {
					servletOutputStream.close();
					facesContext.responseComplete();
				}
			} catch (IOException e) {
			}
		}
	}
}