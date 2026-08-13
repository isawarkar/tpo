package com.ut.fbn.admin;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ut.fbn.admin.task.StudentArrivedTask;
import com.ut.fbn.admin.util.AES;
import com.ut.fbn.admin.util.FBNUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//implementing onclicklistener
public class ScanQRCodeActivity extends Parent {

	// View Objects
	private Button buttonScan;
	private TextView companyID, companyName, createdBy, verifiedByServer,enrollmentNo;
	private TextView totalShotListed;
	private TextView totalApplied;
	private TextView totalApproved;
	private TextView totalArrived;;
	
	ImageView profileImage;

	public static final int SCAN_IMAGE = 1;

	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanqrcode);
		context = getApplicationContext();
		try {
			// View objects
			buttonScan = (Button) findViewById(R.id.buttonScan);
			companyID = (TextView) findViewById(R.id.companyID);
			companyName = (TextView) findViewById(R.id.companyName);
			createdBy = (TextView) findViewById(R.id.createdBy);
			verifiedByServer = (TextView) findViewById(R.id.verifiedByServer);
			enrollmentNo = (TextView) findViewById(R.id.enrollmentNo);
			totalShotListed = (TextView) findViewById(R.id.totalShotListed);
			totalApplied = (TextView) findViewById(R.id.totalApplied);
			totalApproved = (TextView) findViewById(R.id.totalApproved);
			totalArrived = (TextView) findViewById(R.id.totalArrived);
			
			profileImage = (ImageView) findViewById(R.id.profileImage);
			profileImage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateToFullProfileImageActivity();
				}
			});
			

			buttonScan.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					profileImage.setImageURI(null);
					profileImage.setVisibility(View.GONE);
					companyID.setText("");
					companyName.setText("");
					createdBy.setText("");
					verifiedByServer.setText("");
					enrollmentNo.setText("");
					totalShotListed.setText("");
					totalApplied.setText("");
					totalApproved.setText("");
					totalArrived.setText("");
					IntentIntegrator.initiateScan(ScanQRCodeActivity.this, "You need Barcode!",
							"Please click YES to install the ZXING scanner", "YES", "Cancel");
			}
			});// closing the setOnClickListener method

		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	// Getting the scan results
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (result != null) {
			// if qrcode has nothing in it
			if (result.getContents() == null) {
				Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
				FBNUtil.showErrorDialog("Result Not Found!", this);
			} else {
				String dataString = AES.symmetricDecrypt(result.getContents(), FBNUtil.S);
				String[] str = dataString.split("#");
				if (str != null && str.length == 4) {
					enrollmentNo.setText(str[0]);
					companyID.setText(str[1]);
					companyName.setText(str[2]);
					createdBy.setText(str[3]);
					verifiedByServer.setText("Verifiying........");
					StudentArrivedTask studentArrivedTask = new StudentArrivedTask(str[0],dataString,this,verifiedByServer,totalShotListed,totalApplied,totalApproved,totalArrived,profileImage,context);
					studentArrivedTask.execute();
					Toast.makeText(this, "Verifiying........", Toast.LENGTH_LONG).show();
				}else {
					Toast.makeText(this, "Invalid QR code!", Toast.LENGTH_LONG).show();
					FBNUtil.showErrorDialog("Invalid QR code!", this);
				}
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
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
		super.invalidateOptionsMenu();
		return true;
	}

}