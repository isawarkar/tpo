package com.ut.fbn.admin;

import com.ut.fbn.admin.task.StudentListRequestTask;
import com.ut.fbn.admin.util.DBController;
import com.ut.fbn.admin.util.FBNUtil;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class StudentListActivity extends Parent {

	public static Integer hallticketId = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.studentlist);

		if (hallticketId != null) {
			try {
				TextView studentCount = (TextView) findViewById(R.id.studentCount);

				try {
					
					if (FBNUtil.isUserLoggedIn(context)) {
					
						if (FBNUtil.checkInternet(context, CompanyList.class)) {
								ListView studentList = (ListView) findViewById(R.id.studentList);
								StudentListRequestTask studentListTask = new StudentListRequestTask(context,
										hallticketId, studentList, this, studentCount);
								studentListTask.execute();
						} else {
							FBNUtil.showInternetErrorDetails(this);
						}
					} else {
						DBController controller = new DBController(context);
						controller.deleteUserInfo();
						studentCount.setText("Not LoggedIn.Please login");
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
