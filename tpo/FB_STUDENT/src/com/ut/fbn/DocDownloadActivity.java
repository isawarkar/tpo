package com.ut.fbn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ut.fbn.adapter.DownloadedDocsCustomAdapter;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

@SuppressWarnings("deprecation")
public class DocDownloadActivity extends Parent {

	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.downloaddocslistview);
		context = getApplicationContext();
		
		FBNUtil.getReadPhoneStatePermission(context,this);
		FBNUtil.getWriteExternalPermission(context,this);
		FBNUtil.getReadExternalPermission(context,this);
		
		File path = new File(FBNConstants.FBN_DOWNLOAD_DIR);
		if (path != null) {
			File[] files = path.listFiles();
			if (files != null) {
				List<String> filesList = new  ArrayList<String>(files.length);
				for (File file : files) {
					filesList.add(file.getName());
				}
				final ListView listviewOld = (ListView) findViewById(R.id.downloadedticketslist);
				DownloadedDocsCustomAdapter adapterOld = new DownloadedDocsCustomAdapter(this,context, filesList);
				listviewOld.setAdapter(adapterOld);
				FBNUtil.setListBackground(listviewOld);
			}
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		if (FBNUtil.isUserLoggedIn(context)) {
			allLoggedInMenuItems(menu);
		}else {
			allMenuItems(menu, false, false);
		}
		this.menu = menu;
		return true;
	}
}
