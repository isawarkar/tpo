package com.ut.fbn.admin;

import com.ut.fbn.admin.util.FBNConstants;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

public class ParentFragmentActivity extends AppCompatActivity {

	Context context = this;
	public Menu menu;

	protected void allLoggedInMenuItems(Menu menu) {
		MenuItem logout = menu.add(FBNConstants.Home);
		logout.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				navigateToHomeActivity();
				return true;
			}
		});
		MenuItem scanqr = menu.add(FBNConstants.Scan_QR);
		scanqr.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				navigateToScanQRActivity();
				return true;
			}
		});

		allMenuItems(menu, true, true);
	}

	protected void allMenuItems(Menu menu, boolean loggedIn, boolean homeAdded) {

		if (!loggedIn) {
			MenuItem login = menu.add(FBNConstants.Login);
			login.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					navigateToLoginActivity();
					return true;
				}
			});
		}

		MenuItem about = menu.add(FBNConstants.About);
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
	public void navigateToAboutActivity() {
		Intent homeIntent = new Intent(context, AboutActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

	public void navigateToScanQRActivity() {
		Intent homeIntent = new Intent(context, ScanQRCodeActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

	public void changePasswordActivity() {
		Intent homeIntent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

	public void forgotPasswordActivity() {
		Intent homeIntent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}
}
