package com.ut.task;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ut.fbn.NoticeActivity;
import com.ut.pojo.Notice;
import com.ut.util.DBController;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;
import com.ut.util.WSURL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class NoticRequestTask extends AsyncTask<String, Void, String> {
	Context context;
	Intent intent;
	String registerdEnrollment;

	public Integer notice_events = 0;

	public NoticRequestTask(Context context) {
		this.context = context;
	}

	public NoticRequestTask(Context context, Intent intent, String registerdEnrollment) {
		this.context = context;
		this.intent = intent;
		this.registerdEnrollment = registerdEnrollment;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... urls) {
		String response = "";
		try {
			JSONObject dato = new JSONObject();
			dato.put("enrollmentNo", registerdEnrollment);
			dato.put("studentSpecific", false);
			response = FBNUtil.callWebService(dato, WSURL.NOTIC_WS);
			JSONArray arr = new JSONArray(response);
			if (arr != null && arr.length() > 0) {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
				Editor editor = prefs.edit();
				editor.putString(FBNConstants.NOTICS, arr.toString());
				editor.commit();
			}
			DBController controller = new DBController(context);

			for (int i = 0; i < arr.length(); i++) {
				Notice notice = FBNUtil.convertNoticeRequest(arr.getJSONObject(i));
				Boolean bool = controller.isNoticeRead(notice);
				if (!bool) {
					notice_events++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	protected void onPostExecute(String response) {
		if (response != null) {
			if (notice_events > 0) {
				intent = new Intent(context, NoticeActivity.class);
				FBNUtil.showNoticeNotification(context, notice_events, intent);
				notice_events = 0;
			}
		}
	}
}