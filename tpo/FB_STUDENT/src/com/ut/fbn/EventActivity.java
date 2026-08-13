package com.ut.fbn;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.ut.fbn.adapter.HallTicketCustomAdapter;
import com.ut.pojo.HallTicket;
import com.ut.util.DBController;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class EventActivity extends Parent {

	private Context context;
	private TextView new_events;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventlistview);
		context = getApplicationContext();
		new_events = (TextView) findViewById(R.id.new_events);
		final ListView listview = (ListView) findViewById(R.id.eventRequestList);
		JSONArray arr = getEventRequestList();
		final List<HallTicket> eventRequests = new ArrayList<HallTicket>(arr.length());
		final DBController controller = new DBController(context);
		try {
			for (int i = 0; i < arr.length(); i++) {
				HallTicket event = FBNUtil.convertEventRequest(arr.getJSONObject(i));
				Boolean bool = controller.isEventRead(event);
				if (!bool) {
					eventRequests.add(event);
				}
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}
		HallTicketCustomAdapter adapter = new HallTicketCustomAdapter(eventRequests, context, true,this);
		listview.setAdapter(adapter);
		FBNUtil.setListBackground(listview);
		String text = eventRequests.size() + " " + new_events.getText().toString();
		new_events.setText(text);
	}

	private JSONArray getEventRequestList() {
		JSONArray arr = null;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String bloodRequestList = prefs.getString(FBNConstants.OPENINGS, null);
		try {
			arr = new JSONArray(bloodRequestList);
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return arr;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		if (FBNUtil.isUserLoggedIn(context)) {
			allLoggedInMenuItems(menu);
		}else {
			allMenuItems(menu, false, false);
		}
		this.menu = menu;
		return true;
	}

}
