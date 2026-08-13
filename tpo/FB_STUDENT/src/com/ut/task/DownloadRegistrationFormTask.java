package com.ut.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.ut.fbn.MainActivity;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;
import com.ut.util.WSURL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.util.Base64;

public class DownloadRegistrationFormTask extends AsyncTask<String, Void, String> {
	String enrollmentNo;
	Context context;
	private Activity parentActivity;

	public DownloadRegistrationFormTask(String enrollmetNo, Context contex, Activity parentActivity) {
		this.enrollmentNo = enrollmetNo;
		this.context = contex;
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
			dato.put("enrollmetNo", enrollmentNo);
			response = FBNUtil.callWebService(context,dato, WSURL.DOWNLOAD_REGISTRATION_FORM_WS);
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
				String fileContent = jsonObj.getString("fileContent");
				String fineName = enrollmentNo + ".PDF";
				File mydir = new File(FBNConstants.FBN_DOWNLOAD_DIR); // Creating an internal dir;
				File file = null;
				if (!mydir.exists()) {
					mydir.mkdirs();
					file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fineName);
				} else {
					file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fineName);
				}

				FileOutputStream fos = new FileOutputStream(file);
				fos.write(Base64.decode(fileContent, Base64.NO_WRAP));
				fos.close();

				Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
				Intent share = new Intent();
				share.setAction(Intent.ACTION_SEND);
				share.setType("application/pdf");
				share.putExtra(Intent.EXTRA_STREAM, uri);
				share.putExtra(Intent.EXTRA_TEXT, "Resume of " + enrollmentNo);
				share.putExtra(Intent.EXTRA_SUBJECT, "Resume of " + enrollmentNo);
				share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				parentActivity.startActivity(Intent.createChooser(share, "Share File"));
				FBNUtil.hideProgress(parentActivity);
			} catch (JSONException e) {
				e.printStackTrace();
				FBNUtil.hideProgress(parentActivity);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				FBNUtil.hideProgress(parentActivity);
			} catch (IOException e) {
				e.printStackTrace();
				FBNUtil.hideProgress(parentActivity);
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