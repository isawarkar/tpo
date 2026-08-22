package com.ut.fbn.admin;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import com.ut.fbn.admin.R;
import com.ut.fbn.admin.extension.DrawableClickListener;
import com.ut.fbn.admin.task.ChangePasswordTask;
import com.ut.fbn.admin.util.Encryption;
import com.ut.fbn.admin.util.FBNConstants;
import com.ut.fbn.admin.util.FBNUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordActivity extends Parent {
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	final Context context = this;
	private Button submit;
	private Button clearButton;
	private EditText password;
	private EditText newPassword;
	private EditText confirmPassword;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changepassword);
		// showAdd(R.id.regiterLayout);

		password = (EditText) findViewById(R.id.password);
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

		newPassword = (EditText) findViewById(R.id.newPassword);
		newPassword.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(password) {
			@Override
			public boolean onDrawableClick() {
				if (newPassword.getInputType() == 129
						|| newPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD
						|| newPassword.getInputType() == InputType.TYPE_CLASS_TEXT) {
					newPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					newPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login1, 0);
				} else {
					newPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
					newPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login, 0);
				}
				return true;
			}
		});

		confirmPassword = (EditText) findViewById(R.id.confirmPassword);
		confirmPassword.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(password) {
			@Override
			public boolean onDrawableClick() {
				if (confirmPassword.getInputType() == 129
						|| confirmPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD
						|| confirmPassword.getInputType() == InputType.TYPE_CLASS_TEXT) {
					confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					confirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login1, 0);
				} else {
					confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
					confirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.login, 0);
				}
				return true;
			}
		});

		clearButton = (Button) findViewById(R.id.clearButton);
		clearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (password != null) {
					password.setText("");
				}
				if (newPassword != null) {
					newPassword.setText("");
				}
				if (confirmPassword != null) {
					confirmPassword.setText("");
				}

			}
		});
		submit = (Button) findViewById(R.id.submitButton);

		// add button listener
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (FBNUtil.checkInternet(ChangePasswordActivity.this, ChangePasswordActivity.class)) {

					try {
						JSONObject registerObject = new JSONObject();

						if (password.getText() == null || "".equals(password.getText().toString())) {
							password.setFocusable(true);
							password.setFocusableInTouchMode(true);
							password.setError("Old password is required!");
							return;
						} else {
							registerObject.put("password",
									Encryption.getEncryptedString(password.getText().toString()));
						}

						if (newPassword.getText() == null || "".equals(newPassword.getText().toString())) {
							confirmPassword.setFocusable(true);
							confirmPassword.setFocusableInTouchMode(true);
							newPassword.setError("New password is required!");
							return;
						} else {
							registerObject.put("newPassword",
									Encryption.getEncryptedString(newPassword.getText().toString()));
						}

						if (confirmPassword.getText() == null || "".equals(confirmPassword.getText().toString())) {
							confirmPassword.setFocusable(true);
							confirmPassword.setFocusableInTouchMode(true);
							confirmPassword.setError("Confirm password is required!");
							return;
						}

						if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
							FBNUtil.showErrorDialog("New password and Confirm password do not match!", context);
							return;
						}
						setUserName(registerObject);

						ChangePasswordTask task = new ChangePasswordTask(context, registerObject,
								ChangePasswordActivity.this);
						task.execute();
					} catch (JSONException e) {

						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {

						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {

						e.printStackTrace();
					}
				}
			}
		});

	}

	private void setUserName(JSONObject registerObject) throws JSONException {
		SharedPreferences prefs = getSharedPreferences(FBNConstants.MY_PREFS_NAME, MODE_PRIVATE);
		if (prefs != null) {
			String userName = prefs.getString(FBNConstants.USERNAME, null);
			if (userName != null) {
				registerObject.put("userName", userName);
			} else {
				FBNUtil.showErrorDialog("You are not currently logged in!", context);
				return;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		SharedPreferences prefs = getSharedPreferences(FBNConstants.MY_PREFS_NAME, MODE_PRIVATE);
		if (FBNUtil.isUserLoggedIn(context)) {
			allLoggedInMenuItems(menu);
		} else {
			allMenuItems(menu, false, false);
		}
		this.menu = menu;
		return true;
	}
}
