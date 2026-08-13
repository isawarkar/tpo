package com.ut.fbn.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ut.fbn.R;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

public class DownloadedDocsCustomAdapter extends BaseAdapter implements ListAdapter {
	private List<String> list = new ArrayList<String>();
	private Context context;
	private Activity activity;

	public DownloadedDocsCustomAdapter(Activity activity,Context context, List<String> list) {
		this.context = context;
		this.list = list;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int pos) {
		return list.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return 0;
		// just return 0 if your list items do not have an Id variable.
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.downloadeddocslistviewadpter, null);
		}

		// Handle TextView and display string from your list
		TextView listItemText = (TextView) view.findViewById(R.id.list_item_string);
		final String fileName = list.get(position);
		listItemText.setText(fileName);
		listItemText.setTextColor(FBNConstants.FONT_COLOR);
		// Handle buttons and add onClickListeners
		Button openButton = (Button) view.findViewById(R.id.open_btn);
		// readedButton.setBackgroundColor(Color.RED);
		openButton.setVisibility(View.VISIBLE);
		openButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shareFile(position, fileName);
			}
		});

		Button deleteButton = (Button) view.findViewById(R.id.delete_btn);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String fileName = list.get(position);
				File file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fileName);
				if (file != null && file.exists()) {
					file.delete();
					list.remove(fileName);
					notifyDataSetChanged();
				}
			}
		});

		Button shareButton = (Button) view.findViewById(R.id.shareImage_btn);
		shareButton.setVisibility(View.VISIBLE);
		shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (FBNUtil.checkInternet(context, EligibleHallTicketCustomAdapter.class)) {
						shareFile(position, fileName);
					} else {
						FBNUtil.showInternetErrorDetails(context);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return view;
	}
	private void shareFile(final int position, final String fileName) {
		String fineName = list.get(position);
		File file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fineName);
		if (file != null && file.exists()) {
			String arry[] = fileName.split("\\.") ;
			Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
			Intent share = new Intent();
			share.setAction(Intent.ACTION_SEND);
			share.setType("application/"+arry[1]);
			share.putExtra(Intent.EXTRA_STREAM, uri);
			share.putExtra(Intent.EXTRA_TEXT, fineName);
			share.putExtra(Intent.EXTRA_SUBJECT, fineName);
			share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			activity.startActivity(Intent.createChooser(share, "Share File"));
		}
	}
}