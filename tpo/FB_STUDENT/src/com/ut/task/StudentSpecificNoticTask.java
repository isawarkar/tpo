package com.ut.task;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ut.fbn.adapter.NoticeListCustomAdapter;
import com.ut.pojo.Notice;
import com.ut.util.DBController;
import com.ut.util.FBNUtil;
import com.ut.util.WSURL;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.TextView;

public class StudentSpecificNoticTask extends AsyncTask<String, Void, String> {
	Context context;
	String registerdEnrollment;
	ListView listview;
	TextView new_notices;
	Activity activity;

	public Integer notice_events = 0;

	public StudentSpecificNoticTask(Context context) {
		this.context = context;
	}

	public StudentSpecificNoticTask(Context context, String registerdEnrollment, Activity activity, ListView listview,
			TextView new_notices) {
		this.context = context;
		this.registerdEnrollment = registerdEnrollment;
		this.activity = activity;
		this.listview = listview;
		this.new_notices = new_notices;
	}

	@Override
	protected void onPreExecute() {
	
	}

	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... urls) {
		String response = "";
		try {
			JSONObject dato = new JSONObject();
			dato.put("enrollmentNo", registerdEnrollment);
			dato.put("studentSpecific", true);
			response = FBNUtil.callWebService(dato, WSURL.NOTIC_WS);
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
			try {
				JSONArray arr = new JSONArray(response);
				final List<Notice> noticeList = new ArrayList<Notice>(arr.length());
				final DBController controller = new DBController(context);
				try {
					for (int i = 0; i < arr.length(); i++) {
						Notice notice = FBNUtil.convertNoticeRequest(arr.getJSONObject(i));
						Boolean bool = controller.isNoticeRead(notice);
						if (!bool) {
							noticeList.add(notice);
						}
					}
				} catch (JSONException e) {

					e.printStackTrace();
				}
				NoticeListCustomAdapter adapter = new NoticeListCustomAdapter(noticeList, context, true, activity);
				listview.setAdapter(adapter);
				FBNUtil.setListBackground(listview);
				String text = noticeList.size() + " New Notice Found";
				new_notices.setText(text);
				FBNUtil.hideProgress(activity);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}