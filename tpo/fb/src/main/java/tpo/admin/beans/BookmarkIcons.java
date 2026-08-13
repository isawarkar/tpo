package tpo.admin.beans;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component("BookmarkIcons")
public class BookmarkIcons {

	private static Map<String, String> map = new HashMap<String, String>();
	static {
		map.put("adminResultList", "bxs-zap");
		map.put("broadCastMessage", "bx-mobile-vibration");
		map.put("collegeGroup", "bxs-user-circle");
		map.put("collegeList", "bxs-school");
		map.put("companyList", "bx-building-house");
		map.put("complaintList", "bx-user-x");
		map.put("createOpeninng", "bx-buildings");
		map.put("currentTestDashboard", "bx-pulse");
		map.put("effortsList", "bx-list-plus");
		map.put("effortsListReport", "bx-list-ol");
		map.put("examList", "bx-paint");
		map.put("generateTestUser", "bxs-user-plus");
		map.put("hallTicketList", "bx-download");
		map.put("messageBoard", "bx-mail-send");
		map.put("moduleList", "bx-font-size");
		map.put("noticeList", "bxs-collection");
		map.put("openningList", "bxs-search");
		map.put("projectList", "bxs-book");
		map.put("questionsList", "bx-help-circle");
		map.put("referralList", "bx-send");
		map.put("review", "bxs-bookmark-star");
		map.put("sendNewsLetter", "bx-mail-send");
		map.put("shortRecord", "bxl-stripe");
		map.put("shortRecordHistoryList", "bxs-file-find");
		map.put("studentFeeList", "bxs-dollar-circle");
		map.put("studentList", "bxs-user-badge");
		map.put("userList", "bx-group");
		map.put("newsLetterList", "bx-mail-send");
		
		map.put("doBackup", "bxs-save");
		map.put("restoreBackup", "bxs-data");
		map.put("downloadBackup", "bx-download");
		map.put("investmentList", "bxs-dollar-circle");
	}

	public String getIcon(String key) {
		return map.get(key);
	}

}
