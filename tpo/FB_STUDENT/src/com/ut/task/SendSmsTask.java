package com.ut.task;

import com.ut.util.FBNUtil;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class SendSmsTask extends AsyncTask<String, Void, String> {
	String mobileNo;
	String message;
	Activity activity;
	Context context;

	public SendSmsTask(String mobileNo, String message, Activity activity, Context context) {
		this.mobileNo = mobileNo;
		this.message = message;
		this.activity = activity;
		this.context = context;
	}

	protected void onPreExecute() {
	}

	@Override
	protected String doInBackground(String... urls) {
		try {
			FBNUtil.sendSMS(context, message, mobileNo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return "done";
	}

	@Override
	protected void onPostExecute(String response) {
		try {
			if (response != null && "done".equals(response))
				Toast.makeText(activity, "PIN sent successfully", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
