/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.hibernate.annotation;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import tpo.beans.AdminMenu;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Entity
@Table(name = "bookmarks")
public class Bookmark implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BookmarkID id;

	public BookmarkID getId() {
		return id;
	}

	public void setId(BookmarkID id) {
		this.id = id;
	}


	public String goToBookMark() {
		AdminMenu adminMenu = (AdminMenu) TpoUtil.getManagedBean(AdminMenu.class.getSimpleName());
		if (adminMenu != null) {
			if ("createOpeninng".equals(id.getBookMark())) {
				return adminMenu.goToPageShortRecord("shortRecord", true);
			} else if ("shortRecord".equals(id.getBookMark())) {
				return adminMenu.goToPageShortRecord("shortRecord", false);
			} else {
				return adminMenu.goToPage(id.getBookMark());
			}
		}
		return "";
	}

}