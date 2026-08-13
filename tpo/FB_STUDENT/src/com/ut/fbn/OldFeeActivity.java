package com.ut.fbn;

import java.util.List;

import com.ut.fbn.adapter.FeeListCustomAdapter;
import com.ut.pojo.FeeReminder;
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
public class OldFeeActivity extends Parent {

	private Context context;
	private TextView feeNotification;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feelistview);
		context = getApplicationContext();
		feeNotification = (TextView) findViewById(R.id.feeNotification);
		final DBController controller = new DBController(context);
		final List<FeeReminder> feeListOld = controller.getOldFeeList();
		final ListView listviewOld = (ListView) findViewById(R.id.feeList);
		
		
		if (feeListOld.size() > 0) {
			FeeListCustomAdapter adapterOld = new FeeListCustomAdapter(feeListOld, context, false,this);
			listviewOld.setAdapter(adapterOld);
			FBNUtil.setListBackground(listviewOld);
		}
		String text = feeListOld.size() + " Fee reminder found.";
		feeNotification.setText(text);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		if (FBNUtil.isUserLoggedIn(context)) {
			allLoggedInMenuItems(menu);
		} else {
			allMenuItems(menu, false, false);
		}
		this.menu = menu;
		return true;
	}

}
