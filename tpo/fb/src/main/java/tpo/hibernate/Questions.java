/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.hibernate;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

import javax.xml.bind.DatatypeConverter;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import tpo.util.CCPConstant;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class Questions implements Serializable,JSONAware {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private QuestionsId id;

	private String question;

	private String optiona;

	private String optionb;

	private String optionc;

	private String optiond;

	private String answer;

	private Boolean isImage;

	private String questionType = CCPConstant.SINGLE;

	private Double assignedNo;

	private Blob image;

	@Override
	public String toJSONString() {
		JSONObject obj = new JSONObject();
		obj.put("question", question);
		obj.put("optiona", optiona);
		obj.put("optionb", optionb);
		obj.put("optionc", optionc);
		obj.put("optiond", optiond);
		obj.put("answer", answer);
		obj.put("isImage", isImage);
		obj.put("questionType", questionType);
		if(image != null){
		obj.put("image", getImageArray());
		}
		return obj.toString();
	}

	private String getImageArray() {
		String imageBase64 = null;
		try {
			int blobLength = (int) image.length();
			byte[] blobAsBytes = image.getBytes(1, blobLength);
			imageBase64 = DatatypeConverter.printBase64Binary(blobAsBytes);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return imageBase64;
	}
	// Constructors

	/** default constructor */
	public Questions() {
	}

	/** minimal constructor */
	public Questions(QuestionsId id) {
		this.id = id;
	}

	/** full constructor */
	public Questions(QuestionsId id, String question, String optiona, String optionb, String optionc, String optiond,
			String answer, String questionType) {
		this.id = id;
		this.question = question;
		this.optiona = optiona;
		this.optionb = optionb;
		this.optionc = optionc;
		this.optiond = optiond;
		this.answer = answer;
		this.questionType = questionType;
	}

	// Property accessors

	public QuestionsId getId() {
		return this.id;
	}

	public void setId(QuestionsId id) {
		this.id = id;
	}

	public String getQuestion() {
		return this.question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getOptiona() {
		return this.optiona;
	}

	public void setOptiona(String optiona) {
		this.optiona = optiona;
	}

	public String getOptionb() {
		return this.optionb;
	}

	public void setOptionb(String optionb) {
		this.optionb = optionb;
	}

	public String getOptionc() {
		return this.optionc;
	}

	public void setOptionc(String optionc) {
		this.optionc = optionc;
	}

	public String getOptiond() {
		return this.optiond;
	}

	public void setOptiond(String optiond) {
		this.optiond = optiond;
	}

	public String getAnswer() {
		return this.answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	/**
	 * @return the isImage
	 */
	public Boolean getIsImage() {
		return isImage;
	}

	/**
	 * @param isImage
	 *            the isImage to set
	 */
	public void setIsImage(Boolean isImage) {
		this.isImage = isImage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Questions other = (Questions) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public Double getAssignedNo() {
		return assignedNo;
	}

	public void setAssignedNo(Double assignedNo) {
		this.assignedNo = assignedNo;
	}

	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}

	public byte[] getImageBytes() {
		byte[] blobAsBytes = null;
		try {
			if(image != null){
			blobAsBytes = TpoUtil.convertInputStreamToBytesArray(image.getBinaryStream());
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return blobAsBytes;
	}

}