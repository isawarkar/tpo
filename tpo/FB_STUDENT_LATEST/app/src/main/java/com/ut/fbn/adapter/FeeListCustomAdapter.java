package com.ut.fbn.adapter;

import java.util.ArrayList;
import java.util.List;

import com.ut.fbn.R;
import com.ut.pojo.FeeReminder;
import com.ut.util.DBController;
import com.ut.util.FBNConstants;
import com.ut.util.FBNUtil;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

public class FeeListCustomAdapter extends BaseAdapter implements ListAdapter {
	private List<FeeReminder> list = new ArrayList<FeeReminder>();
	private Context context;
	private boolean fromFlag;
	private Activity parentActivity;

	public FeeListCustomAdapter(List<FeeReminder> list, Context context, boolean fromFlag, Activity parentActivity) {
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
			view = inflater.inflate(R.layout.feelistviewadpter, null);
		}

		// Handle TextView and display string from your list
		TextView listItemText = (TextView) view.findViewById(R.id.list_item_string);
		final FeeReminder feeReminder = list.get(position);
		listItemText.setTextColor(FBNConstants.FONT_COLOR);
		
		listItemText.setText("Amount Due : " +  feeReminder.getAmountDue() +"\nDue On:"+feeReminder.getDueOn());

		final DBController controller = new DBController(context);
		// Handle buttons and add onClickListeners
		Button readedButton = (Button) view.findViewById(R.id.readed_btn);
		// readedButton.setBackgroundColor(Color.RED);
		if (fromFlag) {
			readedButton.setVisibility(View.VISIBLE);
			readedButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final FeeReminder feeReminder = list.get(position);
					controller.insertFeeRequest(feeReminder);
					list.remove(feeReminder);
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
				final FeeReminder feeReminder = list.get(position);
				try {
					controller.deleteFeeRequest(feeReminder);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				list.remove(feeReminder);
				notifyDataSetChanged();
			}
		});
		}else {
			deleteButton.setVisibility(View.GONE);
		}

		Button showDetails = (Button) view.findViewById(R.id.showDetails);
		showDetails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final FeeReminder feeReminder = list.get(position);
				try {
					FBNUtil.showNoticeDetails(feeReminder.toString(), "Fee Complete detail's", parentActivity);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		});

		return view;
	}
	
}