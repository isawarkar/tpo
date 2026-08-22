package com.ut.fbn.admin.task;

import org.json.JSONException;
import org.json.JSONObject;

import com.ut.fbn.admin.util.FBNUtil;
import com.ut.fbn.admin.util.WSURL;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

public class UploadProfileImageTask extends AsyncTask<String, Void, String> {
	String registeredUserName;
	String encodedFile;
	Context context;
	Activity parentActivity;

	public UploadProfileImageTask(String registeredUserName, String encodedFile, Activity parentActivity,Context context) {
		this.registeredUserName = registeredUserName;
		this.context = context;
		this.encodedFile = encodedFile;
		this.parentActivity = parentActivity;
	}

	protected void onPreExecute() {
		FBNUtil.showProgress(parentActivity);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... urls) {
		String response = "";
		try {
			JSONObject dato = new JSONObject();
			dato.put("registeredUserName", registeredUserName);
			dato.put("encodedFile", encodedFile);
			response = FBNUtil.callWebService(context,dato, WSURL.UPLOAD_IMAGE_WS);
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
				String code = jsonObj.getString("code");
				if (code != null && "success".equals(code)) {
					FBNUtil.showSucessDialog("Image is uploaded succesfully", parentActivity);
					FBNUtil.hideProgress(parentActivity);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				FBNUtil.hideProgress(parentActivity);
			}
		}
	}

}