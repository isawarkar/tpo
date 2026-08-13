package com.ut.fbn;

import com.ut.task.EligibleEventRequestTask;
import com.ut.util.DBController;
import com.ut.util.FBNUtil;
import com.ut.util.WSURL;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Parent {

	Context context;
	public Menu menu;
	private String host = null;
	String registerdEnrollment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
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
				FBNUtil.scheduleEventRecever(context, getBaseContext());
				FBNUtil.scheduleNoticeRecever(context, getBaseContext());
				FBNUtil.scheduleFeeReminderRecever(context, getBaseContext());

				registerdEnrollment = FBNUtil.getRegisteredUserName(context);
				if (FBNUtil.checkInternet(context, MainActivity.class)) {
					// instantiate custom adapter
						ListView eligibleO = (ListView) findViewById(R.id.eligibleOpeningsList);
						EligibleEventRequestTask eligibleEventRequestTask = new EligibleEventRequestTask(context,
								registerdEnrollment, eligibleO, this, eligible_Openings);
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
