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

import tpo.admin.beans.AdminUser;
import tpo.hibernate.Shortlist;
import tpo.imageservice.client.FileUploadUtility;
import tpo.util.FbMessageUtil;
import tpo.util.IMAGECONS;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("ShortRecordTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class ShortRecordTableBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(ShortRecordTableBean.class);

	private String fileName;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private FileUploadUtility fileUploadUtility;

	@Autowired
	private Pagination pagination;

	private List<Shortlist> shortlistList = null;

	private List<Shortlist> selectedShortRecordList = new ArrayList<Shortlist>();

	public List<Shortlist> getShortRecordList() {
		return shortlistList;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<Shortlist> getSelectedShortRecordList() {
		return selectedShortRecordList;
	}

	public void setSelectedShortRecordList(List<Shortlist> selectedShortRecordList) {
		this.selectedShortRecordList = selectedShortRecordList;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSeletedRecord() {
		try {
			if (selectedShortRecordList != null && selectedShortRecordList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (Shortlist shortlist : selectedShortRecordList) {
					fileUploadUtility.deleteFileWithParam(getFileServiceUrl() + "/delete", shortlist.getFileName(),
							IMAGECONS.shortlistedxls.toString());
					session.delete(shortlist);
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

	public void renderShortReport() {
		try {
			if (selectedShortRecordList != null && selectedShortRecordList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				if (selectedShortRecordList.size() > 1) {
					UIBackingBean.setInfoMessage(
							FbMessageUtil.getLabel("Please_select_only_one_record_to_see_full_information"));
					return;
				} 
				Shortlist shortlist = selectedShortRecordList.get(0);

				byte[] a = fileUploadUtility.downloadFile(getFileServiceUrl() + "/download", shortlist.getFileName(),
						IMAGECONS.shortlistedxls);
				if (a != null) {
					TpoUtil.renderEXcelFile(a, shortlist.getFileName());
				} else {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("File_not_found"));
				}

			}
		} catch (Exception e) {
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
			Criteria criteria = session.createCriteria(Shortlist.class);
			String userName = AdminUser.getUser().getUserName();
			criteria.add(Restrictions.eq("createdBy", userName));
			String sqlStr = "select count(fileName) from shortlist where createdBy='" + userName + "'";

			if (fileName != null && !fileName.equals("")) {
				criteria.add(Restrictions.ilike("fileName", "%" + fileName + "%"));
				sqlStr = sqlStr + " and fileName like '%" + fileName + "%'";
			}
			criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			NativeQuery<BigInteger> query = session.createSQLQuery(sqlStr);
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			shortlistList = criteria.list();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
