package com.ut.fbn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.lowagie.text.pdf.codec.Base64;
import com.ut.fbn.adapter.EligibleHallTicketCustomAdapter;
import com.ut.fbn.sms.SmsListener;
import com.ut.fbn.sms.SmsReceiver;
import com.ut.task.DownloadProfileTask;
import com.ut.task.DownloadRegistrationFormTask;
import com.ut.task.DownloadResumeTask;
import com.ut.task.SendSmsTask;
import com.ut.task.StudentSpecificNoticTask;
import com.ut.task.UploadProfileImageTask;
import com.ut.task.VerifyNumberTask;
import com.ut.util.DBController;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;
import com.ut.util.WSURL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.text.InputFilter;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

public class ProfileActivity extends Parent {

	Context context;
	public static final int PICK_IMAGE = 1;

	public Menu menu;
	private String host = null;

	TextView message, mobileView, emailAddress;

	ImageView profileImage, logoutImage, menuIconImaage, qrCodeImaage, verifiedImage, verifiedEmailImage;
	CheckBox box;
	Button verifyMobileNO;

	String registerdEnrollment;

	int pin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profilelayout);
		context = getApplicationContext();
		FBNUtil.getReadPhoneStatePermission(context, this);
		FBNUtil.getWriteExternalPermission(context, this);
		FBNUtil.getReadExternalPermission(context, this);
		FBNUtil.getSendSms(context, this);

		try {
			if (FBNUtil.isUserLoggedIn(context)) {
				setProfileData();
				TextView new_notices = (TextView) findViewById(R.id.new_notices);
				ListView listview = (ListView) findViewById(R.id.noticeList);
				
				String registerdEnrollment = FBNUtil.getRegisteredUserName(context);
				if (registerdEnrollment != null) {
					StudentSpecificNoticTask task = new StudentSpecificNoticTask(context, registerdEnrollment,this,listview,new_notices);
					task.execute();
				}
			} else {
				DBController controller = new DBController(context);
				controller.deleteUserInfo();
				navigateToLoginActivity();
			}
		} catch (Exception e) {
			WSURL.setLocalhost(host);
			e.printStackTrace();
		}
	}

	private void setProfileData() {
		logoutImage = (ImageView) findViewById(R.id.logoutImage);
		logoutImage.setVisibility(View.VISIBLE);
		profileImage = (ImageView) findViewById(R.id.profileImage);
		registerForContextMenu(profileImage);
		profileImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openContextMenu(v);
			}
		});// closing the setOnClickListener method
		verifiedImage = (ImageView) findViewById(R.id.verifiedImage);
		verifiedEmailImage = (ImageView) findViewById(R.id.verifiedEmailImage);
		menuIconImaage = (ImageView) findViewById(R.id.menuIconImaage);
		menuIconImaage.setVisibility(View.VISIBLE);
		menuIconImaage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PopupMenu popup = new PopupMenu(ProfileActivity.this, menuIconImaage);
				popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						if (R.id.downloadOrignalResume == item.getItemId()) {
							downloadResume();
						} else if (R.id.downloadRegistrationForm == item.getItemId()) {
							downloadRegistrationForm();

						} else if (R.id.changePassword == item.getItemId()) {
							changePasswordActivity();

						}
						// Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(),
						// Toast.LENGTH_SHORT).show();
						return true;
					}

					private void downloadRegistrationForm() {
						String enrollmentNo = FBNUtil.getRegisteredUserName(context);
						String fineName = enrollmentNo + ".PDF";
						File file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fineName);
						if (file != null && file.exists()) {
							FBNUtil.showProgress(ProfileActivity.this);
							Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
							Intent share = new Intent();
							share.setAction(Intent.ACTION_SEND);
							share.setType("application/pdf");
							share.putExtra(Intent.EXTRA_STREAM, uri);
							share.putExtra(Intent.EXTRA_TEXT, "Resume of " + enrollmentNo);
							share.putExtra(Intent.EXTRA_SUBJECT, "Resume of " + enrollmentNo);
							share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
							startActivity(Intent.createChooser(share, "Share File"));
							FBNUtil.hideProgress(ProfileActivity.this);
						} else {
								DownloadRegistrationFormTask downloadRegistrationFormTask = new DownloadRegistrationFormTask(
										enrollmentNo, context, ProfileActivity.this);
								downloadRegistrationFormTask.execute();
						}
					}

					private void downloadResume() {
						if (FBNUtil.checkInternet(context, EligibleHallTicketCustomAdapter.class)) {
							String enrollmentNo = FBNUtil.getRegisteredUserName(context);
							DownloadResumeTask hallTicketTask = new DownloadResumeTask(enrollmentNo, context,
									ProfileActivity.this);
							hallTicketTask.execute();
						} else {
							FBNUtil.showInternetErrorDetails(ProfileActivity.this);
						}
					}
				});

				popup.show();// showing popup menu
			}
		});// closing the setOnClickListener method

		qrCodeImaage = (ImageView) findViewById(R.id.qrCodeImaage);
		qrCodeImaage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String fineName = registerdEnrollment + ".png";
				File file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fineName); // Creating an internal dir;
				if (file.exists()) {
					Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
					qrCodeImaage.setImageURI(uri);
					navigateToQRProfileImageActivity();
				}
			}

		});

		message = (TextView) findViewById(R.id.message);
		emailAddress = (TextView) findViewById(R.id.emailAddress);
		mobileView = (TextView) findViewById(R.id.mobileNo);
		verifyMobileNO = (Button) findViewById(R.id.verifyMobileNO);
		verifyMobileNO.setVisibility(View.VISIBLE);
		message = (TextView) findViewById(R.id.message);

		String emailAddressStr = FBNUtil.getText(FBNConstants.EMAIL_ADDRESS, context);
		if (emailAddressStr != null) {
			emailAddress.setText(emailAddressStr);
		}
		String profileText = FBNUtil.getText(FBNConstants.PROFILE_TEXT, context);
		if (profileText != null) {
			message.setText(profileText);
		}
		String mNO = FBNUtil.getText(FBNConstants.MOBILE_NO, context);
		if (mNO != null) {
			mobileView.setText(mNO);
		}
		String verifyMobileNOStr = FBNUtil.getText(FBNConstants.MOBILE_VERIFIED, context);
		if (verifyMobileNOStr != null) {
			if ("YES".equals(verifyMobileNOStr)) {
				verifiedImage.setVisibility(View.VISIBLE);
				verifyMobileNO.setVisibility(View.GONE);
			} else {
				verifyMobileNO.setVisibility(View.VISIBLE);
				verifiedImage.setVisibility(View.GONE);
			}
			verifyMobileNO.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FBNUtil.getSendSms(context, ProfileActivity.this);
					FBNUtil.getReceiveSms(context, ProfileActivity.this);
					pin = FBNUtil.get6DigitRandomNumber();
					SendSmsTask sendSmsTask = new SendSmsTask(mobileView.getText().toString(),
							"Dear,\nYour PIN is " + pin + "\nRegards,\nFresher Buddy", ProfileActivity.this, context);
					sendSmsTask.execute();
					AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
					builder.setTitle("Mobile No Verify");
					builder.setMessage("Please enter PIN sent on your mobile?");
					builder.setIcon(R.drawable.ic_launcher);

					// Set up the input
					final EditText password = new EditText(ProfileActivity.this);

					int maxLength = 6;
					password.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength) });
					builder.setView(password);

					SmsReceiver.bindListener(new SmsListener() {
						@Override
						public void messageReceived(List<String> list) {
							if (list != null) {
								String sender = list.get(0);
								if (mobileView.getText() != null && mobileView.getText().toString().endsWith(sender)) {
									VerifyNumberTask task = new VerifyNumberTask(context, registerdEnrollment,
											ProfileActivity.this);
									task.execute();
								}
								password.setText(list.get(1));
							}
						}
					});

					// Set up the buttons
					builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {

								if (password.getText() == null || "".equals(password.getText().toString())) {
									password.setFocusable(true);
									password.setFocusableInTouchMode(true);
									FBNUtil.showErrorDialog("Incorrect PIN entered", ProfileActivity.this);
									return;
								}
								int enteredPin = Integer.valueOf(password.getText().toString());
								if (pin == enteredPin) {
									VerifyNumberTask task = new VerifyNumberTask(context, registerdEnrollment,
											ProfileActivity.this);
									task.execute();
								} else {
									FBNUtil.showErrorDialog("Please enter correct numeric PIN", ProfileActivity.this);
								}
							} catch (NumberFormatException e) {
								FBNUtil.showErrorDialog("Please enter numeric PIN", ProfileActivity.this);
								e.printStackTrace();
							}
						}
					});
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					builder.show();
				}
			});
		}

		String verifyEmailStr = FBNUtil.getText(FBNConstants.EMAIL_VERIFIED, context);
		if (verifyEmailStr != null) {
			if ("YES".equals(verifyEmailStr)) {
				verifiedEmailImage.setVisibility(View.VISIBLE);
			} else {
				verifiedEmailImage.setVisibility(View.GONE);
			}
		}
		registerdEnrollment = FBNUtil.getRegisteredUserName(context);
		String fineName = registerdEnrollment + ".jpg";
		File file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fineName); // Creating an internal dir;
		if (file.exists()) {
			Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
			profileImage.setImageURI(uri);
		}
			DownloadProfileTask hallTicketTask = new DownloadProfileTask(message, mobileView, verifyMobileNO,
					emailAddress, profileImage, registerdEnrollment, verifiedImage, qrCodeImaage, verifiedEmailImage, context, this);
			hallTicketTask.execute();
		
		logoutImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					showConfirmationDialog();
				} catch (Exception e) {

					e.printStackTrace();
				}
			}

		});
		verifiedEmailImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					showEmailOrMobileDialog("E-mail address");
				} catch (Exception e) {

					e.printStackTrace();
				}
			}

		});
		verifiedImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					showEmailOrMobileDialog("Mobile Number");
				} catch (Exception e) {

					e.printStackTrace();
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
		super.invalidateOptionsMenu();
		return true;
	}

	private void showConfirmationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Confirmaation");
		builder.setMessage("Do you realy want to logout?");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				try {
					clear();
					dialog.dismiss();
					// FBNUtil.sendSMS(context, "Your are successfully loggedout\n.Please login
					// again.", "9589555592");
					navigatetoHomeActivity();
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showEmailOrMobileDialog(String type) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(type + " Verified");
		builder.setMessage("You " + type + " is Verified!");
		builder.setIcon(R.drawable.ic_launcher);

		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void clear() {
		FBNUtil.clearAll(context);
		context.getSharedPreferences(FBNUtil.MY_PREFS_NAME, MODE_PRIVATE).edit().clear().commit();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
		menu.setHeaderTitle("Select The Action");
		menu.setHeaderIcon(R.drawable.alert);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		try {
			if (item.getItemId() == R.id.upload) {

				Intent pickIntent = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				pickIntent.setType("image/*");

				Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
				startActivityForResult(Intent.createChooser(chooserIntent, "Select Picture"), PICK_IMAGE);
			} else if (item.getItemId() == R.id.fullimage) {
				navigateToFullProfileImageActivity();
			} else {
				return false;
			}
			return true;
		} catch (Exception e) {

			e.printStackTrace();
		} catch (Throwable e) {

			e.printStackTrace();
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null && data.getData() != null) {
				Uri uri = data.getData();
				profileImage.setImageURI(uri);
				String filePath = getRealPathFromURI(context, uri);
				String base64EncodedData = Base64.encodeFromFile(filePath);
				updateLocalFile(base64EncodedData);
				UploadProfileImageTask uploadProfileImageTask = new UploadProfileImageTask(registerdEnrollment,
						base64EncodedData, this,context);
				uploadProfileImageTask.execute();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		} catch (Throwable e) {

			e.printStackTrace();
		}
	}

	private void updateLocalFile(String base64EncodedData) throws FileNotFoundException, IOException {
		File file = new File(FBNConstants.FBN_DOWNLOAD_DIR + registerdEnrollment + ".jpg");
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(Base64.decode(base64EncodedData));
		fos.close();
	}

	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

}
