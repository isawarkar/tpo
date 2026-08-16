package tpo.beans;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import tpo.admin.beans.AdminUser;
import tpo.admin.beans.EmailAttachmentBean;
import tpo.admin.beans.RegistrationTableBean;
import tpo.dao.CommonDBBean;
import tpo.ets.beans.EffortReportTableBean;
import tpo.ets.beans.EffortTableBean;
import tpo.hibernate.annotation.BroadCastMessage;
import tpo.hibernate.annotation.CustomerReview;
import tpo.util.AES;
import tpo.util.CCPConstant;
import tpo.util.TpoUtil;

@Component("AdminMenu")
@Scope("session")
public class AdminMenu extends Parent {

	@Value("${enableEffortSystem:}")
	private String enableEffortSystem;

	private String enableEffortSystemFlag;

	@PostConstruct
	public void init() {
	    enableEffortSystemFlag = AES.symmetricDecrypt(
	        enableEffortSystem,
	        TpoUtil.getKeyInfo()
	    );
	}

	/*
	*/
	public String goToPage(String page) {
		Pagination pagination = Pagination.getPagination();
		if (pagination != null)
			pagination.resetCurrentPage();
		if ("examList".equals(page)) {
			ExamTableBean bean = (ExamTableBean) TpoUtil.getManagedBean(ExamTableBean.class.getSimpleName());
			if (bean != null) {
				bean.setExamName(null);
				bean.setResultList(null);
				bean.setClickedTestName(null);
			}
		} else if ("hallTicketList".equals(page)) {
			OpenningListTableBean bean = (OpenningListTableBean) TpoUtil
					.getManagedBean(OpenningListTableBean.class.getSimpleName());
			if (bean != null) {
				bean.setPieSelectedCategory(null);
				bean.setHallticketID(null);
				bean.setHallTicketIdString(null);
				bean.setHallTicketConnectSelectedList(null);
				bean.setPieSelectedCategoryChild(null);
				bean.setApplied(null);
				bean.setApproved(null);
			}
		} else if ("companyList".equals(page)) {
			CompanyTableBean bean = (CompanyTableBean) TpoUtil.getManagedBean(CompanyTableBean.class.getSimpleName());
			if (bean != null) {
				bean.setCompanyID(null);
				bean.setCompanyName(null);
			}
		} else if ("collegeList".equals(page)) {
			CollegeTableBean bean = (CollegeTableBean) TpoUtil.getManagedBean(CollegeTableBean.class.getSimpleName());
			if (bean != null) {
				bean.setCollegeName(null);
			}
		} else if ("studentList".equals(page)) {
			RegistrationTableBean bean = (RegistrationTableBean) TpoUtil
					.getManagedBean(RegistrationTableBean.class.getSimpleName());
			if (bean != null) {
				bean.resetSearch();
			}
		} 
		else if ("selectedStudentList".equals(page)) {
			RegistrationTableBean bean = (RegistrationTableBean) TpoUtil
					.getManagedBean(RegistrationTableBean.class.getSimpleName());
			if (bean != null) {
				bean.resetSearch();
				bean.setSelectedFlag(true);
				page = "studentList";
			}
		} 
		else if ("openningList".equals(page)) {
			OpenningListTableBean bean = (OpenningListTableBean) TpoUtil
					.getManagedBean(OpenningListTableBean.class.getSimpleName());
			if (bean != null) {
				bean.setCompanyName(null);
				bean.setListNull();
			}
		} else if ("shortRecordHistoryList".equals(page)) {
			ShortRecordTableBean bean = (ShortRecordTableBean) TpoUtil
					.getManagedBean(ShortRecordTableBean.class.getSimpleName());
			if (bean != null) {
				bean.setFileName(null);
			}
		} else if ("questionsList".equals(page)) {
			QuestionListTableBean bean = (QuestionListTableBean) TpoUtil
					.getManagedBean(QuestionListTableBean.class.getSimpleName());
			if (bean != null) {
				bean.setTestName(null);
				bean.setQuestionList(null);
			}
		} else if ("adminResultList".equals(page)) {
			StudentTableBean bean = (StudentTableBean) TpoUtil.getManagedBean(StudentTableBean.class.getSimpleName());
			if (bean != null) {
				bean.setLoginName(null);
				bean.setPieSelectedCategory(null);
				bean.setSelectedCategory(null);
				bean.setResult(null);
				bean.setTemp(false);
			}
		} else if ("userList".equals(page)) {
			UserTableBean bean = (UserTableBean) TpoUtil.getManagedBean(UserTableBean.class.getSimpleName());
			if (bean != null) {
				bean.setUserName(null);
			}
		} else if ("noticeList".equals(page)) {
			NoticeTableBean bean = (NoticeTableBean) TpoUtil.getManagedBean(NoticeTableBean.class.getSimpleName());
			if (bean != null) {
				bean.setNoticeName(null);
			}
		} else if ("effortsList".equals(page)) {
			EffortTableBean bean = (EffortTableBean) TpoUtil.getManagedBean(EffortTableBean.class.getSimpleName());
			if (bean != null) {
				bean.clearBean();
			}
		} else if ("effortsListReport".equals(page)) {
			EffortReportTableBean bean = (EffortReportTableBean) TpoUtil
					.getManagedBean(EffortReportTableBean.class.getSimpleName());
			if (bean != null) {
				bean.clearBean();
			}
		} else if ("studentFeeList".equals(page)) {
			FeeTableBean bean = (FeeTableBean) TpoUtil.getManagedBean(FeeTableBean.class.getSimpleName());
			if (bean != null) {
				bean.setEnrollmentNumber(null);
				bean.setShowWarnig(true);
			}
		} else if ("referralList".equals(page)) {
			ReferralTableBean bean = (ReferralTableBean) TpoUtil
					.getManagedBean(ReferralTableBean.class.getSimpleName());
			if (bean != null) {
				bean.clear();
			}
		} else if ("messageBoard".equals(page)) {
			EmailAttachmentBean attachmentBean = (EmailAttachmentBean) TpoUtil
					.getManagedBean(EmailAttachmentBean.class.getSimpleName());
			if (attachmentBean != null) {
				attachmentBean.setFileList(null);
			}
		} else if ("sendNewsLetter".equals(page)) {
			EmailAttachmentBean attachmentBean = (EmailAttachmentBean) TpoUtil
					.getManagedBean(EmailAttachmentBean.class.getSimpleName());
			if (attachmentBean != null) {
				attachmentBean.setFileList(null);
			}
		}

		else if ("broadCastMessage".equals(page)) {
			CommonDBBean bean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
			if (bean != null) {
				BroadCastMessageBean castMessageBean = (BroadCastMessageBean) TpoUtil
						.getManagedBean(BroadCastMessageBean.class.getSimpleName());
				if (castMessageBean != null) {
					BroadCastMessage broadCastMessage = bean.getCurrentBroadCastMessage(null,false);
					if (broadCastMessage != null) {
						castMessageBean.setBroadCastMessage(broadCastMessage);
						castMessageBean.setCurrentDocMode(CCPConstant.UPDATE);
					} else {
						castMessageBean.setCurrentDocMode(CCPConstant.CREATE);
						castMessageBean.setBroadCastMessage(new BroadCastMessage());
					}
				}
			}
		}

		return page;
	}

