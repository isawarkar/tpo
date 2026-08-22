package com.ut.fbn.admin.task;

import org.json.JSONObject;

import com.ut.fbn.admin.util.FBNUtil;
import com.ut.fbn.admin.util.WSURL;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

public class DeleteTask extends AsyncTask<String, Void, String> {
	Context context;
	String companyID;
	String hallticketId;
	private Activity parentActivity;

	public DeleteTask(Context context, String companyID, String hallticketId, Activity parentActivity) {
		this.context = context;
		this.parentActivity = parentActivity;
		this.companyID = companyID;
		this.hallticketId = hallticketId;
	}

	protected void onPreExecute() {
		FBNUtil.showProgress(parentActivity);
	}

	@Override
	protected String doInBackground(String... urls) {
		String response = "";

		try {

			JSONObject dato = new JSONObject();
			if (companyID != null) {
				dato.put("companyId", companyID);
				response = FBNUtil.callWebService(context, dato, WSURL.DELETE_COMPANY);
			}
			if (hallticketId != null) {
				dato.put("hallticketId", hallticketId);
				response = FBNUtil.callWebService(context, dato, WSURL.DELETE_OPENING);
			}

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

}
