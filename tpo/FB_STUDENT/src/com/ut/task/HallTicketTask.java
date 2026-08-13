package com.ut.task;

import org.json.JSONException;
import org.json.JSONObject;

import com.ut.fbn.MainActivity;
import com.ut.util.FBNUtil;
import com.ut.util.WSURL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class HallTicketTask extends AsyncTask<String, Void, String> {
	String enrollmetNo;
	Integer hallticketid;
	Context context;
	boolean isApplied;
	private Activity parentActivity;

	public HallTicketTask(String enrollmetNo, Integer hallticketid, boolean isApplied, Context contex,
			Activity parentActivity) {
		this.enrollmetNo = enrollmetNo;
		this.hallticketid = hallticketid;
		this.isApplied = isApplied;
		this.context = contex;
		this.parentActivity = parentActivity;
	}

	protected void onPreExecute() {
		FBNUtil.showProgress(parentActivity);
	}

	@Override
	protected String doInBackground(String... urls) {
		String response = "";
		try {
			JSONObject dato = new JSONObject();
			dato.put("enrollmetNo", enrollmetNo);
			dato.put("hallticketid", hallticketid);
			dato.put("isApplied", isApplied);

			response = FBNUtil.callWebService(context,dato, WSURL.HALLTICKET_APPLY_WS);

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
			try {
				JSONObject jsonObj = new JSONObject(response);

				Boolean status = jsonObj.getBoolean("status");
				if (status) {
					try {
						FBNUtil.showSucessDialog("You Action is successfully completed!", context);
					} catch (Exception e) {
						FBNUtil.hideProgress(parentActivity);
						e.printStackTrace();
					}
				} else {
					FBNUtil.showErrorDialog("You Action is not completed successfully!", context);
				}
				FBNUtil.hideProgress(parentActivity);
				navigatetoHomeActivity();
			} catch (JSONException e) {
				FBNUtil.hideProgress(parentActivity);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method which navigates from Login Activity to Home Activity
	 */
	public void navigatetoHomeActivity() {
		Intent homeIntent = new Intent(context, MainActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		parentActivity.startActivity(homeIntent);
	}
}