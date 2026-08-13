package tpo.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import tpo.imageservice.client.FileUploadUtility;
import tpo.util.IMAGECONS;
import tpo.util.TpoUtil;

@Component("CreateOpeninngBean")
@Scope("session")
public class CreateOpeninngBean extends Parent {

	@Autowired
	private FileUploadUtility fileUploadUtility;

	private Boolean createOpeninngBool = false;
	
	private Boolean openingXls = false;
	
	private String xlsFileName = null;

	public Boolean getCreateOpeninngBool() {
		return createOpeninngBool;
	}

	public void setCreateOpeninngBool(Boolean createOpeninngBool) {
		this.createOpeninngBool = createOpeninngBool;
	}

	public String getXlsFileName() {
		return xlsFileName;
	}

	public void setXlsFileName(String xlsFileName) {
		this.xlsFileName = xlsFileName;
	}

	public void renderFile() {
		TpoUtil.renderEXcelFile(
				fileUploadUtility.downloadFile(getFileServiceUrl() + "/download", xlsFileName, openingXls ? IMAGECONS.openingXls : IMAGECONS.shortlistedxls),
				xlsFileName);
	}

	public Boolean getOpeningXls() {
		return openingXls;
	}

	public void setOpeningXls(Boolean openingXls) {
		this.openingXls = openingXls;
	}

	
}
