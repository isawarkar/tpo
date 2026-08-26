package com.ut.fbn.adapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ut.fbn.R;
import com.ut.pojo.Result;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * This Adapter class is used to created a result list.
 * 
 * @author Indrajeet Sawarkar
 * 
 *
 */
public class ResultListCustomAdapter extends BaseAdapter implements ListAdapter {
	private List<Result> list = new ArrayList<Result>();
	private Context context;
	private Activity parentActivity;

	public ResultListCustomAdapter(List<Result> list, Context context, Activity parentActivity) {
		this.list = list;
		this.context = context;
		this.parentActivity = parentActivity;
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
			view = inflater.inflate(R.layout.resultviewadpter, null);
		}

		// Handle TextView and display string from your list
		TextView listItemText = (TextView) view.findViewById(R.id.list_item_string);
		Result result = list.get(position);
		listItemText.setText("Test Name:" + result.getTestName() + "\nResult:" + result.getResult());
		listItemText.setTextColor(FBNConstants.FONT_COLOR);

		// Handle buttons and add onClickListeners
		Button downlodCert = (Button) view.findViewById(R.id.downlodCert);
		if (result.getCertificateAvialable()) {
			downlodCert.setVisibility(View.VISIBLE);
			downlodCert.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
							Result result = list.get(position);
							String enrollmentNo = result.getLoginname();
							String testName = result.getTestName();
							String percent = result.getTotalnumbers();
							String fineName = "Certificate_" + testName + "_" + enrollmentNo + "_" + Double.valueOf(percent)
									+ ".PDF";
							File file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fineName);
							if (file != null && !file.exists()) {
								FileOutputStream fos = new FileOutputStream(file);
								fos.write(result.getCertificate());
								fos.close();
							}
							FBNUtil.showProgress(parentActivity);
							Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
							Intent share = new Intent();
							share.setAction(Intent.ACTION_SEND);
							share.setType("application/pdf");
							share.putExtra(Intent.EXTRA_STREAM, uri);
							share.putExtra(Intent.EXTRA_TEXT, "Certificate of " + enrollmentNo);
							share.putExtra(Intent.EXTRA_SUBJECT, "Certificate of " + enrollmentNo);
							share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
							parentActivity.startActivity(Intent.createChooser(share, "Share File"));
							FBNUtil.hideProgress(parentActivity);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			});
		} else {
			downlodCert.setVisibility(View.INVISIBLE);
		}

		Button showdetails = (Button) view.findViewById(R.id.showDetails);
		showdetails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Result result = list.get(position);
				FBNUtil.showNoticeDetails(result.toString(), "Result Complete detail's", parentActivity);

			}
		});

		return view;
	}
}