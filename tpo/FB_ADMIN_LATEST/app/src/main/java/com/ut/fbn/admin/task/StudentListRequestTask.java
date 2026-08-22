package com.ut.fbn.admin.task;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ut.adapter.StudentListCustomAdapter;
import com.ut.fbn.admin.MainActivity;
import com.ut.fbn.admin.util.FBNConstants;
import com.ut.fbn.admin.util.FBNUtil;
import com.ut.fbn.admin.util.WSURL;
import com.ut.pojo.Student;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class StudentListRequestTask extends AsyncTask<String, Void, String> {
	Context context;
	Intent intent;
	Integer hallticketID;
	ListView studentListView;
	private Activity parentActivity;
	TextView studentCount;

	public StudentListRequestTask(Context context, Integer hallticketID, ListView studentList,
			Activity parentActivity, TextView studentCount) {
		this.context = context;
		this.hallticketID = hallticketID;
		this.studentListView = studentList;
		this.parentActivity = parentActivity;
		this.studentCount = studentCount;
	}

	public StudentListRequestTask(Context context, Intent intent) {
		this.context = context;
		this.intent = intent;
	}

	@Override
	protected void onPreExecute() {
		FBNUtil.showProgress(parentActivity);

	}

	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... urls) {
		String response = null;
		try {

			JSONObject dato = new JSONObject();
			dato.put("hallticketId", String.valueOf(hallticketID));
			response = FBNUtil.callWebService(context,dato, WSURL.STUDENT_LIST_BY_COMPANY_WS);
			if (response != null) {
				JSONArray arr = new JSONArray(response);
				if (arr != null && arr.length() > 0) {
					FBNUtil.setJSONArraytList(arr, FBNConstants.STUDENT_LIST_BY_COMPANY_WS, context);
				} else {
					FBNUtil.setJSONArraytList(null, FBNConstants.STUDENT_LIST_BY_COMPANY_WS, context);
				}
			}
		}catch (Exception e) {
			FBNUtil.hideProgress(parentActivity);
		} catch (Throwable e) {
			FBNUtil.hideProgress(parentActivity);
		}
		return response;
	}

	
	@Override
	protected void onPostExecute(String response) {
		if (response != null) {
			JSONArray arr = FBNUtil.getJSONArraytList(FBNConstants.STUDENT_LIST_BY_COMPANY_WS, context);
			List<Student> studentList = null;
			if (arr != null && arr.length() > 0) {
				studentList = new ArrayList<Student>(arr.length());
				try {
					for (int i = 0; i < arr.length(); i++) {
						Student hallTicket = FBNUtil.convertToStudent(arr.getJSONObject(i));
						studentList.add(hallTicket);
					}
				} catch (JSONException e) {
					FBNUtil.hideProgress(parentActivity);
					e.printStackTrace();
				}
				studentCount.setText("Total Student's = " + studentList.size());
				studentCount.setVisibility(View.VISIBLE);
				StudentListCustomAdapter adapter = new StudentListCustomAdapter(studentList, context,
						parentActivity,hallticketID);
				studentListView.setAdapter(adapter);
				FBNUtil.setListBackground(studentListView);
				FBNUtil.hideProgress(parentActivity);
			}else {
					Intent homeIntent = new Intent(context, MainActivity.class);
					homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					parentActivity.startActivity(homeIntent);
			}
		}
	}
}