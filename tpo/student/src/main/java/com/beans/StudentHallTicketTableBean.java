/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.beans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;

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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dao.CommonDBBean;
import com.hibernate.Company;
import com.hibernate.HallTicket;
import com.hibernate.HallTicketConnect;
import com.hibernate.Registration;
import com.pdf.generator.PDFGenerator;
import com.util.FbMessageUtil;
import com.util.ResourceID;
import com.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("StudentHallTicketTableBean")
@Transactional(readOnly = true)
@Scope("session")
public class StudentHallTicketTableBean {

	private Logger logger = LoggerFactory.getLogger(StudentHallTicketTableBean.class);

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private CommonDBBean commonDBBean;
	
	@Autowired
	private PDFGenerator pDFGenerator;
	
	private List<Company> companyList;

	@Autowired
	private Pagination pagination;

	private List<HallTicketConnect> hallTicketList;

	private List<HallTicketConnect> hallTicketSelectedList = new ArrayList<HallTicketConnect>();
	
	private String criteria;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void apply() {
		try {
			if (hallTicketSelectedList != null && hallTicketSelectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_at_least_one_record_to_Apply"));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (HallTicketConnect connect : hallTicketSelectedList) {
					if (connect.getId().getHallTicket().getIsActive()) {
						connect.setIsApplied(true);
						connect.setAppliedOn(Calendar.getInstance().getTime());
						session.update(connect);
						UIBackingBean.setSuccessMessage(
								FbMessageUtil.getLabel("You_have_successfully_applied_for_selected_company"));
					} else {
						UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("You_can_not_apply_for",connect.getId().getHallTicket().getCompanyName())
								);

					}
				}
				hallTicketList = null;
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
	public void remove() {
		try {
			if (hallTicketSelectedList != null && hallTicketSelectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("Please_select_at_least_one_record_to_Remove"));
			} else {
				Session session = sessionFactory.getCurrentSession();
				for (HallTicketConnect connect : hallTicketSelectedList) {
					connect.setIsApplied(false);
					connect.setIsApproved(false);
					connect.setAppliedOn(null);
					connect.setApprovedOn(null);
					session.update(connect);
					UIBackingBean.setSuccessMessage(
							FbMessageUtil.getLabel("You_have_successfully_removed_for_selected_company"));
				}
				hallTicketList = null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void download() {
		try {
			if (hallTicketSelectedList != null && hallTicketSelectedList.size() == 0) {
				UIBackingBean.setInfoMessage(FbMessageUtil.getLabel(ResourceID.Error26));
			} else {
				List<HallTicket> hallTicketList = new ArrayList<HallTicket>(hallTicketSelectedList.size());
				for (HallTicketConnect connect : hallTicketSelectedList) {
					if (connect.getId().getHallTicket().getIsActive()) {
						if (connect.getIsApplied()) {
							if (connect.getIsApproved()) {
								hallTicketList.add(connect.getId().getHallTicket());
							} else {
								UIBackingBean.setErrorMessage(
										FbMessageUtil.getLabel("You_can_not_download_the_Hall_Ticket_for",
												connect.getId().getHallTicket().getCompanyName()));
								return;
							}
						} else {
							UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("You_have_not_applied_for",
									connect.getId().getHallTicket().getCompanyName()));
							return;
						}
					} else {
						UIBackingBean.setInfoMessage(FbMessageUtil.getLabel("You_can_not_download_hall_ticket_for",
								connect.getId().getHallTicket().getCompanyName()));
						return;

					}
				}

				if (hallTicketList.size() > 0) {
					for(HallTicket hallTicket:hallTicketList) {
						hallTicket.setCompany(commonDBBean.getCompnay(hallTicket.getCompanyID()));
					}
					Session session = sessionFactory.getCurrentSession();
					Registration registration = (Registration) session.get(Registration.class,
							Student.getStudent().getRollNumber());
					NativeQuery<String> collegeQ = session.createSQLQuery("SELECT userName FROM college  where CollegeName = '"
							+ registration.getCollegeName() + "'");
					String userName = (String) collegeQ.uniqueResult();
					
					TpoUtil.renderPDFFile(pDFGenerator.generateHallTicket(registration, hallTicketList, userName),registration.getRollnumber());
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

	@SuppressWarnings("unchecked")
	public List<HallTicketConnect> getHallTicketList() {
		return hallTicketList;
	}

	public List<HallTicketConnect> getHallTicketSelectedList() {
		return hallTicketSelectedList;
	}

	public void setHallTicketSelectedList(List<HallTicketConnect> hallTicketSelectedList) {
		this.hallTicketSelectedList = hallTicketSelectedList;
	}

	public void inIt() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Student student = Student.getStudent();
			if (student != null) {
				Criteria criteria = session.createCriteria(HallTicketConnect.class);
				criteria.add(Restrictions.eq("id.rollnumber", student.getRollNumber()));
				NativeQuery<BigInteger> query = session
						.createSQLQuery("SELECT count(rollnumber) FROM hallticketconnect where rollnumber = '"
								+ student.getRollNumber() + "'");
				criteria.setFirstResult(pagination.getPageSize() * (pagination.getCurrentPage() - 1));
				criteria.setMaxResults(pagination.getPageSize());
				hallTicketList = criteria.list();
				BigInteger totalCount = (BigInteger) query.uniqueResult();
				pagination.setTotalDisplayRecords(totalCount.intValue());
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
	
	public void setCompanyName(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					Integer companyID = (Integer) parameter.getValue();
					if(companyID != null) {
						companyList = new ArrayList<>(1);
						companyList.add(commonDBBean.getCompnay(companyID));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public List<Company> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<Company> companyList) {
		this.companyList = companyList;
	}

	public void setCritera(AjaxActionEvent event) {
		try {
			if (event != null) {
				CommandLink link = (CommandLink) event.getSource();
				if (link != null) {
					List<UIComponent> list = link.getChildren();
					UIParameter parameter = (UIParameter) list.get(0);
					criteria = (String) parameter.getValue();
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
	
	

}