package com.beans;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.annotation.DocumentList;
import com.util.FbMessageUtil;
import com.util.ResourceID;
import com.util.TpoUtil;

@Component("AllDocumentsBean")
@Scope("session")
public class AllDocumentsBean implements Serializable {

	private Logger logger = LoggerFactory.getLogger(AllDocumentsBean.class);

	private List<DocumentList> allDocumentList = new ArrayList<DocumentList>();
	private List<DocumentList> selectedDocumentList = new ArrayList<DocumentList>();

	private String documentName;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private Pagination pagination;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean isDocAvailable(String docName) {
		boolean flag = false;
		StudentRegistrationBean bean = (StudentRegistrationBean) TpoUtil
				.getManagedBean(StudentRegistrationBean.class.getSimpleName());
		Session session = sessionFactory.getCurrentSession();
		if (bean != null && session != null) {
			Criteria criteria = session.createCriteria(DocumentList.class);
			criteria.add(Restrictions.eq("documentID.rollnumber", bean.getRegistration().getRollnumber()));
			criteria.add(Restrictions.eq("documentID.documentName", docName));
			DocumentList documentList = (DocumentList) criteria.uniqueResult();
			if (documentList != null) {
				flag = true;
			}
		}
		return flag;
	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public byte[] getImageBytesAsImage(DocumentList documentList) {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		InputStream stream = null;
		try {
			if (documentList != null) {
				stream = documentList.getDocument().getBinaryStream();
				byte[] buf = new byte[100000];

				int bytesRead;
				do {
					bytesRead = stream.read(buf);
					if (bytesRead != -1)
						arrayOutputStream.write(buf, 0, bytesRead);
				} while (bytesRead != -1);
				arrayOutputStream.close();
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return arrayOutputStream.toByteArray();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void downloadResume() {
		if (selectedDocumentList != null && selectedDocumentList.size() == 0) {
			UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
		} else {
			OutputStream servletOutputStream = null;
			HttpServletResponse response = null;
			FacesContext facesContext = null;
			try {
				DocumentList  docObject = selectedDocumentList.get(0);
				if (docObject != null) {
					facesContext = FacesContext.getCurrentInstance();
					response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
					servletOutputStream = response.getOutputStream();
					response.setContentType("application/jpeg");
					response.setHeader("Content-Disposition", "attachment; filename=" + docObject.getDocumentID().getDocumentName() + ".jpeg");
					servletOutputStream.write(getImageBytesAsImage(docObject));
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

	public void searchRecord() {
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void inIt() {
		try {
			StudentRegistrationBean bean = (StudentRegistrationBean) TpoUtil
					.getManagedBean(StudentRegistrationBean.class.getSimpleName());
			Session session = sessionFactory.getCurrentSession();
			if (bean != null && session != null) {
				Criteria criteria = session.createCriteria(DocumentList.class);
				criteria.add(Restrictions.eq("documentID.rollnumber", bean.getRegistration().getRollnumber()));
				String sqlStr = "select count(documentName) from documentlist where rollnumber='"
						+ bean.getRegistration().getRollnumber() + "'";

				if (documentName != null && !documentName.equals("")) {
					criteria.add(Restrictions.ilike("documentID.documentName", "%" + documentName + "%"));
					sqlStr = sqlStr + " and documentName like '%" + documentName + "%'";
				}
				criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
				criteria.setMaxResults(pagination.getPageSize());
				NativeQuery<?> query = session.createSQLQuery(sqlStr);
				BigInteger totalCount = (BigInteger) query.uniqueResult();
				pagination.setTotalDisplayRecords(totalCount.intValue());
				allDocumentList = criteria.list();
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public List<DocumentList> getAllDocumentList() {
		return allDocumentList;
	}

	public void setAllDocumentList(List<DocumentList> allDocumentList) {
		this.allDocumentList = allDocumentList;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSeletedRecord() {
		try {
			if (selectedDocumentList != null && selectedDocumentList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (DocumentList documentList : selectedDocumentList) {
					session.delete(documentList);
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
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void verifySelected() {
		try {
			if (selectedDocumentList != null && selectedDocumentList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (DocumentList documentList : selectedDocumentList) {
					documentList.setIsVerifiedWithOrgnal(true);
					documentList.setVerifiedDate(Calendar.getInstance().getTime());
					session.update(documentList);
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

	public List<DocumentList> getSelectedDocumentList() {
		return selectedDocumentList;
	}

	public void setSelectedDocumentList(List<DocumentList> selectedDocumentList) {
		this.selectedDocumentList = selectedDocumentList;
	}

}