	public String goToPageShortRecord(String page, boolean flag) {
		CreateOpeninngBean createOpeninngBean = (CreateOpeninngBean) TpoUtil
				.getManagedBean(CreateOpeninngBean.class.getSimpleName());
		createOpeninngBean.setCreateOpeninngBool(flag);
		createOpeninngBean.setXlsFileName(null);
		EmailAttachmentBean attachmentBean = (EmailAttachmentBean) TpoUtil
				.getManagedBean(EmailAttachmentBean.class.getSimpleName());
		if (attachmentBean != null) {
			attachmentBean.setFileList(null);
		}
		return page;
	}

	public String goToPageCustomerReview(String page) {
		CustomerReviewBean customerReviewBean = (CustomerReviewBean) TpoUtil
				.getManagedBean(CustomerReviewBean.class.getSimpleName());
		CustomerReview customerReview = customerReviewBean.isRecordExist(AdminUser.getUser().getUserName());
		if (customerReview != null) {
			customerReviewBean.setComment(customerReview.getComment());
			customerReviewBean.setStarRating(customerReview.getStarRating());
		} else {
			customerReviewBean.setComment(null);
			customerReviewBean.setStarRating(0);
		}
		return page;
	}

	public String getEnableEffortSystemFlag() {
		return enableEffortSystemFlag;
	}

	public void setEnableEffortSystemFlag(String enableEffortSystemFlag) {
		this.enableEffortSystemFlag = enableEffortSystemFlag;
	}

}
