package com.ut.fbn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreenActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		e.printStackTrace();
		}
		startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
		finish();
	}
}