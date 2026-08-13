package tpo.util;

public enum IMAGECONS {
	company("company"), notice("notice"), openingXls("openingXls"), shortlistedxls("shortlistedxls"),
	student("student/"), userlogo("userlogo"), userprofilepics("userprofilepics"), certificate("certificate"),resume("resume");

	private final String text;

	/**
	 * @param text
	 */
	IMAGECONS(final String text) {
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return text;
	}
}
