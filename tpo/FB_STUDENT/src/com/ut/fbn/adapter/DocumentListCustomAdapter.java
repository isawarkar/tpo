package com.ut.fbn.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ut.fbn.R;
import com.ut.pojo.Document;
import com.ut.task.DownloadDocuemntTask;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;
import com.ut.util.WSURL;

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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * This Adapter class is used to created a result list.
 * 
 * @author Indrajeet Sawarkar
 * 
 *
 */
public class DocumentListCustomAdapter extends BaseAdapter implements ListAdapter {
	private List<Document> list = new ArrayList<Document>();
	private Context context;
	private Activity parentActivity;

	public DocumentListCustomAdapter(List<Document> list, Context context, Activity parentActivity) {
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
			view = inflater.inflate(R.layout.documentviewadpter, null);
		}

		// Handle TextView and display string from your list
		TextView listItemText = (TextView) view.findViewById(R.id.list_item_string);
		Document document = list.get(position);
		listItemText.setText("Document Name:" + document.getDocumentName());
		listItemText.setTextColor(FBNConstants.FONT_COLOR);
		
		String enNo = FBNUtil.getRegisteredUserName(context);
		ImageView image = (ImageView) view.findViewById(R.id.docImage);
		String fileName = FBNConstants.FBN_DOWNLOAD_DIR + enNo+"_"+document.getDocumentName()+".jpeg";
		File file = new File(fileName); // Creating an internal dir;
		if (file.exists()) {
			Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
			image.setImageURI(uri);
		}

		// Handle buttons and add onClickListeners
		Button downlodCert = (Button) view.findViewById(R.id.downlodCert);
		downlodCert.setVisibility(View.VISIBLE);
		//downlodCert.setBackground(Drawable.createFromPath(fileName));
		downlodCert.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (FBNUtil.checkInternet(context, EligibleHallTicketCustomAdapter.class)) {
					Document document = list.get(position);
					String registerdEnrollment = FBNUtil.getRegisteredUserName(context);
					String documentName = document.getDocumentName();
					String fileName = registerdEnrollment + "_" + documentName+ ".jpeg";
					File file = new File(FBNConstants.FBN_DOWNLOAD_DIR + fileName);
					if (file != null && file.exists()) {
						FBNUtil.showProgress(parentActivity);
						Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
						Intent share = new Intent();
						share.setAction(Intent.ACTION_SEND);
						share.setType("application/jpeg");
						share.putExtra(Intent.EXTRA_STREAM, uri);
						share.putExtra(Intent.EXTRA_TEXT, "Document of " + registerdEnrollment+"\n Document Name:" +fileName);
						share.putExtra(Intent.EXTRA_SUBJECT, "Document of " + registerdEnrollment+"\n Document Name:" +fileName);
						share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						parentActivity.startActivity(Intent.createChooser(share, "Share File"));
						FBNUtil.hideProgress(parentActivity);
					} else {
							DownloadDocuemntTask downloadRegistrationFormTask = new DownloadDocuemntTask(registerdEnrollment,documentName,
									context,parentActivity);
							downloadRegistrationFormTask.execute();
					}
				} else {
					FBNUtil.showInternetErrorDetails(parentActivity);
				}
			}

		});

		Button showdetails = (Button) view.findViewById(R.id.showDetails);
		showdetails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Document document = list.get(position);
				FBNUtil.showNoticeDetails(document.toString(), "Document Complete detail's", parentActivity);

			}
		});

		return view;
	}
}