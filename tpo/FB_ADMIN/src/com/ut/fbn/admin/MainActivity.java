package com.ut.fbn.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lowagie.text.pdf.codec.Base64;
import com.ut.fbn.admin.task.CompanyListRequestTask;
import com.ut.fbn.admin.task.DownloadProfileImageTask;
import com.ut.fbn.admin.task.UploadProfileImageTask;
import com.ut.fbn.admin.util.DBController;
import com.ut.fbn.admin.util.FBNConstants;
import com.ut.fbn.admin.util.FBNUtil;
import com.ut.fbn.admin.util.WSURL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainActivity extends Parent {

	Context context;
	public static final int PICK_IMAGE = 1;

	public Menu menu;
	private String host = null;

	TextView userNameText;

	ImageView profileImage, logoutImage, menuIconImaage;

	String registeredUserName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		context = getApplicationContext();

		FBNUtil.getWriteExternalPermission(context, this);
		FBNUtil.getReadExternalPermission(context, this);
		try {
			if (WSURL.localhost == null) {
				host = FBNUtil.getProperty("device_localhost", context);
				WSURL.setLocalhost(host);
			}
			if (FBNUtil.isUserLoggedIn(context)) {

				TextView eligible_Openings = (TextView) findViewById(R.id.Eligible_Openings);
				FBNUtil.scheduleNoticeRecever(context, getBaseContext());
				registeredUserName = FBNUtil.getRegisteredUderName(context);
				setProfileData(registeredUserName);
				if (FBNUtil.checkInternet(context, CompanyList.class)) {
					// instantiate custom adapter
					ListView eligibleO = (ListView) findViewById(R.id.eligibleOpeningsList);
					CompanyListRequestTask eligibleEventRequestTask = new CompanyListRequestTask(context,
							registeredUserName, eligibleO, this, eligible_Openings);
					eligibleEventRequestTask.execute();
				} else {
					FBNUtil.showInternetErrorDetails(this);
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

	private void setProfileData(String userName) {
		logoutImage = (ImageView) findViewById(R.id.logoutImage);
		logoutImage.setVisibility(View.VISIBLE);
		logoutImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					showConfirmationDialog();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		menuIconImaage = (ImageView) findViewById(R.id.menuIconImaage);
		menuIconImaage.setVisibility(View.VISIBLE);
		menuIconImaage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PopupMenu popup = new PopupMenu(MainActivity.this, menuIconImaage);
				popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						if (R.id.changePassword == item.getItemId()) {
							changePasswordActivity();
						}
						return true;
					}

				});

				popup.show();// showing popup menu
			}
		});// closing the setOnClickListener method

		profileImage = (ImageView) findViewById(R.id.profileImage);
		registerForContextMenu(profileImage);
		profileImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openContextMenu(v);
			}
		});// closing the setOnClickListener method

		DownloadProfileImageTask downloadProfileImageTask = new DownloadProfileImageTask(profileImage, context, this);
		downloadProfileImageTask.execute();

		userNameText = (TextView) findViewById(R.id.userName);
		userNameText.setText(userName);

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
					navigateToHomeActivity();
				} catch (Exception e) {
					// TODO Auto-generated catch block
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

	private void clear() {
		FBNUtil.clearAll(context);
		context.getSharedPreferences(FBNConstants.MY_PREFS_NAME, MODE_PRIVATE).edit().clear().commit();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
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
				String filePath = getRealPathFromURI(context, uri);
				File file = new File(filePath);
				filePath = saveBitmapToFile(file);
				String base64EncodedData = Base64.encodeFromFile(filePath);
				if (base64EncodedData != null) {
					updateLocalFile(base64EncodedData);
					UploadProfileImageTask uploadProfileImageTask = new UploadProfileImageTask(registeredUserName,
							base64EncodedData, this, context);
					uploadProfileImageTask.execute();
					profileImage.setImageURI(uri);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void updateLocalFile(String base64EncodedData) throws FileNotFoundException, IOException {
		File file = new File(FBNConstants.FBN_DOWNLOAD_DIR + registeredUserName + ".jpg");
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(Base64.decode(base64EncodedData));
		fos.close();
	}

	public String saveBitmapToFile(File file){
	    try {

	    	
	        // BitmapFactory options to downsize the image
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        o.inSampleSize = 6;
	        // factor of downsizing the image

	        FileInputStream inputStream = new FileInputStream(file);
	        //Bitmap selectedBitmap = null;
	        BitmapFactory.decodeStream(inputStream, null, o);
	        inputStream.close();

	        // The new size we want to scale to
	        final int REQUIRED_SIZE=100;

	        // Find the correct scale value. It should be the power of 2.
	        int scale = 1;
	        while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
	                        o.outHeight / scale / 2 >= REQUIRED_SIZE) {
	            scale *= 2;
	        }

	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize = scale;
	        inputStream = new FileInputStream(file);

	        Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
	        inputStream.close();

	        // here i override the original image file
	        file.createNewFile();
	        FileOutputStream outputStream = new FileOutputStream(file);

	        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

	        return file.getAbsolutePath();
	    } catch (Exception e) {
	        return null;
	    }
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
