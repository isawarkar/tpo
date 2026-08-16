package tpo.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Message;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
import tpo.util.TpoUtil;

@Repository("FeedBackBean")
@Transactional(readOnly = true)
@Scope("session")
public class FeedBackBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(FeedBackBean.class);

	private Complaint complaint = new Complaint();
	
	private Integer complaintNo;

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void sendFeedback() {
		try {
			EmailUtil emailUtill = getEmailInstance();
			if (emailUtill != null) {
				int complaintNumber;
				Session session = sessionFactory.getCurrentSession();
				if(complaint.getComplaintNumber() != null){
					complaintNumber = complaint.getComplaintNumber();
					complaint.setStatus(Boolean.FALSE);
					session.update(complaint);
				}else{
					complaint.setStatus(Boolean.FALSE);
					complaintNumber = TpoUtil.getRandomNumber();
					complaint.setComplaintNumber(complaintNumber);
					Date date = new Date();
					complaint.setCreatedOn(date);
					complaint.setUpdatedOn(date);
					session.save(complaint);
				}
				List<String> address = new ArrayList<String>(2);
				String subject;
				address.add(superUserEmail);
				address.add(TpoUtil.ADMIN_EMAIL);
				subject = "Feedback from " + complaint.getName() + " Number is " + complaint.getContactNumber() + "";
				emailUtill
						.postMail(address, subject,
								complaint.getComplaint() + "\n" + "Your Complaint/Service number is "
										+ complaintNumber,
								complaint.getEmail(), Message.RecipientType.TO);
				address = new ArrayList<String>(1);
				address.add(complaint.getEmail());
				StringBuffer message = new StringBuffer(
						FbMessageUtil.getLabel("Thank_you_for_your_feedback", complaint.getName()));
				message.append("\n").append(FbMessageUtil.getLabel("Update_on_your_complaint",complaint.getComplaintNumber()));
				message.append("\n");
				message.append(TpoUtil.getMesageString());
				emailUtill.postMail(address, FbMessageUtil.getLabel("Thank_you_for_your_feedback", complaint.getName()),
						message.toString(), TpoUtil.ADMIN_EMAIL, Message.RecipientType.TO);
				Object param[] = new Object[2];
				param[0] = complaint.getName();
				param[1] = complaintNumber;
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("dear", param));
			} else {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("In_Local_System_Mode_email_can_not_be_sent"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void setComplaintFromPopup(AjaxActionEvent event) {
		complaint = null;
		setComplaint(event);
	}
	public void setComplaint(AjaxActionEvent event) {
		Session session = sessionFactory.getCurrentSession();
		if(complaint != null && complaint.getComplaintNumber()!= null){
			complaintNo = complaint.getComplaintNumber();
		}
		complaint = (Complaint)session.get(Complaint.class, complaintNo);
		if(complaint != null){
			String complanyStr = complaint.getComplaint();
			complaint.setUpdatedOn(new Date());
			complanyStr = complanyStr + "<br/>########################Updated On "+complaint.getUpdatedOn()+"########################<br/>";
			complaint.setComplaint(complanyStr);		
		}else{
			UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Complaint_not_found",complaintNo));
			complaint = new Complaint();
		}
	}

	public Complaint getComplaint() {
		return complaint;
	}

	public void setComplaint(Complaint complaint) {
		this.complaint = complaint;
	}

	public Integer getComplaintNo() {
		return complaintNo;
	}

	public void setComplaintNo(Integer complaintNo) {
		this.complaintNo = complaintNo;
	}
	
	

}
