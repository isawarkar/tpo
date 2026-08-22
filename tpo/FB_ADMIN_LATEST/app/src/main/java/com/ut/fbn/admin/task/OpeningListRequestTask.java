package com.ut.fbn.admin.task;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ut.adapter.OpeningListCustomAdapter;
import com.ut.fbn.admin.MainActivity;
import com.ut.fbn.admin.util.FBNConstants;
import com.ut.fbn.admin.util.FBNUtil;
import com.ut.fbn.admin.util.WSURL;
import com.ut.pojo.HallTicket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class OpeningListRequestTask extends AsyncTask<String, Void, String> {
	Context context;
	Intent intent;
	Integer companyId;
	ListView eligibleOpenings;
	private Activity parentActivity;
	TextView eligible_Openings;

	public OpeningListRequestTask(Context context, Integer companyId, ListView eligibleOpenings,
			Activity parentActivity, TextView eligible_Openings) {
		this.context = context;
		this.companyId = companyId;
		this.eligibleOpenings = eligibleOpenings;
		this.parentActivity = parentActivity;
		this.eligible_Openings = eligible_Openings;
	}

	public OpeningListRequestTask(Context context, Intent intent) {
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
			dato.put("companyID", String.valueOf(companyId));
			response = FBNUtil.callWebService(context,dato, WSURL.OPPENING_LIST_BY_COMPANY_WS);
			if (response != null) {
				JSONArray arr = new JSONArray(response);
				if (arr != null && arr.length() > 0) {
					FBNUtil.setJSONArraytList(arr, FBNConstants.OPPENING_LIST_BY_COMPANY_WS, context);
				} else {
					FBNUtil.setJSONArraytList(null, FBNConstants.OPPENING_LIST_BY_COMPANY_WS, context);
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
			JSONArray arr = FBNUtil.getJSONArraytList(FBNConstants.OPPENING_LIST_BY_COMPANY_WS, context);
			List<HallTicket> hallTicketList = null;
			if (arr != null && arr.length() > 0) {
				hallTicketList = new ArrayList<HallTicket>(arr.length());
				try {
					for (int i = 0; i < arr.length(); i++) {
						HallTicket hallTicket = FBNUtil.convertToHallTicket(arr.getJSONObject(i));
						hallTicketList.add(hallTicket);
					}
				} catch (JSONException e) {
					FBNUtil.hideProgress(parentActivity);
					e.printStackTrace();
				}
				eligible_Openings.setText("Total Opening's = " + hallTicketList.size());
				eligible_Openings.setVisibility(View.VISIBLE);
				OpeningListCustomAdapter adapter = new OpeningListCustomAdapter(hallTicketList, context,
						parentActivity);
				eligibleOpenings.setAdapter(adapter);
				FBNUtil.setListBackground(eligibleOpenings);
				FBNUtil.hideProgress(parentActivity);
			}else {
					Intent homeIntent = new Intent(context, MainActivity.class);
					homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					parentActivity.startActivity(homeIntent);
			}
		}else {
			FBNUtil.hideProgress(parentActivity);
			FBNUtil.showErrorDialog("No Data Found!", parentActivity);
		}
	}
}