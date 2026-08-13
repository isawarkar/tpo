package com.ut.task;

import org.json.JSONObject;

import com.ut.fbn.MainActivity;
import com.ut.util.FBNUtil;
import com.ut.util.WSURL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class ForgotPasswordTask extends AsyncTask<String, Void, String> {
	String enrollmentNo;
	String email;
	Context context;
	private Activity parentActivity;

	public ForgotPasswordTask(Context contex, String enrollmentNo, String email, Activity parentActivity) {
		this.context = contex;
		this.parentActivity = parentActivity;
		this.enrollmentNo = enrollmentNo;
		this.email = email;
	}

	protected void onPreExecute() {
		FBNUtil.showProgress(parentActivity);
	}

	@Override
	protected String doInBackground(String... urls) {
		String response = "";

		try {
			JSONObject dato = new JSONObject();
			dato.put("enrollmetNo", enrollmentNo);
			dato.put("email", email);
			dato.put("BASE_PATH", WSURL.BASE_PATH);
			response = FBNUtil.callWebService(context,dato, WSURL.FORGOT_PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
			FBNUtil.hideProgress(parentActivity);
		} catch (Throwable e) {
			e.printStackTrace();
			FBNUtil.hideProgress(parentActivity);
		}
		return response;
	}

	@Override
	protected void onPostExecute(String response) {
		if (response != null) {
			if (response.substring(0, 1).equalsIgnoreCase("S")) {
				FBNUtil.showSucessDialog(response.substring(1, response.length()), parentActivity);
				context.getSharedPreferences(FBNUtil.MY_PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit();
				FBNUtil.hideProgress(parentActivity);
			} else {
				FBNUtil.hideProgress(parentActivity);
				FBNUtil.showErrorDialog(response.substring(1, response.length()), parentActivity);
			}
		}
	}

	private void navigateToHome() {
		Intent homeIntent = new Intent(context, MainActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		parentActivity.startActivity(homeIntent);
	}

}
