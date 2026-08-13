package tpo.beans;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;

import org.openfaces.component.command.CommandLink;
import org.openfaces.event.AjaxActionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import tpo.dao.CommonDBBean;
import tpo.hibernate.annotation.Bookmark;
import tpo.util.FbMessageUtil;

@Component("BookmarkBean")
@Scope("session")
public class BookmarkBean {

	@Autowired
	private CommonDBBean commonDBBean;

	private String name;

	private Bookmark bookmark;

	public String getIcon(String name) {
    	bookmark =  commonDBBean.getBookmark(name);
    	if(bookmark != null) {
    		return "bxs-star";
    	}else {
    		return "bx-star";
    	}
    	
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBookmark(AjaxActionEvent event) {
		if (event != null) {
			CommandLink link = (CommandLink) event.getSource();
			if (link != null) {
				List<UIComponent> list = link.getChildren();
				UIParameter parameter = (UIParameter) list.get(0);
				name = (String) parameter.getValue();
				//bookmark = commonDBBean.getBookmark(name);
				if (bookmark == null) {
					commonDBBean.addBookMark(name);
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("bookmarkAdded"));
				} else {
					commonDBBean.deleteBookMark(name);
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("bookmarkRemoved"));
				}
			}
		}
	}
}
