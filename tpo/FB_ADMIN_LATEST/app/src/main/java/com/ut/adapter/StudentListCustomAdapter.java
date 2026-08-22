package com.ut.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.ut.fbn.admin.R;
import com.ut.fbn.admin.CompanyDetails;
import com.ut.fbn.admin.StudentListActivity;
import com.ut.fbn.admin.task.ApproveOrRejectTask;
import com.ut.fbn.admin.task.CompanyListRequestTask;
import com.ut.pojo.Student;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * This Adapter class is used to created a eligible list.
 * 
 * @author Indrajeet Sawarkar
 * 
 *
 */
public class StudentListCustomAdapter extends BaseAdapter implements ListAdapter {
	private List<Student> list = new ArrayList<Student>();
	private Context context;
	private Activity parentActivity;
	Integer hallticketID;

	public StudentListCustomAdapter(List<Student> list, Context context, Activity parentActivity,Integer hallticketID) {
		this.list = list;
		this.context = context;
		this.parentActivity = parentActivity;
		this.hallticketID = hallticketID;
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
		try {
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.studentlistviewadapter, null);
			}

			// Handle TextView and display string from your list
			final Student student = list.get(position);

			TextView rollnumber = (TextView) view.findViewById(R.id.rollnumber);
			rollnumber.setText(String.valueOf(student.getRollnumber()));

			TextView appliedOn = (TextView) view.findViewById(R.id.appliedOn);
			appliedOn.setText(student.getAppliedOn());

			TextView approvedOn = (TextView) view.findViewById(R.id.approvedOn);
			approvedOn.setText(student.getApprovedOn());

			Button approve = (Button) view.findViewById(R.id.approve);
			Button deny = (Button) view.findViewById(R.id.deny);
			if (student.getIsApplied() && !student.getIsApproved()) {
				approve.setVisibility(View.VISIBLE);
				deny.setVisibility(View.GONE);
				approve.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							JSONObject registerObject = new JSONObject();
							registerObject.put("status", "true");
							registerObject.put("rollNumber",student.getRollnumber());
							registerObject.put("hallticketId", hallticketID);
							ApproveOrRejectTask approveOrRejectTask = new ApproveOrRejectTask(context,registerObject, parentActivity);
							approveOrRejectTask.execute();
							notifyDataSetChanged();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}

			if (student.getIsApproved()) {
				deny.setVisibility(View.VISIBLE);
				approve.setVisibility(View.GONE);
				deny.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							JSONObject registerObject = new JSONObject();
							registerObject.put("status", "false");
							registerObject.put("rollNumber",student.getRollnumber());
							registerObject.put("hallticketId", hallticketID);
							ApproveOrRejectTask approveOrRejectTask = new ApproveOrRejectTask(context,registerObject, parentActivity);
							approveOrRejectTask.execute();
							notifyDataSetChanged();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
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

}