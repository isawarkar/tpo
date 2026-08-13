/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.admin.beans;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.faces.component.html.HtmlSelectBooleanCheckbox;

import org.apache.catalina.core.ApplicationPart;
import org.openfaces.component.ajax.Ajax;
import org.openfaces.event.AjaxActionEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import tpo.beans.UIBackingBean;
import tpo.util.FbMessageUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Component("EmailAttachmentBean")
@Scope("session")
public class EmailAttachmentBean {

	private ApplicationPart file1;

	private ApplicationPart file2;

	private ApplicationPart file3;

	private ApplicationPart file4;

	private ApplicationPart file5;

	List<File> fileList;
	
	private boolean showWindow;

	public void attach() {
		try {
			fileList = TpoUtil.createTempFileList(this);
			if(fileList != null && fileList.size() >0 )
			UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("files_attached",fileList.size()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ApplicationPart getFile1() {
		return file1;
	}

	public void setFile1(ApplicationPart file1) {
		this.file1 = file1;
	}

	public ApplicationPart getFile2() {
		return file2;
	}

	public void setFile2(ApplicationPart file2) {
		this.file2 = file2;
	}

	public ApplicationPart getFile3() {
		return file3;
	}

	public void setFile3(ApplicationPart file3) {
		this.file3 = file3;
	}

	public ApplicationPart getFile4() {
		return file4;
	}

	public void setFile4(ApplicationPart file4) {
		this.file4 = file4;
	}

	public ApplicationPart getFile5() {
		return file5;
	}

	public void setFile5(ApplicationPart file5) {
		this.file5 = file5;
	}

	public List<File> getFileList() {
		return fileList;
	}

	public void setFileList(List<File> fileList) {
		this.fileList = fileList;
	}

	public boolean isShowWindow() {
		return showWindow;
	}

	public void setShowWindow(boolean showWindow) {
		this.showWindow = showWindow;
	}
	
	public void clearAjax(AjaxActionEvent event) {
			if (event != null) {
				Ajax ajax =(Ajax) event.getSource();
					if (ajax != null) {
						HtmlSelectBooleanCheckbox booleanCheckbox = (HtmlSelectBooleanCheckbox)ajax.getParent();
						if(booleanCheckbox !=null && !((Boolean) booleanCheckbox.getValue()).booleanValue()){
							fileList = null;
						}
						
				}
			}
	}
	
	public void clearFileList() {
		fileList = null;
	}

}
