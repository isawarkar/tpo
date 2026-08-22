package com.ut.fbn.admin;

import java.io.File;

import com.ut.fbn.admin.R;
import com.ut.fbn.admin.util.FBNConstants;
import com.ut.fbn.admin.util.FBNUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FullProfileImageActivity extends Parent {

	private ImageView noticeImage;

	private Button downloadNotice;

	private TextView fileNameText;

	public Menu menu;

	Context context;

	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.attachment);
			context = getApplicationContext();

			noticeImage = (ImageView) findViewById(R.id.noticeImage);
			downloadNotice = (Button) findViewById(R.id.downloadNotice);
			fileNameText = (TextView) findViewById(R.id.fileName);

			String registeredUserName = FBNUtil.getRegisteredUderName(context);
			final String fileName = registeredUserName + ".jpg";
			File file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fileName); // Creating an internal dir;
			if (file.exists()) {
				final Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
				noticeImage.setImageURI(uri);
				fileNameText.setText("Name:" + fileName);
				downloadNotice.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						FBNUtil.showProgress(FullProfileImageActivity.this);
						Intent share = new Intent();
						share.setAction(Intent.ACTION_SEND);
						share.setType("application/jpg");
						share.putExtra(Intent.EXTRA_STREAM, uri);
						share.putExtra(Intent.EXTRA_TEXT, fileName);
						share.putExtra(Intent.EXTRA_SUBJECT, fileName);
						share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						startActivity(Intent.createChooser(share, "Share File"));
						FBNUtil.hideProgress(FullProfileImageActivity.this);
					}
				});
			}

		} catch (Exception e) {
			
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
