package tpo.hibernate.annotation;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class BookmarkID implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "userName")
	private String userName;

	@Column(name = "bookmark")
	private String bookMark;
	
    public BookmarkID() {

    }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBookMark() {
		return bookMark;
	}

	public void setBookMark(String bookMark) {
		this.bookMark = bookMark;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bookMark == null) ? 0 : bookMark.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
		BookmarkID other = (BookmarkID) obj;
		if (bookMark == null) {
			if (other.bookMark != null)
				return false;
		} else if (!bookMark.equals(other.bookMark))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

    
	
}