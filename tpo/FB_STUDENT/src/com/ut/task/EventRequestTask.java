package com.ut.task;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ut.fbn.EventActivity;
import com.ut.pojo.HallTicket;
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

public class EventRequestTask extends AsyncTask<String, Void, String> {
	Context context;
	Intent intent;
	String registerdEnrollment;
	public Integer new_events = 0;

	public EventRequestTask(Context context) {
		this.context = context;
	}

	public EventRequestTask(Context context, Intent intent,String registerdEnrollment) {
		this.context = context;
		this.intent = intent;
		this.registerdEnrollment=registerdEnrollment;
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
			response = FBNUtil.callWebService(dato, WSURL.EVENT_WS);
			
			JSONArray arr = new JSONArray(response);
			if (arr != null && arr.length() > 0) {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
				Editor editor = prefs.edit();
				editor.putString(FBNConstants.OPENINGS, arr.toString());
				editor.commit();
			}
			DBController controller = new DBController(context);

			for (int i = 0; i < arr.length(); i++) {
				HallTicket hallTicket = FBNUtil.convertEventRequest(arr.getJSONObject(i));
				Boolean bool = controller.isEventRead(hallTicket);
				if (!bool) {
					new_events++;
				}
			}

		}catch (Exception e) {
			e.printStackTrace();
		}catch (Throwable e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	protected void onPostExecute(String response) {
		if (response != null) {
			if (new_events > 0) {
				intent = new Intent(context, EventActivity.class);
				FBNUtil.showNotification(context, new_events, intent);
				new_events = 0;
			}
		}
	}
}