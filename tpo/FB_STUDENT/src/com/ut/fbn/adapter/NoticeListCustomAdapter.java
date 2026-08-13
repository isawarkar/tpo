package com.ut.fbn.adapter;

import java.util.ArrayList;
import java.util.List;

import com.ut.fbn.AttachmentActivity;
import com.ut.fbn.R;
import com.ut.pojo.Notice;
import com.ut.util.DBController;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

public class NoticeListCustomAdapter extends BaseAdapter implements ListAdapter {
	private List<Notice> list = new ArrayList<Notice>();
	private Context context;
	private boolean fromFlag;
	private Activity parentActivity;

	public NoticeListCustomAdapter(List<Notice> list, Context context, boolean fromFlag, Activity parentActivity) {
		this.list = list;
		this.context = context;
		this.fromFlag = fromFlag;
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
			view = inflater.inflate(R.layout.noticeviewadpter, null);
		}

		// Handle TextView and display string from your list
		TextView listItemText = (TextView) view.findViewById(R.id.list_item_string);
		final Notice notice = list.get(position);
		listItemText.setText(notice.getNoticeName());
		if ("YES".equals(notice.getExpired()) || notice.isImpTag())
			listItemText.setTextColor(Color.RED);
		else
			listItemText.setTextColor(FBNConstants.FONT_COLOR);

		TextView attachment1 = (TextView) view.findViewById(R.id.attachment1);
		if (notice.getFileName1() != null && !"null".equals(notice.getFileName1())) {
			attachment1.setText(notice.getFileName1());
			attachment1.setVisibility(View.VISIBLE);
			attachment1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AttachmentActivity.fileNameStr = notice.getFileName1();
					AttachmentActivity.fileString = notice.getFile1();
					navigateToAttachemtActivity();
				}
			});
		} else {
			attachment1.setVisibility(View.GONE);
		}

		TextView attachment2 = (TextView) view.findViewById(R.id.attachment2);
		if (notice.getFileName2() != null && !"null".equals(notice.getFileName2())) {
			attachment2.setText(notice.getFileName2());
			attachment2.setVisibility(View.VISIBLE);
			attachment2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AttachmentActivity.fileNameStr = notice.getFileName2();
					AttachmentActivity.fileString = notice.getFile2();
					navigateToAttachemtActivity();
				}
			});
		} else {
			attachment2.setVisibility(View.GONE);
		}

		TextView attachment3 = (TextView) view.findViewById(R.id.attachment3);
		if (notice.getFileName3() != null && !"null".equals(notice.getFileName3())) {
			attachment3.setText(notice.getFileName3());
			attachment3.setVisibility(View.VISIBLE);
			attachment3.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AttachmentActivity.fileNameStr = notice.getFileName3();
					AttachmentActivity.fileString = notice.getFile3();
					navigateToAttachemtActivity();
				}
			});
		} else {
			attachment3.setVisibility(View.GONE);
		}

		TextView attachment4 = (TextView) view.findViewById(R.id.attachment4);
		if (notice.getFileName4() != null && !"null".equals(notice.getFileName4())) {
			attachment4.setText(notice.getFileName4());
			attachment4.setVisibility(View.VISIBLE);
			attachment4.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AttachmentActivity.fileNameStr = notice.getFileName4();
					AttachmentActivity.fileString = notice.getFile4();
					navigateToAttachemtActivity();
				}
			});
		} else {
			attachment4.setVisibility(View.GONE);
		}

		TextView attachment5 = (TextView) view.findViewById(R.id.attachment5);
		if (notice.getFileName5() != null && !"null".equals(notice.getFileName5())) {
			attachment5.setText(notice.getFileName5());
			attachment5.setVisibility(View.VISIBLE);
			attachment5.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
						AttachmentActivity.fileNameStr = notice.getFileName5();
						AttachmentActivity.fileString = notice.getFile5();
						navigateToAttachemtActivity();
				}
			});
		} else {
			attachment5.setVisibility(View.GONE);
		}

		final DBController controller = new DBController(context);
		// Handle buttons and add onClickListeners
		Button readedButton = (Button) view.findViewById(R.id.readed_btn);
		// readedButton.setBackgroundColor(Color.RED);
		if (fromFlag) {
			readedButton.setVisibility(View.VISIBLE);
			readedButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final Notice notice = list.get(position);
					controller.insertNoticeRequest(notice);
					list.remove(notice);
					notifyDataSetChanged();
				}
			});
		} else {
			readedButton.setVisibility(View.INVISIBLE);
		}

		Button deleteButton = (Button) view.findViewById(R.id.delete_btn);
		if (!fromFlag) {
		deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Notice notice = list.get(position);
				try {
					controller.deleteNoticeRequest(notice);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				list.remove(notice);
				notifyDataSetChanged();
			}
		});
		} else {
			deleteButton.setVisibility(View.GONE);
		}

		Button showDetails = (Button) view.findViewById(R.id.showDetails);
		showDetails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Notice notice = list.get(position);
				try {
					FBNUtil.showNoticeDetails(notice.toString(), "Notice Complete detail's", parentActivity);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		});

		return view;
	}
	
	
	public void navigateToAttachemtActivity() {
		Intent homeIntent = new Intent(context, AttachmentActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		parentActivity.startActivity(homeIntent);
	}
}