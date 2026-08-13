package com.ut.fbn;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

public class ParentFragmentActivity extends FragmentActivity {

	Context context = this;
	public Menu menu;

	protected void allLoggedInMenuItems(Menu menu) {
		MenuItem home = menu.add("Home");
		home.setIcon(R.drawable.ic_launcher);
		home.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				navigateToHomeActivity();
				return true;
			}
		});
		
		MenuItem profile = menu.add("Profile");
		profile.setIcon(R.drawable.ic_launcher);
		profile.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				navigateToProfileActivity();
				return true;
			}
		});

		MenuItem testList = menu.add("Test Result List");
		testList.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				navigateToResultActivity();
				return true;
			}
		});
		MenuItem docuemntList = menu.add("Document List");
		docuemntList.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				navigateToDocuemntActivity();
				return true;
			}
		});
		MenuItem oldEvents = menu.add("Readed Opening's");
		oldEvents.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				navigateToOldEventActivity();
				return true;
			}
		});

		MenuItem oldNotices = menu.add("Readed Notice's");
		oldNotices.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				navigateToOldNoticeActivity();
				return true;
			}
		});

		MenuItem readedFeeReminder = menu.add("Readed Fee Reminder's");
		readedFeeReminder.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				navigateToOldFeeActivity();
				return true;
			}
		});
		allMenuItems(menu, true, true);
	}

	protected void allMenuItems(Menu menu, boolean loggedIn, boolean homeAdded) {
		if (!homeAdded) {
			MenuItem home = menu.add("Home");
			home.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					navigateToHomeActivity();
					return true;
				}
			});
		}

		if (!loggedIn) {
			MenuItem login = menu.add("Login");
			login.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					navigateToLoginActivity();
					return true;
				}
			});
		}

		MenuItem downloadedHT = menu.add("Downloaded Doc's");
		downloadedHT.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				navigateToDownloadedHallTicketActivity();
				return true;
			}
		});

		MenuItem settings = menu.add("Settings");
		settings.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				navigateToSettingActivity();
				return true;
			}
		});

		MenuItem about = menu.add("About");
		about.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				navigateToAboutActivity();
				return true;
			}
		});

	}

	public void navigateToLoginActivity() {
		Intent homeIntent = new Intent(context, LoginActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

	/**
	 * 
	 */
	public void navigateToHomeActivity() {
		Intent homeIntent = new Intent(context, MainActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}
	
	/**
	 * 
	 */
	public void navigateToProfileActivity() {
		Intent homeIntent = new Intent(context, ProfileActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

	/**
	 * 
	 */
	public void navigateToDownloadedHallTicketActivity() {
		Intent homeIntent = new Intent(context, DocDownloadActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

	/**
	 * 
	 */
	public void navigateToSettingActivity() {
		Intent homeIntent = new Intent(context, SettingActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

	/**
	 * 
	 */
	public void navigateToAboutActivity() {
		Intent homeIntent = new Intent(context, AboutActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

	/**
	 * 
	 */
	public void navigateToOldEventActivity() {
		Intent homeIntent = new Intent(context, OldEventActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

	public void navigateToOldNoticeActivity() {
		Intent homeIntent = new Intent(context, OldNoticeActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
		return true;
	}

	/**
	 * Method which navigates from Login Activity to Home Activity
	 */
	public void navigatetoHomeActivity() {
		Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

	public void navigateToResultActivity() {
		Intent homeIntent = new Intent(context, ResultActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

	public void navigateToDocuemntActivity() {
		Intent homeIntent = new Intent(context, DocuemntActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

	public void navigateToOldFeeActivity() {
		Intent homeIntent = new Intent(context, OldFeeActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}


}
