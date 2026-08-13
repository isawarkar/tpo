package com.ut.fbn;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.ut.fbn.adapter.FeeListCustomAdapter;
import com.ut.pojo.FeeReminder;
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
public class FeeActivity extends Parent {

	private Context context;
	private TextView feeNotification;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feelistview);
		context = getApplicationContext();
		feeNotification = (TextView) findViewById(R.id.feeNotification);
		final ListView listview = (ListView) findViewById(R.id.feeList);
		JSONArray arr = getNoticeList();
		final List<FeeReminder> feeList = new ArrayList<FeeReminder>(arr.length());
		final DBController controller = new DBController(context);
		try {
			for (int i = 0; i < arr.length(); i++) {
				FeeReminder feeReminder = FBNUtil.convertFeeRequest(arr.getJSONObject(i));
				Boolean bool = controller.isFeeReaded(feeReminder);
				if (!bool) {
					feeList.add(feeReminder);
				}
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}
		FeeListCustomAdapter adapter = new FeeListCustomAdapter(feeList, context, true,this);
		listview.setAdapter(adapter);
		FBNUtil.setListBackground(listview);
		String text = feeList.size() + " Fee reminder found. ";
		feeNotification.setText(text);
	}

	

	private JSONArray getNoticeList() {
		JSONArray arr = null;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String bloodRequestList = prefs.getString(FBNConstants.FEE_REMINDER, null);
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
