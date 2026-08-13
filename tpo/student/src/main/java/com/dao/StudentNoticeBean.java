package com.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.beans.Parent;

@Repository("StudentNoticeBean")
@Scope("session")
public class StudentNoticeBean extends Parent {

	private List<String> campusList = new ArrayList<String>();
	private List<String> noticeList = new ArrayList<String>();

	public List<String> getCampusList() {
		return campusList;
	}

	public void setCampusList(List<String> campusList) {
		this.campusList = campusList;
	}

	public List<String> getNoticeList() {
		return noticeList;
	}

	public void setNoticeList(List<String> noticeList) {
		this.noticeList = noticeList;
	}

	public Integer getCount() {
		int count = 0;
		if (campusList != null) {
			count = count + campusList.size();
		}
		if (noticeList != null) {
			count = count + noticeList.size();
		}

		return count;
	}
	
	public String getColor() {
		if(getCount() > 0 ) {
			return "red blinking";
		}else {
			return "green";
		}
	}

}
