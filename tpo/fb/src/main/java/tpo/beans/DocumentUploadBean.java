package tpo.beans;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.core.ApplicationPart;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openfaces.component.command.CommandLink;
import org.openfaces.event.AjaxActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.dao.CommonDBBean;
import tpo.hibernate.annotation.DocumentID;
import tpo.hibernate.annotation.DocumentList;
import tpo.util.FbMessageUtil;
import tpo.util.TpoUtil;

@Component("DocumentUploadBean")
@Scope("session")
public class DocumentUploadBean implements Serializable {

	private Logger logger = LoggerFactory.getLogger(DocumentUploadBean.class);

	private DocumentList documentList = new DocumentList();

	private String documentName;
	private String documentNameOther;

	private String fileName;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ApplicationPart documnet;

	public byte[] getImageBytes() {
		byte[] b = null;
		try {
			StudentRegistrationBean bean = (StudentRegistrationBean) TpoUtil
					.getManagedBean(StudentRegistrationBean.class.getSimpleName());
			if (bean != null && bean.getRegistration() != null) {
				CommonDBBean commonDBBean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
				if (commonDBBean != null) {
					b = commonDBBean.getStudentProfilePic(bean.getRegistration());
					if (b == null) {
							b = TpoUtil.convertInputStreamToBytesArray(TpoUtil.getNAFile());
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return b;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void uploadDocument() {
		try {
			if("Other_Document".equals(documentName) && (documentNameOther == null || "".equals(documentNameOther))) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("please_enter_other_document_name"));
				return;
			}
			
			StudentRegistrationBean bean = (StudentRegistrationBean) TpoUtil
					.getManagedBean(StudentRegistrationBean.class.getSimpleName());
			Session session = sessionFactory.getCurrentSession();
			if (bean != null && session != null) {
				if (!"".equals(documnet.getSubmittedFileName())) {
					if (documnet.getSize() > TpoUtil.IMAGE_SIZE) {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_small_size_image_Photo"));
						return;
					}
					if (TpoUtil.imageTypes.contains(documnet.getContentType())) {
						Blob blob = Hibernate.getLobCreator(session).createBlob(documnet.getInputStream(),
								documnet.getSize());
						documentList.setDocument(blob);
					} else {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Only_Image_Type_can_be_uploaded"));
						return;
					}

				}
				DocumentID documentID = new DocumentID();
				if("Other_Document".equals(documentName) &&  !"".equals(documentNameOther)) {
					documentID.setDocumentName(documentNameOther);
				}else {
				documentID.setDocumentName(documentName);
				}
				documentID.setRollnumber(bean.getRegistration().getRollnumber());
				documentList.setDocumentID(documentID);
				documentList.setIsVerifiedWithOrgnal(false);
				documentList.setUploadedDate(Calendar.getInstance().getTime());
				session.saveOrUpdate(documentList);
				UIBackingBean
						.setSuccessMessage(FbMessageUtil.getLabel("Your_document_uploaded_successfully", documentName));
			}
		} catch (HibernateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public ApplicationPart getDocumnet() {
		return documnet;
	}

	public void setDocumnet(ApplicationPart documnet) {
		this.documnet = documnet;
	}

	public DocumentList getDocumentList() {
		return documentList;
	}

	public void setDocumentList(DocumentList documentList) {
		this.documentList = documentList;
	}

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

	public void setDocumentName(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					fileName = (String) parameter.getValue();
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

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public byte[] getImageBytesAsImage() {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		InputStream stream = null;
		try {
			if (fileName != null) {
				StudentRegistrationBean bean = (StudentRegistrationBean) TpoUtil
						.getManagedBean(StudentRegistrationBean.class.getSimpleName());
				Session session = sessionFactory.getCurrentSession();
				if (bean != null && session != null) {
					Criteria criteria = session.createCriteria(DocumentList.class);
					criteria.add(Restrictions.eq("documentID.rollnumber", bean.getRegistration().getRollnumber()));
					criteria.add(Restrictions.eq("documentID.documentName", fileName));
					DocumentList documentList = (DocumentList) criteria.uniqueResult();
					if (documentList != null) {
						stream = documentList.getDocument().getBinaryStream();
						byte[] buf = new byte[1000000];

						int bytesRead;
						do {
							bytesRead = stream.read(buf);
							if (bytesRead != -1)
								arrayOutputStream.write(buf, 0, bytesRead);
						} while (bytesRead != -1);
						arrayOutputStream.close();
					}
				}

			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return arrayOutputStream.toByteArray();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
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
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName+".jpeg");
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

	public String getDocumentNameOther() {
		return documentNameOther;
	}

	public void setDocumentNameOther(String documentNameOther) {
		this.documentNameOther = documentNameOther;
	}

	
}