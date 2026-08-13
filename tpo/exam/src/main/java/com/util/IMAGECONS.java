package com.util;

public enum IMAGECONS {
	student("student/"), certificate("certificate");

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
