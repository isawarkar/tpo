package com.ut.adapter;

import java.util.ArrayList;
import java.util.List;

import com.ut.fbn.admin.CompanyDetails;
import com.ut.fbn.admin.OpeningListActivity;
import com.ut.fbn.admin.R;
import com.ut.fbn.admin.StudentListActivity;
import com.ut.fbn.admin.task.DeleteTask;
import com.ut.fbn.admin.util.FBNUtil;
import com.ut.pojo.Company;
import com.ut.pojo.HallTicket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * This Adapter class is used to created a eligible list.
 * 
 * @author Indrajeet Sawarkar
 * 
 *
 */
public class OpeningListCustomAdapter extends BaseAdapter implements ListAdapter {
	private List<HallTicket> list = new ArrayList<HallTicket>();
	private Context context;
	private Activity parentActivity;

	public OpeningListCustomAdapter(List<HallTicket> list, Context context, Activity parentActivity) {
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
		return list.get(pos).getHallTicketId();
		// just return 0 if your list items do not have an Id variable.
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		try {
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.openinglistviewadapter, null);
			}

			// Handle TextView and display string from your list
			final HallTicket hall = list.get(position);

			TextView hallTicketId = (TextView) view.findViewById(R.id.hallTicketId);
			hallTicketId.setText(String.valueOf(hall.getHallTicketId()));

			TextView date = (TextView) view.findViewById(R.id.date);
			date.setText(hall.getDate());

			TextView time = (TextView) view.findViewById(R.id.time);
			time.setText(hall.getTime());

			TextView packageOffering = (TextView) view.findViewById(R.id.packageOffering);
			packageOffering.setText(hall.getPackageOffering());

			TextView lastDateToApply = (TextView) view.findViewById(R.id.lastDateToApply);
			lastDateToApply.setText(hall.getLastDateToApply());

			TextView interviewLocation = (TextView) view.findViewById(R.id.interviewLocation);
			interviewLocation.setText(hall.getInterviewLocation());

			TextView postingLocation = (TextView) view.findViewById(R.id.postingLocation);
			postingLocation.setText(hall.getPostingLocation());

			TextView role = (TextView) view.findViewById(R.id.role);
			role.setText(hall.getRole());

			TextView criteria = (TextView) view.findViewById(R.id.criteria);
			criteria.setText(Html.fromHtml(hall.getCriteria()));

			TextView applied = (TextView) view.findViewById(R.id.applied);
			String text = hall.getTotalApplied();
			applied.setText(Html.fromHtml(text));

			TextView approved = (TextView) view.findViewById(R.id.approved);
			approved.setText(Html.fromHtml(hall.getTotalApproved()));

			TextView arrived = (TextView) view.findViewById(R.id.arrived);
			arrived.setText(Html.fromHtml(hall.getTotalArrived()));

			TextView shortlisted = (TextView) view.findViewById(R.id.shortlisted);
			shortlisted.setText(Html.fromHtml(hall.getTotalShortlisted()));

			Button showStudents = (Button) view.findViewById(R.id.showStudents);
			if (text != null && !text.startsWith("<font color='red' size='20'>0")) {
				showStudents.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						StudentListActivity.hallticketId = hall.getHallTicketId();
						navigateToStudentListActivity();
					}
				});
				showStudents.setBackgroundColor(Color.GREEN);
			} else {
				showStudents.setBackgroundColor(Color.RED);
				showStudents.setText("No Student Applied");
			}

			Button deleteOpening = (Button) view.findViewById(R.id.deleteOpening);

			deleteOpening.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showConfirmationDialog(list.get(position));
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return view;
	}

	public void navigateToCompanyDetailsActivity() {
		Intent homeIntent = new Intent(context, CompanyDetails.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		parentActivity.startActivity(homeIntent);
	}

	public void navigateToStudentListActivity() {
		Intent homeIntent = new Intent(context, StudentListActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		parentActivity.startActivity(homeIntent);
	}

	private void showConfirmationDialog(final HallTicket hallTicket) {
		AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
		builder.setTitle("Confirmaation");
		builder.setMessage("Are you sure to Delete?");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				try {
					if (FBNUtil.checkInternet(context, OpeningListCustomAdapter.class)) {
						DeleteTask deleteTask = new DeleteTask(context, null,
								String.valueOf(hallTicket.getHallTicketId()), parentActivity);
						deleteTask.execute();
						notifyDataSetChanged();
					} else {
						FBNUtil.showInternetErrorDetails(parentActivity);
					}
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

}