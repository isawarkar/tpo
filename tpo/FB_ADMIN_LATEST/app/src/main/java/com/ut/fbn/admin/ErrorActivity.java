package com.ut.fbn.admin;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.Button;

import com.ut.fbn.admin.R;

public class ErrorActivity extends Parent {

	private Button homeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.errorlayout);

		homeButton = (Button) findViewById(R.id.homeButton);
		homeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				navigateToHomeActivity();
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		MenuItem home = menu.add("Home");
		home.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				navigateToHomeActivity();
				return true;
			}
		});
		
		
		/*
		 * if (FBNUtil.isUserLoggedIn(context)) { allLoggedInMenuItems(menu); } else {
		 * allMenuItems(menu, false, false); }
		 */
		this.menu = menu;
		return true;
	}

}