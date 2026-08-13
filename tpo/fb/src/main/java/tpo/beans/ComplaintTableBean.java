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

import javax.mail.Message;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.openfaces.event.AjaxActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.email.EmailUtil;
import tpo.hibernate.annotation.Complaint;
import tpo.util.FbMessageUtil;
import tpo.util.FbResourceUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("ComplaintTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class ComplaintTableBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(ComplaintTableBean.class);

	private Integer complaintNumber;

	private List<Complaint> complaintList = null;

	private List<Complaint> selectedComplaintList = new ArrayList<Complaint>();

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private Pagination pagination;

	private Complaint complaint;

	private Boolean status;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSeletedRecord() {
		complaint = null;
		try {
			if (selectedComplaintList != null && selectedComplaintList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error4));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (Complaint complaint : selectedComplaintList) {
					session.delete(complaint);
				}
				complaintList = null;
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
		complaint = null;
		try {
			if (selectedComplaintList != null && selectedComplaintList.size() == 0) {
				UIBackingBean.setInfoMessage(
						FbMessageUtil.getLabel("Please_select_at_least_one_record_to_change_the_Status"));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (Complaint complaint : selectedComplaintList) {
					if (complaint.getStatus()) {
						complaint.setStatus(false);
					} else {
						complaint.setStatus(true);
					}
					session.update(complaint);
					EmailUtil emailUtill = getEmailInstance();
					if (emailUtill != null) {
						List<String> address = new ArrayList<String>(1);
						address.add(complaint.getEmail());
						StringBuffer message = new StringBuffer();
						message.append(
								FbMessageUtil.getLabel("Update_on_your_complaint", complaint.getComplaintNumber()));
						message.append("\n");
						message.append("\n");
						message.append(FbResourceUtil.getLabel("Status")).append("=").append(complaint.getStatus()
								? FbResourceUtil.getLabel("Closed") : FbResourceUtil.getLabel("Open"));
						message.append("\n");
						message.append(complaint.getComplaint());
						message.append("\n");
						message.append(TpoUtil.getMesageString());
						emailUtill.postMail(address,
								FbMessageUtil.getLabel("Update_on_your_complaint", complaint.getComplaintNumber()),
								message.toString(), TpoUtil.ADMIN_EMAIL, Message.RecipientType.TO);
						Object param[] = new Object[2];
						param[0] = complaint.getName();
						param[1] = complaint.getComplaintNumber();
						UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("dear", param));
					} else {
						UIBackingBean
								.setInfoMessage(FbMessageUtil.getLabel("In_Local_System_Mode_email_can_not_be_sent"));
					}
				}
				complaintList = null;
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
		complaint = null;
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
	}

	public void setComplaintToUpdate(AjaxActionEvent event) {
		try {
			if (selectedComplaintList != null && selectedComplaintList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Info12"));
				complaint = null;
			} else {
				if (selectedComplaintList.size() > 1) {
					UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
					complaint = null;
				} else {
					complaint = selectedComplaintList.get(0);
					String complanyStr = complaint.getComplaint();
					complaint.setUpdatedOn(new Date());
					complanyStr = complanyStr + "<br/>########################Updated On " + complaint.getUpdatedOn()+ "########################<br/>";
					complaint.setComplaint(complanyStr);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setComplaint(AjaxActionEvent event) {
		try {
			if (selectedComplaintList != null && selectedComplaintList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Info12"));
				complaint = null;
			} else {
				if (selectedComplaintList.size() > 1) {
					UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_only_one_record_to_update"));
					complaint = null;
				} else {
					complaint = selectedComplaintList.get(0);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void inIt() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(Complaint.class);
			String queryStr = "select count(complaint) from complaints where 1=1";

			if (complaintNumber != null && !complaintNumber.equals("")) {
				criteria.add(Restrictions.eq("complaintNumber", complaintNumber));
				queryStr = queryStr + " and  complaintNumber like '%" + complaintNumber + "%'";
			}

			if (status != null && !status.equals("")) {
				criteria.add(Restrictions.eq("status", status));
				queryStr = queryStr + " and  status = " + status + "";
			}

			NativeQuery<BigInteger> query = session.createSQLQuery(queryStr);
			criteria.addOrder(Order.asc("createdOn"));
			criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
			criteria.setMaxResults(pagination.getPageSize());
			BigInteger totalCount = (BigInteger) query.uniqueResult();
			pagination.setTotalDisplayRecords(totalCount.intValue());
			complaintList = criteria.list();

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public Integer getComplaintNumber() {
		return complaintNumber;
	}

	public void setComplaintNumber(Integer complaintNumber) {
		this.complaintNumber = complaintNumber;
	}

	public List<Complaint> getComplaintList() {
		return complaintList;
	}

	public void setComplaintList(List<Complaint> complaintList) {
		this.complaintList = complaintList;
	}

	public List<Complaint> getSelectedComplaintList() {
		return selectedComplaintList;
	}

	public void setSelectedComplaintList(List<Complaint> selectedComplaintList) {
		this.selectedComplaintList = selectedComplaintList;
	}

	public Complaint getComplaint() {
		return complaint;
	}

	public void setComplaint(Complaint complaint) {
		this.complaint = complaint;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	/*
	 * public void generateXls() { try { if (noticeList != null &&
	 * !noticeList.isEmpty()) { String fileName =
	 * ExcelHandler.generateNoticListXls((selectedNoticeList != null &&
	 * selectedNoticeList.size() >0)?selectedNoticeList:noticeList); if
	 * (fileName != null) { ExcelHandler.renderReport(fileName); } } else {
	 * UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("list_is_empty")); }
	 * } catch (HibernateException e) { logger.error(e.getMessage()); e.printStackTrace(); }
	 * catch (Exception e) { logger.error(e.getMessage()); e.printStackTrace(); } }
	 * 
	 * public void generatePdfReport() { try { if (noticeList != null &&
	 * !noticeList.isEmpty()) {
	 * PDFGenerator.getInstance().generateNoticeList((selectedNoticeList != null
	 * && selectedNoticeList.size() >0)?selectedNoticeList:noticeList); } else {
	 * UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("list_is_empty")); }
	 * } catch (HibernateException e) { logger.error(e.getMessage()); e.printStackTrace(); }
	 * catch (Exception e) { logger.error(e.getMessage()); e.printStackTrace(); }
	 * 
	 * }
	 */

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateComplaint() {
		update();
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateAndCloseComplaint() {
		complaint.setStatus(true);
		update();
	}

	private void update() {
		try {
			EmailUtil emailUtill = getEmailInstance();
			if (emailUtill != null) {
				Session session = sessionFactory.getCurrentSession();
				session.update(complaint);
				List<String> address = new ArrayList<String>(1);
				address.add(complaint.getEmail());
				StringBuffer message = new StringBuffer(FbMessageUtil.getLabel("Update_on_your_complaint"));
				message.append("\n")
						.append(FbMessageUtil.getLabel("Update_on_your_complaint", complaint.getComplaintNumber()));
				message.append("\n");
				message.append(complaint.getComplaint());
				message.append("\n");
				message.append(TpoUtil.getMesageString());
				emailUtill.postMail(address,
						FbMessageUtil.getLabel("Update_on_your_complaint", complaint.getComplaintNumber()),
						message.toString(), TpoUtil.ADMIN_EMAIL, Message.RecipientType.TO);
				UIBackingBean.setSuccessMessage(
						FbMessageUtil.getLabel("is_successfully_updated", complaint.getComplaintNumber()));
			} else {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("In_Local_System_Mode_email_can_not_be_sent"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

}