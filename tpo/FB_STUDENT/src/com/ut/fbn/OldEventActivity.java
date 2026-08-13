package com.ut.fbn;

import java.util.List;

import com.ut.fbn.adapter.HallTicketCustomAdapter;
import com.ut.pojo.HallTicket;
import com.ut.util.DBController;
import com.ut.util.FBNUtil;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class OldEventActivity extends Parent {

	private Context context;
	private TextView old_events;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventlistview);
		context = getApplicationContext();
		old_events = (TextView) findViewById(R.id.new_events);
		final DBController controller = new DBController(context);
		List<HallTicket> eventRequestListOld = controller.getOldEventList();

		if (eventRequestListOld.size() > 0) {
			final ListView listviewOld = (ListView) findViewById(R.id.eventRequestList);
			HallTicketCustomAdapter adapterOld = new HallTicketCustomAdapter(eventRequestListOld, context, false,this);
			listviewOld.setAdapter(adapterOld);
			FBNUtil.setListBackground(listviewOld);
		}
		String text = eventRequestListOld.size() + " " + old_events.getText().toString();
		old_events.setText(text);

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
