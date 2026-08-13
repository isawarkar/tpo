package com.ut.fbn.admin.task;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ut.adapter.CompanyCustomAdapter;
import com.ut.fbn.admin.MainActivity;
import com.ut.fbn.admin.util.FBNConstants;
import com.ut.fbn.admin.util.FBNUtil;
import com.ut.fbn.admin.util.WSURL;
import com.ut.pojo.Company;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class CompanyListRequestTask extends AsyncTask<String, Void, String> {
	Context context;
	Intent intent;
	String userName;
	ListView eligibleOpenings;
	private Activity parentActivity;
	TextView eligible_Openings;

	public CompanyListRequestTask(Context context, String userName, ListView eligibleOpenings, Activity parentActivity,
			TextView eligible_Openings) {
		this.context = context;
		this.userName = userName;
		this.eligibleOpenings = eligibleOpenings;
		this.parentActivity = parentActivity;
		this.eligible_Openings = eligible_Openings;
	}

	public CompanyListRequestTask(Context context, Intent intent) {
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
			dato.put("userName", userName);
			response = FBNUtil.callWebService(context, dato, WSURL.COMPANY_LIST_WS);
			if (response != null) {
				JSONArray arr = new JSONArray(response);
				if (arr != null && arr.length() > 0) {
					FBNUtil.setJSONArraytList(arr, FBNConstants.COMPANY_LIST, context);
				} else {
					FBNUtil.setJSONArraytList(null, FBNConstants.COMPANY_LIST, context);
				}
			}
		} catch (Exception e) {
			FBNUtil.hideProgress(parentActivity);
		} catch (Throwable e) {
			FBNUtil.hideProgress(parentActivity);
		}
		return response;
	}

	@Override
	protected void onPostExecute(String response) {
		if (response != null) {
			JSONArray arr = FBNUtil.getJSONArraytList(FBNConstants.COMPANY_LIST, context);
			List<Company> eventRequests = null;
			if (arr != null && arr.length() > 0) {
				eventRequests = new ArrayList<Company>(arr.length());
				try {
					for (int i = 0; i < arr.length(); i++) {
						Company company = FBNUtil.convertToCompany(arr.getJSONObject(i));
						eventRequests.add(company);
					}
				} catch (JSONException e) {
					FBNUtil.hideProgress(parentActivity);
					e.printStackTrace();
				}
				eligible_Openings.setText("Total Company = " + eventRequests.size());
				eligible_Openings.setVisibility(View.VISIBLE);
				CompanyCustomAdapter adapter = new CompanyCustomAdapter(eventRequests, context, parentActivity);
				eligibleOpenings.setAdapter(adapter);
				FBNUtil.setListBackground(eligibleOpenings);
				FBNUtil.hideProgress(parentActivity);
			} else {
				Intent homeIntent = new Intent(context, MainActivity.class);
				homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				parentActivity.startActivity(homeIntent);
			}
		} else {
			FBNUtil.hideProgress(parentActivity);
			FBNUtil.showErrorDialog("No Data Found!", parentActivity);
		}
	}
}