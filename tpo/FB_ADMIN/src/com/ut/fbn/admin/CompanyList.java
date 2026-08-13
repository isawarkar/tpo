package com.ut.fbn.admin;

import org.json.JSONException;
import org.json.JSONObject;

import com.ut.fbn.admin.task.CompanyListRequestTask;
import com.ut.fbn.admin.util.DBController;
import com.ut.fbn.admin.util.FBNConstants;
import com.ut.fbn.admin.util.FBNUtil;
import com.ut.fbn.admin.util.WSURL;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class CompanyList extends Parent {

	Context context;
	public Menu menu;
	private String host = null;
	String userName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.companylist);
		context = getApplicationContext();
		FBNUtil.getReadPhoneStatePermission(context, this);
		FBNUtil.getWriteExternalPermission(context, this);
		FBNUtil.getReadExternalPermission(context, this);
		FBNUtil.getSendSms(context, this);

		TextView eligible_Openings = (TextView) findViewById(R.id.Eligible_Openings);

		try {
			if (WSURL.localhost == null) {
				host = FBNUtil.getProperty("device_localhost", context);
				WSURL.setLocalhost(host);
			}

			if (FBNUtil.isUserLoggedIn(context)) {
			
				userName = FBNUtil.getRegisteredUderName(context);
				if (FBNUtil.checkInternet(context, CompanyList.class)) {
					// instantiate custom adapter
						ListView eligibleO = (ListView) findViewById(R.id.eligibleOpeningsList);
						CompanyListRequestTask eligibleEventRequestTask = new CompanyListRequestTask(context,
								userName, eligibleO, this, eligible_Openings);
						eligibleEventRequestTask.execute();
				} else {
					FBNUtil.showInternetErrorDetails(this);
				}
			} else {
				DBController controller = new DBController(context);
				controller.deleteUserInfo();
				eligible_Openings.setText("Not LoggedIn.Please login");
				navigateToLoginActivity();
			}
		} catch (Exception e) {
			WSURL.setLocalhost(host);
			e.printStackTrace();
		}
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
		super.invalidateOptionsMenu();
		return true;
	}
	
}
