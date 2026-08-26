package com.ut.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.ut.fbn.MainActivity;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;
import com.ut.util.WSURL;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DownloadProfileTask extends AsyncTask<String, Void, String> {
	String enrollmentNo;
	Context context;
	ImageView image, verifiedImage, qrCodeImage;
	private Activity parentActivity;
	TextView textView;
	TextView mobileView;
	Button verifyMobileNO;
	TextView emailAddress;
	ImageView verifiedEmailImage;

	public DownloadProfileTask(TextView textView, TextView mobileView, Button verifyMobileNO, TextView emailAddress,
			ImageView image, String enrollmetNo, ImageView verifiedImage, ImageView qrCodeImage,
			ImageView verifiedEmailImage, Context contex, Activity parentActivity) {
		this.enrollmentNo = enrollmetNo;
		this.context = contex;
		this.image = image;
		this.parentActivity = parentActivity;
		this.textView = textView;
		this.mobileView = mobileView;
		this.verifyMobileNO = verifyMobileNO;
		this.emailAddress = emailAddress;
		this.verifiedImage = verifiedImage;
		this.verifiedEmailImage = verifiedEmailImage;
		this.qrCodeImage = qrCodeImage;
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
			response = FBNUtil.callWebService(context, dato, WSURL.DOWNLOAD_IMAGE_WS);
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
				if ("B".equals(jsonObj.getString("accuoutStatus"))) {
					FBNUtil.clearAll(context);
					context.getSharedPreferences(FBNUtil.MY_PREFS_NAME, parentActivity.MODE_PRIVATE).edit().clear().commit();
					FBNUtil.showErrorDialog("Your account is Blocked.Please contact admin!", parentActivity);
				} else {
					String fileContent = jsonObj.getString("fileContent");
					String qrCodeImageStr = jsonObj.getString("qrCodeImage");
					textView.setText(null);
					textView.setText("Enrollment NO :" + enrollmentNo);
					if (jsonObj.getString("name") != null) {
						textView.setText(textView.getText() + "\nName:" + jsonObj.getString("name"));
					}
					if (jsonObj.getString("status") != null) {
						textView.setText(textView.getText() + "\nStatus:" + jsonObj.getString("status"));
					}

					if (jsonObj.getString("selectedIn") != null) {
						textView.setText(textView.getText() + "\nSelected In:" + jsonObj.getString("selectedIn"));
					}
					if (jsonObj.getString("backlist") != null) {
						textView.setText(textView.getText() + "\nBack listed:" + jsonObj.getString("backlist"));
					}

					FBNUtil.setText(textView.getText().toString(), FBNConstants.PROFILE_TEXT, context);

					if (jsonObj.getString("email") != null) {
						emailAddress.setText("E-mail:" + jsonObj.getString("email"));
						FBNUtil.setText(emailAddress.getText().toString(), FBNConstants.EMAIL_ADDRESS, context);
					}
					if (jsonObj.getString("mobile") != null) {
						mobileView.setText("Mobile:" + jsonObj.getString("mobile"));
						FBNUtil.setText(mobileView.getText().toString(), FBNConstants.MOBILE_NO, context);
					}

					if (jsonObj.getString("emailVerified") != null) {
						String str = jsonObj.getString("emailVerified");
						if ("YES".equals(str)) {
							verifiedEmailImage.setVisibility(View.VISIBLE);
						} else {
							verifiedEmailImage.setVisibility(View.INVISIBLE);
						}
						FBNUtil.setText(str, FBNConstants.EMAIL_VERIFIED, context);
					}

					if (jsonObj.getString("mobileVerified") != null) {
						String str = jsonObj.getString("mobileVerified");

						if ("YES".equals(str)) {
							verifiedImage.setVisibility(View.VISIBLE);
							verifyMobileNO.setVisibility(View.INVISIBLE);
						} else {
							verifyMobileNO.setVisibility(View.VISIBLE);
							verifiedImage.setVisibility(View.INVISIBLE);
						}
						FBNUtil.setText(str, FBNConstants.MOBILE_VERIFIED, context);
					}
					String fineName = enrollmentNo + ".jpg";
					String qrFineName = enrollmentNo + ".png";
					File mydir = new File(FBNConstants.FBN_DOWNLOAD_DIR); // Creating an internal dir;
					File file = null;
					File qrFile = null;
					if (!mydir.exists()) {
						mydir.mkdirs();
						file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fineName);
						qrFile = new File(FBNConstants.FBN_DOWNLOAD_DIR + qrFineName);
					} else {
						file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fineName);
						qrFile = new File(FBNConstants.FBN_DOWNLOAD_DIR + qrFineName);
					}
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(Base64.decode(fileContent, Base64.NO_WRAP));
					fos.close();

					fos = new FileOutputStream(qrFile);
					fos.write(Base64.decode(qrCodeImageStr, Base64.NO_WRAP));
					fos.close();

					Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
					image.setImageURI(uri);
					Uri qrUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", qrFile);
					qrCodeImage.setImageURI(qrUri);
					qrCodeImage.setVisibility(View.VISIBLE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				FBNUtil.hideProgress(parentActivity);
			} catch (IOException e) {
				e.printStackTrace();
				FBNUtil.hideProgress(parentActivity);
			}
		}
	}

	
	@NonNull
	public Uri saveBitmap(@NonNull final Context context, @NonNull final Bitmap bitmap,
	                      @NonNull final Bitmap.CompressFormat format,
	                      @NonNull final String mimeType,
	                      @NonNull final String displayName) throws IOException {

	    final ContentValues values = new ContentValues();
	    values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
	    values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
	    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);

	    final ContentResolver resolver = context.getContentResolver();
	    Uri uri = null;

	    try {
	        final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	        uri = resolver.insert(contentUri, values);

	        if (uri == null)
	            throw new IOException("Failed to create new MediaStore record.");
     	final OutputStream stream = resolver.openOutputStream(uri);
	            if (stream == null)
	                throw new IOException("Failed to open output stream.");
	         
	            if (!bitmap.compress(format, 95, stream))
	                throw new IOException("Failed to save bitmap.");
	        return uri;
	    }
	    catch (IOException e) {

	        if (uri != null) {
	            // Don't leave an orphan entry in the MediaStore
	            resolver.delete(uri, null, null);
	        }

	        throw e;
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