package com.ut.fbn;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.ut.fbn.adapter.NoticeListCustomAdapter;
import com.ut.pojo.Notice;
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
public class NoticeActivity extends Parent {

	private Context context;
	private TextView new_notices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noticelistview);
		context = getApplicationContext();
		new_notices = (TextView) findViewById(R.id.new_notices);
		final ListView listview = (ListView) findViewById(R.id.noticeList);
		JSONArray arr = getNoticeList();
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
		NoticeListCustomAdapter adapter = new NoticeListCustomAdapter(noticeList, context, true,this);
		listview.setAdapter(adapter);
		FBNUtil.setListBackground(listview);
		String text = noticeList.size() + " " + new_notices.getText().toString();
		new_notices.setText(text);
	}

	private JSONArray getNoticeList() {
		JSONArray arr = null;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String bloodRequestList = prefs.getString(FBNConstants.NOTICS, null);
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
