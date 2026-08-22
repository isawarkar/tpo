package com.ut.fbn.admin;

import com.ut.fbn.admin.R;
import com.ut.fbn.admin.task.ForgotPasswordTask;
import com.ut.fbn.admin.util.FBNUtil;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPasswordActivity extends Parent {
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	final Context context = this;
	private Button submit;
	private Button clearButton;
	private EditText enrollmentNo;
	private EditText email;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgotpassword);
		// showAdd(R.id.regiterLayout);

		enrollmentNo = (EditText) findViewById(R.id.enrollmentNo);

		email = (EditText) findViewById(R.id.email);

		clearButton = (Button) findViewById(R.id.clearButton);
		clearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (enrollmentNo != null) {
					enrollmentNo.setText("");
				}
				if (email != null) {
					email.setText("");
				}

			}
		});
		submit = (Button) findViewById(R.id.submitButton);

		// add button listener
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (FBNUtil.checkInternet(ForgotPasswordActivity.this, ForgotPasswordActivity.class)) {

					try {
						if (enrollmentNo.getText() == null || "".equals(enrollmentNo.getText().toString())) {
							enrollmentNo.setFocusable(true);
							enrollmentNo.setFocusableInTouchMode(true);
							enrollmentNo.setError("EnrollmentNo is required!");
							return;
						}

						if (email.getText() == null || "".equals(email.getText().toString())) {
							email.setFocusable(true);
							email.setFocusableInTouchMode(true);
							email.setError("E-mail is required!");
							return;
						}

						ForgotPasswordTask forgotPasswordTask = new ForgotPasswordTask(context,
								enrollmentNo.getText().toString(), email.getText().toString(),
								ForgotPasswordActivity.this);
						forgotPasswordTask.execute();
					} catch (Exception e) {

						e.printStackTrace();
					}
				}
			}
		});

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
