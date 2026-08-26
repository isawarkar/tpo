package com.ut.fbn;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.ut.fbn.adapter.DocumentListCustomAdapter;
import com.ut.pojo.Document;
import com.ut.task.DocumentListRequestTask;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;
import com.ut.util.WSURL;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class DocuemntActivity extends Parent {

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.documentlistview);
		context = getApplicationContext();
		try {
			final ListView listview = (ListView) findViewById(R.id.documentList);

			String registerdEnrollment = FBNUtil.getRegisteredUserName(context);
			if (registerdEnrollment != null) {
				if (FBNUtil.checkInternet(context, DocuemntActivity.class)) {
					// instantiate custom adapter
						JSONArray arr = FBNUtil.getJSONArraytList(FBNConstants.DOCUMENT_LIST, context);
						List<Document> documentList = null;
						if (arr != null && arr.length() > 0) {
							documentList = new ArrayList<Document>(arr.length());
							try {
								for (int i = 0; i < arr.length(); i++) {
									Document document = FBNUtil.convertDocumentRequest(arr.getJSONObject(i));
									documentList.add(document);
								}
							} catch (JSONException e) {

								e.printStackTrace();
							}
							DocumentListCustomAdapter adapter = new DocumentListCustomAdapter(documentList, context, this);
							listview.setAdapter(adapter);
							FBNUtil.setListBackground(listview);
						}
						DocumentListRequestTask documentListRequestTask = new DocumentListRequestTask(context,
								registerdEnrollment, listview, this);
						documentListRequestTask.execute();
				} else {
					FBNUtil.showInternetErrorDetails(context);
				}
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
