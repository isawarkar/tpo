package com.ut.fbn.admin;

import com.ut.fbn.admin.R;
import com.ut.fbn.admin.extension.DrawableClickListener;
import com.ut.fbn.admin.task.LoginTask;
import com.ut.fbn.admin.util.FBNUtil;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Parent {

	Context context = this;

	EditText userName, password;

	Button adminButton;

	public Menu menu;

	TextView forgotPassword;

	CheckBox box;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		context = getApplicationContext();

		if (com.ut.fbn.admin.util.WSURL.localhost == null) {
			String host = FBNUtil.getProperty("device_localhost", context);
			if (host != null) {
				com.ut.fbn.admin.util.WSURL.setLocalhost(host);
			}
		}

		FBNUtil.getWriteExternalPermission(context, this);
		FBNUtil.getReadExternalPermission(context, this);

		forgotPassword = (TextView) findViewById(R.id.forgotPassword);

		userName = (EditText) findViewById(R.id.userName);
		password = (EditText) findViewById(R.id.password);
		adminButton = (Button) findViewById(R.id.adminButton);

		password.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(password) {
			@Override
			public boolean onDrawableClick() {
				if (password.getInputType() == 129 || password.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD
						|| password.getInputType() == InputType.TYPE_CLASS_TEXT) {
					password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login1, 0);
				} else {
					password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
					password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login, 0);
				}
				return true;
			}
		});

		adminButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				validateForm();
				if (FBNUtil.checkInternet(context, LoginActivity.class)) {
					LoginTask loginTask = new LoginTask(userName.getText().toString(), password.getText().toString(),
							context, LoginActivity.this);
					loginTask.execute();
				} else {
					FBNUtil.showInternetErrorDetails(LoginActivity.this);
				}
			}
		});

		forgotPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				forgotPasswordActivity();
			}

		});

	}

	private void validateForm() {
		if (userName.getText() == null || "".equals(userName.getText().toString())) {
			userName.setError("User name is required!");
			return;
		} else {
			userName.setBackgroundColor(Color.WHITE);
		}

		if (password.getText() == null || "".equals(password.getText().toString())) {
			password.setError("Password is required!");
			return;
		} else {
			password.setBackgroundColor(Color.WHITE);
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
