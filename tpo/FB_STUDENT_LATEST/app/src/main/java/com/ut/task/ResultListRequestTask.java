package com.ut.task;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ut.fbn.adapter.ResultListCustomAdapter;
import com.ut.pojo.Result;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;
import com.ut.util.WSURL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ListView;

public class ResultListRequestTask extends AsyncTask<String, Void, String> {
	Context context;
	Intent intent;
	String registerdEnrollment;
	ListView resultListView;
	private Activity parentActivity;

	public ResultListRequestTask(Context context, String registerdEnrollment, ListView resultListView,
			Activity parentActivity) {
		this.context = context;
		this.registerdEnrollment = registerdEnrollment;
		this.resultListView = resultListView;
		this.parentActivity = parentActivity;
	}

	public ResultListRequestTask(Context context, Intent intent) {
		this.context = context;
		this.intent = intent;
	}

	@Override
	protected void onPreExecute() {
		FBNUtil.showProgress(parentActivity);
		super.onPreExecute();

	}

	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... urls) {
		String response = "";
		try {
					JSONObject dato = new JSONObject();
			dato.put("enrollmentNo", registerdEnrollment);
			
			response = FBNUtil.callWebService(context,dato, WSURL.RESULT_LIST_WS);
			JSONArray arr = new JSONArray(response);
			if (arr != null && arr.length() > 0) {
				FBNUtil.setJSONArraytList(arr, FBNConstants.RESULT_LIST, context);
			}

		} catch (Exception e) {
			e.printStackTrace();
			FBNUtil.hideProgress(parentActivity);
		}catch (Throwable e) {
			e.printStackTrace();
			FBNUtil.hideProgress(parentActivity);
		}
		return response;
	}

	@Override
	protected void onPostExecute(String response) {
		if (response != null) {
			JSONArray arr = FBNUtil.getJSONArraytList(FBNConstants.RESULT_LIST, context);
			List<Result> resultList = null;
			if (arr != null && arr.length() > 0) {
				resultList = new ArrayList<Result>(arr.length());
				try {
					for (int i = 0; i < arr.length(); i++) {
						Result result = FBNUtil.convertResultRequest(arr.getJSONObject(i));
						resultList.add(result);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			ResultListCustomAdapter adapter = new ResultListCustomAdapter(resultList, context, parentActivity);
			resultListView.setAdapter(adapter);
			FBNUtil.setListBackground(resultListView);
		} else {
			FBNUtil.showNoticeDetails("Error while getting Result List", "Error", parentActivity);
		}
		FBNUtil.hideProgress(parentActivity);

	}
}