package com.ut.fbn;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.ut.fbn.adapter.ResultListCustomAdapter;
import com.ut.pojo.Result;
import com.ut.task.ResultListRequestTask;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ResultActivity extends Parent {

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resultlistview);
		context = getApplicationContext();
		try {
			final ListView listview = (ListView) findViewById(R.id.resultList);

			String registerdEnrollment = FBNUtil.getRegisteredUserName(context);
			if (registerdEnrollment != null) {
				if (FBNUtil.checkInternet(context, ResultActivity.class)) {
					// instantiate custom adapter
						JSONArray arr = FBNUtil.getJSONArraytList(FBNConstants.RESULT_LIST, context);
							List<Result> resultList = null;
							if (arr != null && arr.length() > 0) {
								resultList = new ArrayList<Result>(arr.length());
								try {
									for (int i = 0; i < arr.length(); i++) {
										Result result = FBNUtil.convertResultRequest(arr.getJSONObject(i));
										resultList.add(result);
									}
								} catch (JSONException e) {

									e.printStackTrace();
								}
								ResultListCustomAdapter adapter = new ResultListCustomAdapter(resultList, context,
										this);
								listview.setAdapter(adapter);
								FBNUtil.setListBackground(listview);
							}
					
						ResultListRequestTask eligibleEventRequestTask = new ResultListRequestTask(context,
								registerdEnrollment, listview, this);
						eligibleEventRequestTask.execute();
					} else {
						FBNUtil.showWSErrorDetails(context);
					}
				} else {
					FBNUtil.showInternetErrorDetails(context);
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
