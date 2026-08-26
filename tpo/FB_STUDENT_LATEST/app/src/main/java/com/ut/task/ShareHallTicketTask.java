package com.ut.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
import androidx.core.content.FileProvider;
import android.util.Base64;

public class ShareHallTicketTask extends AsyncTask<String, Void, String> {
	String enrollmentNo;
	Integer hallticketid;
	Context context;
	private Activity parentActivity;

	public ShareHallTicketTask(String enrollmetNo, Integer hallticketid, Context contex, Activity parentActivity) {
		this.enrollmentNo = enrollmetNo;
		this.hallticketid = hallticketid;
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
			dato.put("hallticketid", hallticketid);
			response = FBNUtil.callWebService(context,dato, WSURL.DOWNLOAD_HALLTICKET_WS);

		} catch (Exception e) {
			FBNUtil.hideProgress(parentActivity);
			e.printStackTrace();
			FBNUtil.showErrorDialog(e.getMessage(), context);
		}
		return response;
	}

	private static String read(InputStream instream) {
		StringBuilder sb = null;
		try {
			sb = new StringBuilder();
			BufferedReader r = new BufferedReader(new InputStreamReader(instream));
			for (String line = r.readLine(); line != null; line = r.readLine()) {
				sb.append(line);
			}

			instream.close();
			r.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();

	}

	@Override
	protected void onPostExecute(String response) {
		if (response != null) {
			try {
				JSONObject jsonObj = new JSONObject(response);
				String fileContent = jsonObj.getString("fileContent");
				String fineName = enrollmentNo + "_" + hallticketid + ".PDF";
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
				share.putExtra(Intent.EXTRA_TEXT, "Hallticket of " + enrollmentNo);
				share.putExtra(Intent.EXTRA_TEXT, "Company Company ID " + hallticketid);
				share.putExtra(Intent.EXTRA_SUBJECT, "Hallticket of " + enrollmentNo);
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
			} catch (Exception e) {
				e.printStackTrace();
				FBNUtil.hideProgress(parentActivity);
			} catch (Throwable e) {
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