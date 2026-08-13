package com.ut.fbn.admin.sms;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
	private static SmsListener mListener;
	String pin;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle data = intent.getExtras();
		Object[] pdus = (Object[]) data.get("pdus");
		for (int i = 0; i < pdus.length; i++) {
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
			String senderMobileNo = smsMessage.getDisplayOriginatingAddress();
			if(senderMobileNo.startsWith("+91"))
			senderMobileNo = senderMobileNo.substring(3);
			List<String> list = new ArrayList<String>();
			list.add(senderMobileNo);
			// b=sender.endsWith("WNRCRP"); //Just to fetch otp sent from WNRCRP
			String messageBody = smsMessage.getMessageBody();
			if (messageBody != null && messageBody.contains("Fresher Buddy")) {
				pin = messageBody.replaceAll("[^0-9]", ""); // here abcd contains otp
				list.add(pin);
				// Pass on the text to our listener.
				mListener.messageReceived(list); // attach value to interface
			}
		}
	}

	public static void bindListener(SmsListener listener) {
		mListener = listener;
	}
}