package tpo.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("ImageBean")
@Scope("request")
public class ImageBean {

	private boolean isImageCorrect = false;

	public boolean isImageCorrect() {
		return isImageCorrect;
	}

	public void setImageCorrect(boolean isImageCorrect) {
		this.isImageCorrect = isImageCorrect;
	}
	
	

}
