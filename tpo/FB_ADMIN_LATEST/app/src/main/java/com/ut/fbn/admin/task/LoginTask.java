package com.ut.fbn.admin.task;

import org.json.JSONException;
import org.json.JSONObject;

import com.ut.fbn.admin.MainActivity;
import com.ut.fbn.admin.ScanQRCodeActivity;
import com.ut.fbn.admin.util.DBController;
import com.ut.fbn.admin.util.Encryption;
import com.ut.fbn.admin.util.FBNUtil;
import com.ut.fbn.admin.util.WSURL;

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

			JSONObject dato = new JSONObject();
			dato.put("userName", userName);
			dato.put("password", Encryption.getEncryptedString(password));

			response = FBNUtil.callWebService(context, dato, WSURL.LOGIN_WS);

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
				Boolean logindetail = jsonObj.getBoolean("loginFlag");
				if (logindetail) {
					String role = jsonObj.getString("role");
					try {
						DBController controller = new DBController(context);
						controller.insertIntoUserInfo(userName, role);
						FBNUtil.showSucessDialog("You are successfully Logged In!", parentActivity);
						FBNUtil.setLogin(userName, context);
						navigatetoManinActivity();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					FBNUtil.showErrorDialog("Please enter correct Enrollment No and Password!", parentActivity);
					DBController controller = new DBController(context);
					controller.deleteUserInfo();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				FBNUtil.hideProgress(parentActivity);
			}
			FBNUtil.hideProgress(parentActivity);
		}
	}

	public void navigateToScanQRActivity() {
		Intent homeIntent = new Intent(context, ScanQRCodeActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		parentActivity.startActivity(homeIntent);
	}

	public void navigatetoManinActivity() {
		Intent homeIntent = new Intent(context, MainActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		parentActivity.startActivity(homeIntent);
	}
}