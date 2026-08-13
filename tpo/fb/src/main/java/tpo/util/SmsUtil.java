package tpo.util;

import java.util.ResourceBundle;

public class SmsUtil {

	static ResourceBundle labels;
	static {
		labels = ResourceBundle.getBundle("sms");
	}

	public static String getLabel(String key) {
		String value = labels.getString(key);
		if (value != null) {
			return value;
		}
		return key;
	}
}
