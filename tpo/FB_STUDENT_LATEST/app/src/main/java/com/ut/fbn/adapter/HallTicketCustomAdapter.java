package com.ut.fbn.adapter;

import java.util.ArrayList;
import java.util.List;

import com.ut.fbn.CompanyDetails;
import com.ut.fbn.R;
import com.ut.pojo.EligibleHallTicket;
import com.ut.pojo.HallTicket;
import com.ut.util.DBController;
import com.ut.util.FBNUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public class HallTicketCustomAdapter extends BaseAdapter implements ListAdapter {
	private List<HallTicket> list = new ArrayList<HallTicket>();
	private Context context;
	private boolean fromFlag;
	private Activity parentActivity;

	public HallTicketCustomAdapter(List<HallTicket> list, Context context, boolean fromFlag, Activity parentActivity) {
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
		return list.get(pos).getHallTicketId();
		// just return 0 if your list items do not have an Id variable.
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.eventlistviewadpter, null);
		}
		HallTicket hallTicket = list.get(position);

		FBNUtil.setCompanyDetail(view, hallTicket);

		final DBController controller = new DBController(context);
		// Handle buttons and add onClickListeners
		Button readedButton = (Button) view.findViewById(R.id.readed_btn);
		// readedButton.setBackgroundColor(Color.RED);
		if (fromFlag) {
			readedButton.setVisibility(View.VISIBLE);
			readedButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final HallTicket hallTicket = list.get(position);
					controller.insertEventRequest(hallTicket);
					list.remove(hallTicket);
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
					final HallTicket hallTicket = list.get(position);
					controller.deleteEventRequest(hallTicket);
					list.remove(hallTicket);
					notifyDataSetChanged();
				}
			});
		} else {
			deleteButton.setVisibility(View.GONE);
		}

		LinearLayout eligibleLayout = (LinearLayout) view.findViewById(R.id.eligibleLayout);

		eligibleLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CompanyDetails.hallTicket = list.get(position);
				navigateToCompanyDetailsActivity();
			}
		});

		Button showDetails = (Button) view.findViewById(R.id.showDetails);
		showDetails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CompanyDetails.hallTicket = list.get(position);
				navigateToCompanyDetailsActivity();
			}
		});

		return view;
	}

	public void navigateToCompanyDetailsActivity() {
		Intent homeIntent = new Intent(context, CompanyDetails.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		parentActivity.startActivity(homeIntent);
	}

}