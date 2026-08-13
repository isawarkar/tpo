package com.ut.fbn.admin.task;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.lowagie.text.pdf.codec.Base64;
import com.ut.fbn.admin.MainActivity;
import com.ut.fbn.admin.util.FBNUtil;
import com.ut.fbn.admin.util.WSURL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

public class DownloadProfileImageTask extends AsyncTask<String, Void, String> {
	Context context;
	ImageView profileImage;
	Activity parentActivity;

	public DownloadProfileImageTask(ImageView profileImage, Context context, Activity parentActivity) {
		this.context = context;
		this.parentActivity = parentActivity;
		this.profileImage = profileImage;
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
			dato.put("userName", FBNUtil.getRegisteredUderName(context));
			response = FBNUtil.callWebService(context, dato, WSURL.PROFILE_IMAGE_WS);
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
				String profileImageStr = jsonObj.getString("profileImage");
				byte[] image  = Base64.decode(profileImageStr);
				
				Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0,
						image.length);
				profileImage.setImageBitmap(bitmap);
				profileImage.setVisibility(View.VISIBLE);
				FBNUtil.hideProgress(parentActivity);
			} catch (JSONException e) {
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