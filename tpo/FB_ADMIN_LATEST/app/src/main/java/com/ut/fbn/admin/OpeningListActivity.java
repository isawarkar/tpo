package com.ut.fbn.admin;

import com.ut.fbn.admin.R;
import com.ut.fbn.admin.task.OpeningListRequestTask;
import com.ut.fbn.admin.util.DBController;
import com.ut.fbn.admin.util.FBNUtil;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class OpeningListActivity extends Parent {

	public static Integer companyID = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.openinglist);

		if (companyID != null) {
			try {
				TextView eligible_Openings = (TextView) findViewById(R.id.Eligible_Openings);

				try {
					
					if (FBNUtil.isUserLoggedIn(context)) {
					
						if (FBNUtil.checkInternet(context, CompanyList.class)) {
								ListView eligibleO = (ListView) findViewById(R.id.eligibleOpeningsList);
								OpeningListRequestTask eligibleEventRequestTask = new OpeningListRequestTask(context,
										companyID, eligibleO, this, eligible_Openings);
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
					e.printStackTrace();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
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
		return true;
	}

}
