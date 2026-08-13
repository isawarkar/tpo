/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.beans;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Component("pagination")
@Scope("session")
public class Pagination implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean previousLink;
	private boolean nextLink = true;
	private int totalDisplayRecords;

	/**
	 * The maximum page size of the result list
	 */
	private Integer pageSize = 10;

	/**
	 * The page the user is currently on.
	 */
	private int currentPage = 1;
	/**
	 * Calculation for Pagination
	 */
	private Integer pageFirstRecord = 1;
	private Integer pageLastRecord = 1;

	/**
	 * for accessing PreviousPage
	 */
	public void previousPage() {
		currentPage--;
		nextLink = true;
		if (currentPage == 1)
			previousLink = false;

		pageFirstRecord = ((currentPage - 1) * pageSize) + 1;
		pageLastRecord = pageSize * currentPage;
	}

	/**
	 * for accessing nextPage
	 */
	public void nextPage() {
		currentPage++;
		if (currentPage * pageSize >= totalDisplayRecords)
			nextLink = false;
		previousLink = true;

		pageFirstRecord = ((currentPage - 1) * pageSize) + 1;
		pageLastRecord = pageSize * currentPage;
		if (pageLastRecord > totalDisplayRecords) {
			pageLastRecord = totalDisplayRecords;
		}

	}

	/**
	 * for accessing firstPage
	 */
	public void firstPage() {
		currentPage = 1;
		nextLink = true;
		previousLink = false;

		pageFirstRecord = ((currentPage - 1) * pageSize) + 1;
		pageLastRecord = pageSize * currentPage;
		if (pageLastRecord > totalDisplayRecords) {
			pageLastRecord = totalDisplayRecords;
		}
	}

	/**
	 * for accessing lastPage
	 */
	public void lastPage() {
		currentPage = new Double(
				Math.ceil(1.0 * totalDisplayRecords / pageSize)).intValue();
		nextLink = false;
		previousLink = true;

		pageFirstRecord = ((currentPage - 1) * pageSize) + 1;
		pageLastRecord = pageSize * currentPage;
		if (pageLastRecord > totalDisplayRecords) {
			pageLastRecord = totalDisplayRecords;
		}

	}

	/**
	 * 
	 * @return pageSize
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/**
	 * 
	 * @param pageSize
	 *            set the parameter pageSize
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 
	 * @return currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * 
	 * @param currentPage
	 *            set the parameter currentPage
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * Navigation of the PreviousLink and next NextLink
	 */

	public boolean isPreviousLink() {
		return previousLink;
	}

	/**
	 * @param previousLink
	 */
	public void setPreviousLink(boolean previousLink) {
		this.previousLink = previousLink;
	}

	/**
	 * 
	 * @return true/false for the isNectLink.
	 */
	public boolean isNextLink() {
		return nextLink;
	}

	/**
	 * 
	 * @param nextLink
	 *            set the Parameter nextLink
	 */
	public void setNextLink(boolean nextLink) {
		this.nextLink = nextLink;
	}

	/**
	 * 
	 * @return totalDisplayRecords
	 */
	public int getTotalDisplayRecords() {
		return totalDisplayRecords;
	}

	/**
	 * 
	 * @param totalDisplayRecords
	 *            set the Parameter totalDisplayRecords
	 */
	public void setTotalDisplayRecords(int totalDisplayRecords) {

		this.totalDisplayRecords = totalDisplayRecords;
		pageLastRecord = pageSize * currentPage;
		if (pageLastRecord > totalDisplayRecords) {
			pageLastRecord = totalDisplayRecords;
		}

	}

	/**
	 * 
	 * @return pageFirstRecord
	 */
	public Integer getPageFirstRecord() {
		return pageFirstRecord;
	}

	/**
	 * 
	 * @param pageFirstRecord
	 *            set the Parameter pageFirstRecord
	 */
	public void setPageFirstRecord(Integer pageFirstRecord) {
		this.pageFirstRecord = pageFirstRecord;
	}

	/**
	 * 
	 * @return pageLastRecord
	 */
	public Integer getPageLastRecord() {
		return pageLastRecord;
	}

	/**
	 * 
	 * @param pageLastRecord
	 *            set the Parameter pageLastRecord
	 */

	public void setPageLastRecord(Integer pageLastRecord) {
		this.pageLastRecord = pageLastRecord;
	}

	/**
	 * 
	 * Ajax Call for resetting the pagesize.
	 */
	public void resetCurrentPage() {
		previousLink = false;
		nextLink = true;
		totalDisplayRecords = 0;
		pageSize = 10;
		currentPage = 1;
		pageFirstRecord = 1;
		pageLastRecord = 1;
	}

	public static Pagination getPagination() {
		Pagination pagination = (Pagination) TpoUtil
				.getManagedBean("pagination");
		return pagination;
	}

}