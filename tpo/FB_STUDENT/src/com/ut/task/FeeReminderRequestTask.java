package com.ut.task;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ut.fbn.FeeActivity;
import com.ut.pojo.FeeReminder;
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

public class FeeReminderRequestTask extends AsyncTask<String, Void, String> {
	Context context;
	Intent intent;
	String registerdEnrollment;

	public Integer reminder_List = 0;

	public FeeReminderRequestTask(Context context) {
		this.context = context;
	}

	public FeeReminderRequestTask(Context context, Intent intent, String registerdEnrollment) {
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
			response = FBNUtil.callWebService(dato, WSURL.FEE_REMINDER_LIST);
			JSONArray arr = new JSONArray(response);
			if (arr != null && arr.length() > 0) {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
				Editor editor = prefs.edit();
				editor.putString(FBNConstants.FEE_REMINDER, arr.toString());
				editor.commit();
			}
			DBController controller = new DBController(context);

			for (int i = 0; i < arr.length(); i++) {
				FeeReminder feeReminder = FBNUtil.convertFeeRequest(arr.getJSONObject(i));
				Boolean bool = controller.isFeeReaded(feeReminder);
				if (!bool) {
					reminder_List++;
				}
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		}catch (Throwable e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	protected void onPostExecute(String response) {
		if (response != null) {
			if (reminder_List > 0) {
				intent = new Intent(context, FeeActivity.class);
				FBNUtil.showFeeNotification(context, reminder_List, intent);
				reminder_List = 0;
			}
		}
	}
}