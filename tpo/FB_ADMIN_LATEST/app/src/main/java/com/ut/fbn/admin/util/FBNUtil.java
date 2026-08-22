package com.ut.fbn.admin.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Properties;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ut.fbn.admin.R;
import com.google.gson.JsonParser;
import com.lowagie.text.pdf.codec.Base64;
import com.ut.fbn.admin.ErrorActivity;
import com.ut.fbn.admin.MainActivity;
import com.ut.fbn.admin.pojo.Notice;
import com.ut.fbn.admin.receiver.NoticeReceiver;
import com.ut.pojo.Company;
import com.ut.pojo.HallTicket;
import com.ut.pojo.Student;

import android.app.Activity;
import android.app.AlarmManager;
import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FBNUtil {

	
	public static final String MY_PREFS_NAME = "Fresher Buddy Admin";
	
	public static final String S = "XMzDdG4D03CKm2IxIWQw7g==";

	public static Boolean checkInternet(Context context, Class cls) {
		if (!haveNetworkConnection(context)) {
			// Prepare intent which is triggered if the
			// notification is selected

			Intent intent = new Intent(context, cls);
			PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

			String CHANNEL_ID = "my_channel_01";// The id of the channel.
			NotificationChannel mChannel = createChannel(CHANNEL_ID);
			// Create a notification and set the notification channel.
			Notification notification = new Notification.Builder(context, CHANNEL_ID).setTicker("Alert")
					.setContentTitle(context.getString(R.string.Internet_is_not_available))
					.setContentText(context.getString(R.string.Internet_is_not_available))
					.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent).build();
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				mNotificationManager.createNotificationChannel(mChannel);
			}
			// Hide the notification after its selected
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.defaults |= Notification.DEFAULT_SOUND;

			mNotificationManager.notify(0, notification);
			Toast.makeText(context, context.getString(R.string.Internet_is_not_available_full), Toast.LENGTH_LONG)
					.show();
			return false;
		} else {
			return true;
		}
	}

	
	
	private static boolean haveNetworkConnection(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
				Network network = connectivityManager.getActiveNetwork();
				if (network != null) {
					NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
					return capabilities != null && (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
							|| capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED));
				}
			} else {
				NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
				return activeNetworkInfo != null && activeNetworkInfo.isConnected();
			}
		}
		return false;
	}

	
	public static Notice convertNoticeRequest(JSONObject obj) throws JSONException {
		Notice notice = new Notice();
		notice.setNoticeName(stripHtml(obj.getString("noticeName")));
		notice.setNotice(obj.getString("notice"));
		notice.setCreatedBy(stripHtml(obj.getString("createdBy")));
		notice.setExpiryDate(stripHtml(obj.getString("expiryDate")));
		notice.setExpired(obj.getString("expired"));
		notice.setStudentSpecific(obj.getBoolean("studentSpecific"));
		notice.setImpTag(obj.getBoolean("impTag"));
		if (obj.getString("fileName1") != null)
			notice.setFileName1(obj.getString("fileName1"));
		else
			notice.setFileName1(null);

		if (obj.getString("fileName2") != null)
			notice.setFileName2(obj.getString("fileName2"));
		else
			notice.setFileName2(null);

		if (obj.getString("fileName3") != null)
			notice.setFileName3(obj.getString("fileName3"));
		else
			notice.setFileName3(null);

		if (obj.getString("fileName4") != null)
			notice.setFileName4(obj.getString("fileName4"));
		else
			notice.setFileName4(null);

		if (obj.getString("fileName5") != null)
			notice.setFileName5(obj.getString("fileName5"));
		else
			notice.setFileName5(null);

		if (obj.getString("file1") != null)
			notice.setFile1(obj.getString("file1"));
		else
			notice.setFile1(null);

		if (obj.getString("file2") != null)
			notice.setFile2(obj.getString("file2"));
		else
			notice.setFile2(null);

		if (obj.getString("file4") != null)
			notice.setFile3(obj.getString("file3"));
		else
			notice.setFile3(null);

		if (obj.getString("file4") != null)
			notice.setFile4(obj.getString("file4"));
		else
			notice.setFile4(null);

		if (obj.getString("file5") != null)
			notice.setFile5(obj.getString("file5"));
		else
			notice.setFile5(null);
		return notice;
	}

	
	public static String getProperty(String key, Context context) {
		Properties properties = new Properties();
		AssetManager assetManager = context.getAssets();
		try {
			InputStream inputStream = assetManager.open("config.properties");
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties.getProperty(key);
	}

	public static JSONObject getJsonObject(String jsonString) {
		JSONObject jsonObject;
		try {
			JsonParser parser = new JsonParser();
			Object obj = parser.parse(jsonString);
			jsonObject = (JSONObject) obj;
			return jsonObject;
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	public static Dialog showErrorDialog(String message, Context context) {
		// custom dialog
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog);
		dialog.setTitle("Please correct error...");

		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.text);
		text.setText(message);
		ImageView image = (ImageView) dialog.findViewById(R.id.image);
		image.setImageResource(R.drawable.alert);

		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
		return dialog;
	}

	public static Dialog showSucessDialog(String message, Context context) {
		// custom dialog
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog1);
		dialog.setTitle("Congratulation.....");

		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.sucessText);
		text.setText(message);
		ImageView image = (ImageView) dialog.findViewById(R.id.sucessImage);
		image.setImageResource(R.drawable.success_icon);

		Button dialogButton = (Button) dialog.findViewById(R.id.sucessDialogButtonOK);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
		return dialog;
	}

	public static void showNoticeDetails(String message, String title, Context context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		// set title
		alertDialogBuilder.setTitle(title);
		TextView msg = new TextView(context);
		msg.setText(Html.fromHtml("" + message + ""));
		// set dialog message
		alertDialogBuilder.setView(msg).setCancelable(false).setPositiveButton("Close",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity

					}
				});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// Set title divider color
		int titleDividerId = context.getResources().getIdentifier("titleDivider", "id", "android");
		View titleDivider = alertDialog.findViewById(titleDividerId);
		if (titleDivider != null)
			titleDivider.setBackgroundColor(context.getResources().getColor(android.R.color.holo_purple));
		// show it
		alertDialog.show();
	}

	public static void showWSErrorDetails(Context context) {
		try {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			// set title
			alertDialogBuilder.setTitle("WS ERROR");
			TextView msg = new TextView(context);
			msg.setText(Html.fromHtml(
					"<font color='red'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Service not available</font>"));
			// set dialog message
			alertDialogBuilder.setView(msg).setCancelable(false).setPositiveButton("Close",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// if this button is clicked, close
							// current activity

						}
					});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// Set title divider color
			int titleDividerId = context.getResources().getIdentifier("titleDivider", "id", "android");
			View titleDivider = alertDialog.findViewById(titleDividerId);
			if (titleDivider != null)
				titleDivider.setBackgroundColor(context.getResources().getColor(android.R.color.holo_purple));
			// show it
			alertDialog.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void showInternetErrorDetails(Context context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		// set title
		alertDialogBuilder.setTitle("Internet ERROR");
		TextView msg = new TextView(context);
		msg.setText(
				Html.fromHtml("<font color='red'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Internet not available</font>"));
		// set dialog message
		alertDialogBuilder.setView(msg).setCancelable(false).setPositiveButton("Close",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity

					}
				});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// Set title divider color
		int titleDividerId = context.getResources().getIdentifier("titleDivider", "id", "android");
		View titleDivider = alertDialog.findViewById(titleDividerId);
		if (titleDivider != null)
			titleDivider.setBackgroundColor(context.getResources().getColor(android.R.color.holo_purple));
		// show it
		alertDialog.show();
	}

	public static String stripHtml(String html) {
		return Html.fromHtml(html).toString().trim();
		// return Html.escapeHtml(html);
	}

	public static String getRegisteredUderName(Context context) {
		DBController controller = new DBController(context);
		String user[] = controller.selectUserInfo();
		if (user != null && user.length > 0) {
			return user[0];
		}
		return null;
	}

	public static void clearAll(Context context) {
		DBController controller = new DBController(context);
		controller.clearAll();
	}

	public static Integer getReadExternalPermission(Context context, Activity activity) {
		final int READ_EXTERNAL_STORAGE = 0;
		if (ContextCompat.checkSelfPermission(context,
				android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
					android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
				Toast.makeText(context, R.string.Need_Permission, Toast.LENGTH_SHORT).show();
				// Show an explanation to the user *asynchronously* --
			} else {

				// No explanation needed, we can request the permission.

				ActivityCompat.requestPermissions(activity,
						new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE }, READ_EXTERNAL_STORAGE);
			}
		}
		return READ_EXTERNAL_STORAGE;
	}

	public static Integer getSendSms(Context context, Activity activity) {
		final int SEND_SMS = 0;
		if (ContextCompat.checkSelfPermission(context,
				android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.SEND_SMS)) {
				Toast.makeText(context, R.string.Need_Permission, Toast.LENGTH_SHORT).show();
				// Show an explanation to the user *asynchronously* --
			} else {

				// No explanation needed, we can request the permission.

				ActivityCompat.requestPermissions(activity, new String[] { android.Manifest.permission.SEND_SMS },
						SEND_SMS);
			}
		}
		return SEND_SMS;
	}

	public static Integer getReceiveSms(Context context, Activity activity) {
		final int RECEIVE_SMS = 0;
		if (ContextCompat.checkSelfPermission(context,
				android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
					android.Manifest.permission.RECEIVE_SMS)) {
				Toast.makeText(context, R.string.Need_Permission, Toast.LENGTH_SHORT).show();
				// Show an explanation to the user *asynchronously* --
			} else {

				// No explanation needed, we can request the permission.

				ActivityCompat.requestPermissions(activity, new String[] { android.Manifest.permission.RECEIVE_SMS },
						RECEIVE_SMS);
			}
		}
		return RECEIVE_SMS;
	}

	public static Integer getReadPhoneStatePermission(Context context, Activity activity) {

		final int READ_PHONE_STATE = 0;
		if (ContextCompat.checkSelfPermission(context,
				android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
					android.Manifest.permission.READ_PHONE_STATE)) {
				Toast.makeText(activity, R.string.Need_Permission, Toast.LENGTH_SHORT).show();
				// Show an explanation to the user *asynchronously* --
			} else {

				// No explanation needed, we can request the permission.

				ActivityCompat.requestPermissions(activity,
						new String[] { android.Manifest.permission.READ_PHONE_STATE }, READ_PHONE_STATE);
			}
		}
		return READ_PHONE_STATE;
	}

	public static Integer getWriteExternalPermission(Context context, Activity activity) {
		final int WRITE_EXTERNAL_STORAGE = 0;
		if (ContextCompat.checkSelfPermission(context,
				android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
					android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				Toast.makeText(activity, R.string.Need_Permission, Toast.LENGTH_SHORT).show();
				// Show an explanation to the user *asynchronously* --
			} else {

				// No explanation needed, we can request the permission.

				ActivityCompat.requestPermissions(activity,
						new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, WRITE_EXTERNAL_STORAGE);
			}
		}
		return WRITE_EXTERNAL_STORAGE;
	}

	private static NotificationChannel createChannel(String CHANNEL_ID) {
		CharSequence name = "Notification";// The user-visible name of the channel.
		int importance = NotificationManager.IMPORTANCE_HIGH;
		NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
		mChannel.enableVibration(true);
		mChannel.enableLights(true);
		mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
				new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
						.setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build());
		return mChannel;
	}

	public static boolean isUserLoggedIn(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(FBNConstants.MY_PREFS_NAME, Context.MODE_PRIVATE);
		if (sharedPreferences != null) {
			String userName = sharedPreferences.getString(FBNConstants.USERNAME, null);
			if (userName != null) {
				return true;
			}
		}
		return false;
	}
	
	static public boolean isServerReachable(Context context, String url) {
		return true;
	}

	
	public static void scheduleNoticeRecever(Context context, Context baseContext) {
		Intent intentAlarm = new Intent(context, NoticeReceiver.class);
		setReceiver(context, baseContext, intentAlarm);

	}

	
	private static void setReceiver(Context context, Context baseContext, Intent intentAlarm) {
		boolean alarmUp = (PendingIntent.getBroadcast(context, 0, intentAlarm,
				PendingIntent.FLAG_UPDATE_CURRENT) != null);
		if (alarmUp) {
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intentAlarm,
					PendingIntent.FLAG_UPDATE_CURRENT);
			alarmManager.cancel(pendingIntent);
		}

		PendingIntent pendingIntent = PendingIntent.getBroadcast(baseContext, 1, intentAlarm,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
				FBNConstants.NOTIFICATION_TIME_IN_MINUTE * 60 * 60, pendingIntent);
	}

	public static JSONArray getJSONArraytList(String param, Context context) {
		JSONArray arr = null;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String str = prefs.getString(param, null);
		if (str != null) {
			try {
				arr = new JSONArray(str);
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
		return arr;
	}

	public static void setJSONArraytList(JSONArray arr, String param, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString(param, arr.toString());
		editor.commit();
	}

	public static void setText(String text, String param, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString(param, text);
		editor.commit();
	}
	
	public static void setInt(int text, String param, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putInt(param, text);
		editor.commit();
	}
	
	public static int getInt(String param, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getInt(param, 0);
	}


	public static String getText(String param, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(param, null);
	}

	public static void showProgress(Activity activity) {
		ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.progressbar);
		if (progressBar != null) {
			/*
			 * activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
			 * WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
			 */
			progressBar.setVisibility(View.VISIBLE);
		}

	}

	public static void hideProgress(Activity activity) {
		ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.progressbar);
		if (progressBar != null) {
			/*
			 * activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
			 * );
			 */
			progressBar.setVisibility(View.GONE);
		}

	}

	public static void sendSMS(Context context, String message, String toNo) {
		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

		// Get the SmsManager instance and call the sendTextMessage method to send
		// message
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(toNo, null, message, null, null);
	}

	public static Integer get6DigitRandomNumber() {
		Random ra = new Random();
		Integer rendonNumber = ra.nextInt(1000000);
		while (rendonNumber <= 100000) {
			rendonNumber = ra.nextInt(1000000);
		}
		return Math.abs(rendonNumber);
	}
	
	public static void setListBackground(ListView listview) {
		listview.setBackgroundColor(FBNConstants.LIST_BACKGROUND_COLOR);
		int[] colors = { 0, 0xFFFF0000, 0 }; // red for the example
		listview.setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
		listview.setDividerHeight(5);
	}
	
	public void showProcessDialog(Activity activity) {

	    int llPadding = 30;
	    LinearLayout ll = new LinearLayout(activity);
	    ll.setOrientation(LinearLayout.HORIZONTAL);
	    ll.setPadding(llPadding, llPadding, llPadding, llPadding);
	    ll.setGravity(Gravity.CENTER);
	    LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
	            LinearLayout.LayoutParams.WRAP_CONTENT,
	            LinearLayout.LayoutParams.WRAP_CONTENT);
	    llParam.gravity = Gravity.CENTER;
	    ll.setLayoutParams(llParam);

	    ProgressBar progressBar = new ProgressBar(activity);
	    progressBar.setIndeterminate(true);
	    progressBar.setPadding(0, 0, llPadding, 0);
	    progressBar.setLayoutParams(llParam);

	    llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
	            ViewGroup.LayoutParams.WRAP_CONTENT);
	    llParam.gravity = Gravity.CENTER;
	    TextView tvText = new TextView(activity);
	    tvText.setText("Loading ...");
	    tvText.setTextColor(Color.parseColor("#000000"));
	    tvText.setTextSize(20);
	    tvText.setLayoutParams(llParam);

	    ll.addView(progressBar);
	    ll.addView(tvText);

	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setCancelable(true);
	    builder.setView(ll);

	    AlertDialog dialog = builder.create();
	    dialog.show();
	    Window window = dialog.getWindow();
	    if (window != null) {
	        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
	        layoutParams.copyFrom(dialog.getWindow().getAttributes());
	        layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
	        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
	        dialog.getWindow().setAttributes(layoutParams);
	    }
	}
	
	public static void setLogin(String userName,Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(FBNConstants.MY_PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(FBNConstants.USERNAME, userName);
		editor.commit();

	}
	
	public static String callWebService(Context context,JSONObject dato, String wsUrl){
		String response = null;
		try {
			URL url = new URL(wsUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			if (con != null) {
				con.setDoOutput(true);
				con.setDoInput(true);
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestProperty("Accept", "application/json");
				con.setRequestMethod("POST");
				con.setConnectTimeout(10000);
				OutputStream os = con.getOutputStream();
				byte[] input = dato.toString().getBytes("utf-8");
				os.write(input, 0, input.length);

				if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String responseLine = null;
					StringBuilder responseB = new StringBuilder();
					while ((responseLine = br.readLine()) != null) {
						responseB.append(responseLine.trim());
					}
					response = responseB.toString();
				}
			}
		} catch (ConnectException e) {
			navigateToErrorActivity(context);
		}
		catch (SocketTimeoutException e) {
			navigateToErrorActivity(context);
		}catch (Exception e) {
			navigateToErrorActivity(context);
		}
		return response;
	}
	
	public static void navigateToErrorActivity(Context context) {
		Intent homeIntent = new Intent(context, ErrorActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(homeIntent);
	}
	
	public static Student convertEligibleEventRequest(JSONObject obj) throws JSONException {
		Student eligibleHallticket = new Student();
		JSONObject hallticket = obj.getJSONObject("hallTicket");
		eligibleHallticket.setHallTicket(convertEventRequest(hallticket));
		eligibleHallticket.setIsApplied(obj.getBoolean("isApplied"));
		eligibleHallticket.setIsApproved(obj.getBoolean("isApproved"));
		eligibleHallticket.setAppliedOn(obj.getString("appliedOn"));
		eligibleHallticket.setApprovedOn(obj.getString("approvedOn"));
		return eligibleHallticket;
	}

	public static HallTicket convertEventRequest(JSONObject obj){
		HallTicket hallTicket = new HallTicket();
		try {
			Integer hallTicketId = obj.getInt("hallTicketId");
			hallTicket.setHallTicketId(hallTicketId);
			String companyName = stripHtml(obj.getString("companyName"));
			hallTicket.setCompanyName(companyName);
			String date = obj.getString("date");
			hallTicket.setDate(date);
			String time = stripHtml(obj.getString("time"));
			hallTicket.setTime(time);
			String packageOffering = stripHtml(obj.getString("packageOffering"));
			hallTicket.setPackageOffering(packageOffering);
			hallTicket.setUserName(obj.getString("userName"));
			hallTicket.setLastDateToApply(obj.getString("lastDateToApply"));
			hallTicket.setCriteria(obj.getString("criteria"));
			hallTicket.setInterviewLocation(obj.getString("interviewLocation"));
			hallTicket.setPostingLocation(obj.getString("postingLocation"));
			hallTicket.setRole(obj.getString("role"));

			// Setting Compnay
			JSONObject companyObj = obj.getJSONObject("company");
			Company company = new Company();
			company.setCompanyID(companyObj.getInt("companyID"));
			company.setCompanyname(companyObj.getString("companyname"));
			company.setProfile(companyObj.getString("profile"));
			company.setLogo(Base64.decode(companyObj.getString("logo")));
			company.setDomain(companyObj.getString("domain"));
			company.setWebsite(companyObj.getString("website"));
			company.setLinkedIn(companyObj.getString("linkedIn"));
			company.setTwiter(companyObj.getString("twiter"));
			company.setGlassdoor(companyObj.getString("glassdoor"));
			company.setFacebook(companyObj.getString("facebook"));
			company.setEmail(companyObj.getString("email"));
			company.setRemarks(companyObj.getString("remarks"));
			hallTicket.setCompany(company);
			return hallTicket;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return hallTicket;
	}
	public static Company convertToCompany(JSONObject companyObj){
		Company company = new Company();
		try {
			company.setCompanyID(companyObj.getInt("companyID"));
			company.setCompanyname(companyObj.getString("companyname"));
			company.setProfile(companyObj.getString("profile"));
			company.setLogo(Base64.decode(companyObj.getString("logo")));
			company.setDomain(companyObj.getString("domain"));
			company.setWebsite(companyObj.getString("website"));
			company.setLinkedIn(companyObj.getString("linkedIn"));
			company.setTwiter(companyObj.getString("twiter"));
			company.setGlassdoor(companyObj.getString("glassdoor"));
			company.setFacebook(companyObj.getString("facebook"));
			company.setEmail(companyObj.getString("email"));
			company.setRemarks(companyObj.getString("remarks"));
			return company;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return company;
	}
	
	public static HallTicket convertToHallTicket(JSONObject obj){
		HallTicket hallTicket = new HallTicket();
		try {
			Integer hallTicketId = obj.getInt("hallTicketId");
			hallTicket.setHallTicketId(hallTicketId);
			String companyName = stripHtml(obj.getString("companyName"));
			hallTicket.setCompanyName(companyName);
			String date = obj.getString("date");
			hallTicket.setDate(date);
			String time = stripHtml(obj.getString("time"));
			hallTicket.setTime(time);
			String packageOffering = stripHtml(obj.getString("packageOffering"));
			hallTicket.setPackageOffering(packageOffering);
			hallTicket.setUserName(obj.getString("userName"));
			hallTicket.setLastDateToApply(obj.getString("lastDateToApply"));
			hallTicket.setCriteria("<br/>" + obj.getString("criteria"));
			hallTicket.setInterviewLocation(obj.getString("interviewLocation"));
			hallTicket.setPostingLocation(obj.getString("postingLocation"));
			hallTicket.setRole(obj.getString("role"));
			hallTicket.setTotalApplied(obj.getString("totalApplied"));
			hallTicket.setTotalApproved(obj.getString("totalApproved"));
			hallTicket.setTotalArrived(obj.getString("totalArrived"));
			hallTicket.setTotalShortlisted(obj.getString("totalShortlisted"));
			return hallTicket;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return hallTicket;
	}
	
	public static Student convertToStudent(JSONObject obj){
		Student student = new Student();
		try {
			String rollNumber = obj.getString("rollNumber");
			student.setRollnumber(rollNumber);
			student.setIsApplied(obj.getBoolean("isApplied"));
			student.setIsApproved(obj.getBoolean("isApproved"));
			student.setAppliedOn(obj.getString("appliedOn"));
			student.setApprovedOn(obj.getString("approvedOn"));
			return student;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return student;
	}
	
	public static void setCompanyDetail(View view, HallTicket hallTicket) {
		TextView role = (TextView) view.findViewById(R.id.role);
		role.setText(hallTicket.getRole());

		TextView companyName = (TextView) view.findViewById(R.id.companyName);
		companyName.setText(hallTicket.getCompanyName());

		TextView domain = (TextView) view.findViewById(R.id.domain);
		domain.setText(hallTicket.getCompany().getDomain());

		TextView packageOffered = (TextView) view.findViewById(R.id.packageOffered);
		packageOffered.setText(hallTicket.getPackageOffering());

		TextView lastDate = (TextView) view.findViewById(R.id.lastDate);
		lastDate.setText("Last Date to Apply : " + hallTicket.getLastDateToApply());

		ImageView companyLogo = (ImageView) view.findViewById(R.id.companyLogo);
		Bitmap bitmap = BitmapFactory.decodeByteArray(hallTicket.getCompany().getLogo(), 0,
				hallTicket.getCompany().getLogo().length);
		companyLogo.setImageBitmap(bitmap);
		companyLogo.setImageBitmap(bitmap);
	}

	
}
