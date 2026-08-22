package com.ut.fbn.admin.task;

import org.json.JSONObject;

import com.ut.fbn.admin.MainActivity;
import com.ut.fbn.admin.util.FBNConstants;
import com.ut.fbn.admin.util.FBNUtil;
import com.ut.fbn.admin.util.WSURL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class ChangePasswordTask extends AsyncTask<String, Void, String> {
	JSONObject registerObject;
	Context context;
	private Activity parentActivity;

	public ChangePasswordTask(Context contex, JSONObject registerObject, Activity parentActivity) {
		this.context = contex;
		this.parentActivity = parentActivity;
		this.registerObject = registerObject;
	}

	protected void onPreExecute() {
		FBNUtil.showProgress(parentActivity);
	}

	@Override
	protected String doInBackground(String... urls) {
		String response = "";

		try {
			response = FBNUtil.callWebService(context, registerObject, WSURL.CHANAGE_PASS_WS);
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
				context.getSharedPreferences(FBNConstants.MY_PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit();
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
