package com.ut.task;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ut.fbn.ProfileActivity;
import com.ut.fbn.adapter.EligibleHallTicketCustomAdapter;
import com.ut.pojo.EligibleHallTicket;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;
import com.ut.util.WSURL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class EligibleEventRequestTask extends AsyncTask<String, Void, String> {
	Context context;
	Intent intent;
	String registerdEnrollment;
	ListView eligibleOpenings;
	private Activity parentActivity;
	TextView eligible_Openings;

	public EligibleEventRequestTask(Context context, String registerdEnrollment, ListView eligibleOpenings,
			Activity parentActivity, TextView eligible_Openings) {
		this.context = context;
		this.registerdEnrollment = registerdEnrollment;
		this.eligibleOpenings = eligibleOpenings;
		this.parentActivity = parentActivity;
		this.eligible_Openings = eligible_Openings;
	}

	public EligibleEventRequestTask(Context context, Intent intent) {
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
			dato.put("enrollmentNo", registerdEnrollment);
			response = FBNUtil.callWebService(context,dato, WSURL.ELIGIBLE_EVENT_WS);
			if (response != null) {
				JSONArray arr = new JSONArray(response);
				if (arr != null && arr.length() > 0) {
					FBNUtil.setJSONArraytList(arr, FBNConstants.ELIGIBLE_OPENINGS, context);
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
			JSONArray arr = FBNUtil.getJSONArraytList(FBNConstants.ELIGIBLE_OPENINGS, context);
			List<EligibleHallTicket> eventRequests = null;
			if (arr != null && arr.length() > 0) {
				eventRequests = new ArrayList<EligibleHallTicket>(arr.length());
				try {
					for (int i = 0; i < arr.length(); i++) {
						EligibleHallTicket event = FBNUtil.convertEligibleEventRequest(arr.getJSONObject(i));
						eventRequests.add(event);
					}
				} catch (JSONException e) {
					FBNUtil.hideProgress(parentActivity);
					e.printStackTrace();
				}
				eligible_Openings.setText("You are eligible in " + eventRequests.size() + " openings");
				eligible_Openings.setVisibility(View.VISIBLE);
				EligibleHallTicketCustomAdapter adapter = new EligibleHallTicketCustomAdapter(eventRequests, context,
						parentActivity);
				eligibleOpenings.setAdapter(adapter);
				FBNUtil.setListBackground(eligibleOpenings);
				FBNUtil.hideProgress(parentActivity);
			}else {
					Intent homeIntent = new Intent(context, ProfileActivity.class);
					homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					parentActivity.startActivity(homeIntent);
			}
		}
	}
}