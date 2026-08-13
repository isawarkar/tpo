package com.ut.fbn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AttachmentActivity extends Parent {

	private ImageView noticeImage;
	
	private TextView fileNameText;

	private Button downloadNotice;

	public Menu menu;

	public static String fileNameStr;

	public static String fileString;

	Context context;

	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.attachment);
			context = getApplicationContext();

			fileNameText = (TextView) findViewById(R.id.fileName);
			noticeImage = (ImageView) findViewById(R.id.noticeImage);
			downloadNotice = (Button) findViewById(R.id.downloadNotice);
			
			String registerdEnrollment = FBNUtil.getRegisteredUserName(context);
			final String fileName = registerdEnrollment + "_" + fileNameStr;
			final File file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fileName);
			if (!file.exists()) {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(Base64.decode(fileString, Base64.NO_WRAP));
				fos.close();
			}
			final Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
			if (file.exists()) {
				fileNameText.setText("Name:" + fileName);
				noticeImage.setImageURI(uri);
			}
			
			downloadNotice.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					FBNUtil.showProgress(AttachmentActivity.this);
					Intent share = new Intent();
					share.setAction(Intent.ACTION_SEND);
					share.setType("application/jpg");
					share.putExtra(Intent.EXTRA_STREAM, uri);
					share.putExtra(Intent.EXTRA_TEXT, fileName);
					share.putExtra(Intent.EXTRA_SUBJECT, fileName);
					share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
					startActivity(Intent.createChooser(share, "Share File"));
					FBNUtil.hideProgress(AttachmentActivity.this);
				}
			});
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
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
