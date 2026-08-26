package com.ut.task;

import org.json.JSONException;
import org.json.JSONObject;

import com.ut.fbn.MainActivity;
import com.ut.util.DBController;
import com.ut.util.Encryption;
import com.ut.util.FBNUtil;
import com.ut.util.WSURL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class LoginTask extends AsyncTask<String, Void, String> {
	String userName;
	String password;
	Context context;
	private Activity parentActivity;

	public LoginTask(String userName, String password, Context contex, Activity parentActivity) {
		this.userName = userName;
		this.password = password;
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
			/*
			 * HttpClient httpClient = new DefaultHttpClient(); HttpPost post = new
			 * HttpPost(WSURL.LOGIN_WS); post.setHeader("content-type", "application/json");
			 */
			JSONObject dato = new JSONObject();
			dato.put("enrollmetNo", userName);
			dato.put("password", Encryption.getEncryptedString(password));

			response = FBNUtil.callWebService(context,dato, WSURL.LOGIN_WS);
			
			/*
			 * StringEntity entity = new StringEntity(dato.toString());
			 * post.setEntity(entity); HttpResponse resp = httpClient.execute(post); if
			 * (resp == null) { FBNUtil.showNoticeDetails("Error while Logging", "Error",
			 * parentActivity); FBNUtil.hideProgress(parentActivity); return null; }
			 * HttpEntity entityNew = resp.getEntity(); if (entityNew == null) {
			 * FBNUtil.showNoticeDetails("Error while Logging", "Error", parentActivity);
			 * FBNUtil.hideProgress(parentActivity); return null; } return
			 * EntityUtils.toString(entityNew);
			 */
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
				String status = jsonObj.getString("status");
				DBController controller = new DBController(context);
				if ("T".equals(status)) {
					try {
						controller.insertIntoUserInfo(userName);
						FBNUtil.showSucessDialog("You are successfully Logged In!", parentActivity);
						FBNUtil.setLogin(userName, context);
						navigatetoManinActivity();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ("B".equals(status)) {
					FBNUtil.showErrorDialog("Your account is Blocked.Please contact admin!", parentActivity);
					controller.deleteUserInfo();
				} else {
					FBNUtil.showErrorDialog("Please enter correct Enrollment No and Password!", parentActivity);
					controller.deleteUserInfo();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				FBNUtil.hideProgress(parentActivity);
			}
			FBNUtil.hideProgress(parentActivity);
		}
	}

	public void navigatetoManinActivity() {
		Intent homeIntent = new Intent(context, MainActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		parentActivity.startActivity(homeIntent);
	}
}