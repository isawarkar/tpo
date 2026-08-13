package com.ut.fbn.admin.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.ut.fbn.admin.util.FBNConstants;
import com.ut.fbn.admin.util.FBNUtil;
import com.ut.fbn.admin.util.WSURL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class StudentArrivedTask extends AsyncTask<String, Void, String> {
	private String signature;
	private TextView verifiedByServer;
	private Activity parentActivity;
	Context context;

	private TextView totalShotListed;
	private TextView totalApplied;
	private TextView totalApproved;
	private TextView totalArrived;

	private String enrollmentNo;
	private ImageView profileImage;

	public StudentArrivedTask(String enrollmentNo, String signature, Activity parentActivity, TextView verifiedByServer,
			TextView totalShotListed, TextView totalApplied, TextView totalApproved, TextView totalArrived,
			ImageView profileImage, Context context) {
		this.signature = signature;
		this.parentActivity = parentActivity;
		this.verifiedByServer = verifiedByServer;
		this.context = context;
		this.totalShotListed = totalShotListed;
		this.totalApplied = totalApplied;
		this.totalApproved = totalApproved;
		this.totalArrived = totalArrived;
		this.enrollmentNo = enrollmentNo;
		this.profileImage = profileImage;
	}

	protected void onPreExecute() {
		FBNUtil.showProgress(parentActivity);
	}

	@Override
	protected String doInBackground(String... urls) {
		String response = "";
		try {
			JSONObject dato = new JSONObject();
			dato.put("signature", signature);

			response = FBNUtil.callWebService(context, dato, WSURL.STUDENT_ARRIVED_WS);

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
				totalShotListed.setText(jsonObj.getString("totalShotListed"));
				totalApplied.setText(jsonObj.getString("totalApplied"));
				totalApproved.setText(jsonObj.getString("totalApproved"));
				totalArrived.setText(jsonObj.getString("totalArrived"));
				if ("V".equals(status)) {
					verifiedByServer.setText("Successfully Verified!");
					verifiedByServer.setTextColor(Color.GREEN);
					FBNUtil.showSucessDialog("Successfully Verified!", parentActivity);
				} else if ("A".equals(status)) {
					verifiedByServer.setText("Already Verified!");
					verifiedByServer.setTextColor(Color.MAGENTA);
					FBNUtil.showSucessDialog("Already Verified!!", parentActivity);
				} else {
					FBNUtil.showErrorDialog("Error while Verification.Please try agian!", parentActivity);
				}
				String profileImageJSON = jsonObj.getString("profileImage");
				String fineName = enrollmentNo + ".jpg";
				File mydir = new File(FBNConstants.FBN_DOWNLOAD_DIR); // Creating an internal dir;
				File file = null;
				if (!mydir.exists()) {
					mydir.mkdirs();
					file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fineName);
				} else {
					file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fineName);
				}
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(Base64.decode(profileImageJSON, Base64.NO_WRAP));
				fos.close();

				Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
				profileImage.setImageURI(uri);
				profileImage.setVisibility(View.VISIBLE);
				FBNUtil.hideProgress(parentActivity);
			} catch (JSONException e) {
				FBNUtil.hideProgress(parentActivity);
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}