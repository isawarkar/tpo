package com.ut.adapter;

import java.util.ArrayList;
import java.util.List;

import com.ut.fbn.admin.R;
import com.ut.fbn.admin.CompanyDetails;
import com.ut.fbn.admin.OpeningListActivity;

import com.ut.fbn.admin.task.DeleteTask;
import com.ut.fbn.admin.util.FBNUtil;
import com.ut.pojo.Company;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
public class CompanyCustomAdapter extends BaseAdapter implements ListAdapter {
	private List<Company> list = new ArrayList<Company>();
	private Context context;
	private Activity parentActivity;

	public CompanyCustomAdapter(List<Company> list, Context context, Activity parentActivity) {
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
		return list.get(pos).getCompanyID();
		// just return 0 if your list items do not have an Id variable.
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		try {
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.companylistviewadapter, null);
			}

			// Handle TextView and display string from your list
			final Company company = list.get(position);
			
			ImageView companyLogo = (ImageView) view.findViewById(R.id.companyLogo);

			Bitmap bitmap = BitmapFactory.decodeByteArray(company.getLogo(), 0,
					company.getLogo().length);
			companyLogo.setImageBitmap(bitmap);
			
			TextView companyName = (TextView) view.findViewById(R.id.companyName);
			companyName.setText(company.getCompanyname());
			
			TextView companyID = (TextView) view.findViewById(R.id.cId);
			companyID.setText(String.valueOf(company.getCompanyID()));
			
			Button showDetails = (Button) view.findViewById(R.id.showDetails);
			
			showDetails.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CompanyDetails.company = company;
					navigateToCompanyDetailsActivity();
				}
			});
			
			Button showOppenings = (Button) view.findViewById(R.id.showOppenings);
			showOppenings.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					OpeningListActivity.companyID = company.getCompanyID();
					navigateToOpeningListActivity();
				}
			});
			
			Button deleteCompany = (Button) view.findViewById(R.id.deleteCompany);
			
			deleteCompany.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showConfirmationDialog(list.get(position));
				}
			});

			return view;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void navigateToCompanyDetailsActivity() {
		Intent homeIntent = new Intent(context, CompanyDetails.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		parentActivity.startActivity(homeIntent);
	}

	public void navigateToOpeningListActivity() {
		Intent homeIntent = new Intent(context, OpeningListActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		parentActivity.startActivity(homeIntent);
	}
	
	private void showConfirmationDialog(final Company company) {
		AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
		builder.setTitle("Confirmaation");
		builder.setMessage("Are you sure to Delete?");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				try {
					if (FBNUtil.checkInternet(context, CompanyCustomAdapter.class)) {
						DeleteTask deleteTask = new DeleteTask(context, String.valueOf(company.getCompanyID()), null, parentActivity);
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